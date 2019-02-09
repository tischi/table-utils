package de.embl.cba.tables.ui;

import de.embl.cba.bdv.utils.wrap.Wraps;
import de.embl.cba.tables.TableColumns;
import de.embl.cba.tables.modelview.images.DefaultImageSourcesModel;
import de.embl.cba.tables.modelview.images.SourceMetadata;
import de.embl.cba.tables.modelview.segments.ColumnBasedTableRowImageSegment;
import de.embl.cba.tables.modelview.segments.ImageSegmentCoordinate;
import de.embl.cba.tables.modelview.segments.SegmentUtils;
import de.embl.cba.tables.modelview.views.DefaultBdvAndTableView;
import ij.ImagePlus;
import ij.WindowManager;
import ij.text.TextWindow;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import org.scijava.command.Command;
import org.scijava.command.DynamicCommand;
import org.scijava.module.MutableModuleItem;
import org.scijava.object.ObjectService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


@Plugin(type = Command.class, initializer = "init", menuPath = "Plugins>Segmentation>Explore>MorphoLibJ Segmentation" )
public class ExploreMorphoLibJSegmentationCommand< R extends RealType< R > & NativeType< R > >
		extends DynamicCommand
{

	@Parameter
	ObjectService objectService;

	private static final String COLUMN_NAME_LABEL_IMAGE_ID = "LabelImageId";

	@Parameter ( label = "Label mask image" )
	public ImagePlus labelMaskImagePlus;

	@Parameter ( label = "Intensity image (optional)", required = false)
	public ImagePlus intensityImagePlus;

	@Parameter ( label = "Results table" )
	public String resultsTableTitle;

	private ij.measure.ResultsTable resultsTable;

	private LinkedHashMap< String, List< Object > > columns;
	private int numSpatialDimensions;
	private String labelMaskId;
	private HashMap< String, ij.measure.ResultsTable > titleToResultsTable;

	@Override
	public void run()
	{
		labelMaskId = labelMaskImagePlus.getTitle();

		resultsTable = titleToResultsTable.get( resultsTableTitle );

		final List< ColumnBasedTableRowImageSegment > tableRowImageSegments
				= createAnnotatedImageSegments( resultsTable );

		final DefaultImageSourcesModel imageSourcesModel = new DefaultImageSourcesModel();

		numSpatialDimensions = labelMaskImagePlus.getNSlices() > 1 ? 3 : 2;

		imageSourcesModel.addSource(
				Wraps.imagePlusAsSource4DChannelList( labelMaskImagePlus ).get( 0 ),
				labelMaskId,
				SourceMetadata.Flavour.LabelSource,
				numSpatialDimensions
				);

		new DefaultBdvAndTableView( tableRowImageSegments, imageSourcesModel );

	}

	private void init()
	{
		fetchResultsTables();
		MutableModuleItem<String> input = getInfo().getMutableInput("resultsTableTitle", String.class );
		input.setChoices( new ArrayList<>( titleToResultsTable.keySet() ));
	}

	private void fetchResultsTables()
	{
		titleToResultsTable = new HashMap<>();
		final Frame[] nonImageWindows = WindowManager.getNonImageWindows();
		for ( Frame nonImageWindow : nonImageWindows )
		{
			if ( nonImageWindow instanceof TextWindow )
			{
				final TextWindow textWindow = ( TextWindow ) nonImageWindow;

				final ij.measure.ResultsTable resultsTable = textWindow.getResultsTable();

				if ( resultsTable != null )
				{
					titleToResultsTable.put( resultsTable.getTitle(), resultsTable );
				}
			}
		}
	}

	private List< ColumnBasedTableRowImageSegment > createAnnotatedImageSegments(
			ij.measure.ResultsTable resultsTable )
	{
		columns = TableColumns.columnsFromImageJ1ResultsTable( resultsTable );

		columns = TableColumns.addLabelImageIdColumn(
				columns,
				COLUMN_NAME_LABEL_IMAGE_ID,
				labelMaskId
		);

		final HashMap< ImageSegmentCoordinate, List< Object > > imageSegmentCoordinateToColumn
				= createImageSegmentCoordinateToColumn();

		final List< ColumnBasedTableRowImageSegment > segments
				= SegmentUtils.tableRowImageSegmentsFromColumns( columns, imageSegmentCoordinateToColumn );

		return segments;
	}

	private HashMap< ImageSegmentCoordinate, List< Object > > createImageSegmentCoordinateToColumn( )
	{
		final HashMap< ImageSegmentCoordinate, List< Object > > imageSegmentCoordinateToColumn
				= new HashMap<>();

		imageSegmentCoordinateToColumn.put(
				ImageSegmentCoordinate.ImageId,
				columns.get( COLUMN_NAME_LABEL_IMAGE_ID ));

		imageSegmentCoordinateToColumn.put(
				ImageSegmentCoordinate.LabelId,
				columns.get( "Label" ) );

		imageSegmentCoordinateToColumn.put(
				ImageSegmentCoordinate.X,
				columns.get( "Centroid.X" ) );

		imageSegmentCoordinateToColumn.put(
				ImageSegmentCoordinate.Y,
				columns.get( "Centroid.Y" ) );

		if ( numSpatialDimensions == 3 )
		{
			imageSegmentCoordinateToColumn.put(
					ImageSegmentCoordinate.Z,
					columns.get( "Centroid.Z" ) );
		}

		return imageSegmentCoordinateToColumn;
	}


}
