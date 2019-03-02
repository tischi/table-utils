package de.embl.cba.tables.ui;

import de.embl.cba.bdv.utils.wrap.Wraps;
import de.embl.cba.tables.Calibrations;
import de.embl.cba.tables.Logger;
import de.embl.cba.tables.TableColumns;
import de.embl.cba.tables.modelview.images.DefaultImageSourcesModel;
import de.embl.cba.tables.modelview.images.ImageSourcesModel;
import de.embl.cba.tables.modelview.images.SourceMetadata;
import de.embl.cba.tables.modelview.segments.ImageSegmentCoordinate;
import de.embl.cba.tables.modelview.segments.SegmentUtils;
import de.embl.cba.tables.modelview.segments.TableRowImageSegment;
import de.embl.cba.tables.modelview.views.DefaultTableAndBdvViews;
import ij.ImagePlus;
import ij.WindowManager;
import ij.text.TextWindow;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import org.scijava.command.Command;
import org.scijava.command.DynamicCommand;
import org.scijava.module.MutableModuleItem;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


@Plugin(type = Command.class, initializer = "init",
		menuPath = "Plugins>Segmentation>Explore>Explore MorphoLibJ Segmentation" )
public class ExploreMorphoLibJCommand< R extends RealType< R > & NativeType< R > > implements Command
{
	public static final String LABEL = "Label";
	private static final String COLUMN_NAME_LABEL_IMAGE_ID = "LabelImage";

	@Parameter ( label = "Label mask image" )
	public ImagePlus labelMask;

	@Parameter ( label = "Intensity image", required = false )
	public ImagePlus intensityImage;

	@Parameter ( label = "Results table title" )
	public String resultsTableTitle;

	private ij.measure.ResultsTable resultsTable;
	private LinkedHashMap< String, List< ? > > columns;
	private int numSpatialDimensions;
	private String labelMaskId;
	private HashMap< String, ij.measure.ResultsTable > titleToResultsTable;

	@Override
	public void run()
	{
		numSpatialDimensions = labelMask.getNSlices() > 1 ? 3 : 2;

		labelMaskId = labelMask.getTitle();

		resultsTable = titleToResultsTable.get( resultsTableTitle );

		if ( resultsTable == null )
		{
			String error = "Results table not found: " + resultsTableTitle + "\n";
			error += "\n";
			error += "Please choose from:\n";
			for ( String title : titleToResultsTable.keySet() )
			{
				error += "- " + title + "\n";
			}
			Logger.error( error  );
			return;
		}

		final List< TableRowImageSegment > tableRowImageSegments
				= createTableRowImageSegments( resultsTable );

		final ImageSourcesModel imageSourcesModel = createImageSourcesModel();

		final DefaultTableAndBdvViews views =
				new DefaultTableAndBdvViews( tableRowImageSegments, imageSourcesModel );

		views.getTableRowsTableView().categoricalColumnNames().add( LABEL );
	}

	private DefaultImageSourcesModel createImageSourcesModel()
	{
		final DefaultImageSourcesModel imageSourcesModel =
				new DefaultImageSourcesModel( numSpatialDimensions == 2 );

		Logger.info( "Adding to image sources: " + labelMaskId );

		imageSourcesModel.addSource(
				Wraps.imagePlusAsSource4DChannelList( labelMask ).get( 0 ),
				labelMaskId,
				SourceMetadata.Flavour.LabelSource,
				numSpatialDimensions,
				Calibrations.getScalingTransform( labelMask )
				);

		imageSourcesModel.sources().get( labelMaskId ).metadata().showInitially = true;

		if ( intensityImage != labelMask )
		{
			final String intensityImageId = intensityImage.getTitle();

			Logger.info( "Adding to image sources: " + intensityImageId );

			imageSourcesModel.addSource(
					Wraps.imagePlusAsSource4DChannelList( intensityImage ).get( 0 ),
					intensityImageId,
					SourceMetadata.Flavour.IntensitySource,
					numSpatialDimensions,
					Calibrations.getScalingTransform( intensityImage )
			);

			imageSourcesModel.sources().get( labelMaskId )
					.metadata().imageSetIDs.add( intensityImageId );
		}

		return imageSourcesModel;
	}

	private void init()
	{
		fetchResultsTables();
//		MutableModuleItem<String> input = getInfo().getMutableInput("resultsTableTitle", String.class );
//		input.setChoices( new ArrayList<>( titleToResultsTable.keySet() ));
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

	private List< TableRowImageSegment > createTableRowImageSegments(
			ij.measure.ResultsTable resultsTable )
	{
		columns = TableColumns.columnsFromImageJ1ResultsTable( resultsTable );

		columns = TableColumns.addLabelImageIdColumn(
				columns,
				COLUMN_NAME_LABEL_IMAGE_ID,
				labelMaskId );

		final HashMap< ImageSegmentCoordinate, List< ? > > imageSegmentCoordinateToColumn
				= createSegmentCoordinateToColumnMap();

		final List< TableRowImageSegment > segments
				= SegmentUtils.tableRowImageSegmentsFromColumns( columns, imageSegmentCoordinateToColumn, true );

		return segments;
	}

	private HashMap< ImageSegmentCoordinate, List< ? > > createSegmentCoordinateToColumnMap( )
	{
		final HashMap< ImageSegmentCoordinate, List< ? > > coordinateToColumn
				= new HashMap<>();

		coordinateToColumn.put(
				ImageSegmentCoordinate.LabelImage,
				columns.get( COLUMN_NAME_LABEL_IMAGE_ID ));

		coordinateToColumn.put(
				ImageSegmentCoordinate.ObjectLabel,
				columns.get( LABEL ) );

		coordinateToColumn.put(
				ImageSegmentCoordinate.X,
				columns.get( "Centroid.X" ) );

		coordinateToColumn.put(
				ImageSegmentCoordinate.Y,
				columns.get( "Centroid.Y" ) );

		if ( numSpatialDimensions == 3 )
		{
			coordinateToColumn.put(
					ImageSegmentCoordinate.Z,
					columns.get( "Centroid.Z" ) );
		}

		return coordinateToColumn;
	}


}
