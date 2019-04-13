package de.embl.cba.tables.ui;

import de.embl.cba.bdv.utils.wrap.Wraps;
import de.embl.cba.tables.Calibrations;
import de.embl.cba.tables.Logger;
import de.embl.cba.tables.TableColumns;
import de.embl.cba.tables.modelview.images.DefaultImageSourcesModel;
import de.embl.cba.tables.modelview.images.ImageSourcesModel;
import de.embl.cba.tables.modelview.images.SourceMetadata;
import de.embl.cba.tables.modelview.segments.SegmentProperty;
import de.embl.cba.tables.modelview.segments.SegmentUtils;
import de.embl.cba.tables.modelview.segments.TableRowImageSegment;
import de.embl.cba.tables.modelview.views.combined.SegmentsTableAndBdvViews;
import de.embl.cba.tables.modelview.views.combined.SegmentsTableBdvAnd3dViews;
import ij.ImagePlus;
import ij.WindowManager;
import ij.text.TextWindow;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static de.embl.cba.tables.modelview.segments.SegmentUtils.*;


@Plugin(type = Command.class, initializer = "init",
		menuPath = "Plugins>Segmentation>Explore>Explore MorphoLibJ Segmentation" )
public class ExploreMorphoLibJCommand< R extends RealType< R > & NativeType< R > > implements Command
{
	public static final String LABEL = "Label";
	private static final String COLUMN_NAME_LABEL_IMAGE_ID = "LabelImage";
	public static final String CENTROID_X = "Centroid.X";
	public static final String CENTROID_Y = "Centroid.Y";
	public static final String CENTROID_Z = "Centroid.Z";
	public static final String MEAN_BREADTH = "MeanBreadth";

	@Parameter ( label = "Intensity image", required = false )
	public ImagePlus intensityImage;

	@Parameter ( label = "Label mask image" )
	public ImagePlus labelImage;

	@Parameter ( label = "Results table title" )
	public String resultsTableTitle;

	private ij.measure.ResultsTable resultsTable;
	private LinkedHashMap< String, List< ? > > columns;
	private int numSpatialDimensions;
	private String labelImageId;
	private HashMap< String, ij.measure.ResultsTable > titleToResultsTable;

	@Override
	public void run()
	{
		numSpatialDimensions = labelImage.getNSlices() > 1 ? 3 : 2;

		labelImageId = labelImage.getTitle();

		resultsTable = titleToResultsTable.get( resultsTableTitle );

		if ( resultsTable == null )
		{
			throwResultsTableNotFoundError();
			return;
		}

		final List< TableRowImageSegment > tableRowImageSegments
				= createMLJTableRowImageSegments( resultsTable );

		final ImageSourcesModel imageSourcesModel = createImageSourcesModel();

		if ( numSpatialDimensions == 2 )
		{
			new SegmentsTableAndBdvViews(
					tableRowImageSegments,
					imageSourcesModel,
					resultsTableTitle );
		}
		else
		{
			new SegmentsTableBdvAnd3dViews(
					tableRowImageSegments,
					imageSourcesModel,
					resultsTableTitle );
		}
	}

	public void throwResultsTableNotFoundError()
	{
		String error = "Results table not found: " + resultsTableTitle + "\n";
		error += "\n";
		error += "Please choose from:\n";
		for ( String title : titleToResultsTable.keySet() )
		{
			error += "- " + title + "\n";
		}
		Logger.error( error  );
	}

	private DefaultImageSourcesModel createImageSourcesModel()
	{
		final DefaultImageSourcesModel imageSourcesModel =
				new DefaultImageSourcesModel( numSpatialDimensions == 2 );

		Logger.info( "Adding to image sources: " + labelImageId );

		imageSourcesModel.addSourceAndMetadata(
				Wraps.imagePlusAsSource4DChannelList( labelImage ).get( 0 ),
				labelImageId,
				SourceMetadata.Flavour.LabelSource,
				numSpatialDimensions,
				Calibrations.getScalingTransform( labelImage ),
				new File("") // TODO: If this was null BdvSegmentsView thinks there is no table at all...
		);

		imageSourcesModel.sources().get( labelImageId ).metadata().showInitially = true;

		if ( intensityImage != labelImage )
		{
			final String intensityImageId = intensityImage.getTitle();

			Logger.info( "Adding to image sources: " + intensityImageId );

			imageSourcesModel.addSourceAndMetadata(
					Wraps.imagePlusAsSource4DChannelList( intensityImage ).get( 0 ),
					intensityImageId,
					SourceMetadata.Flavour.IntensitySource,
					numSpatialDimensions,
					Calibrations.getScalingTransform( intensityImage ),
					null
			);

			imageSourcesModel.sources().get( labelImageId )
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

	private List< TableRowImageSegment > createMLJTableRowImageSegments(
			ij.measure.ResultsTable resultsTable )
	{
		columns = TableColumns.columnsFromImageJ1ResultsTable( resultsTable );

		columns = TableColumns.addLabelImageIdColumn(
				columns,
				COLUMN_NAME_LABEL_IMAGE_ID,
				labelImageId );

		if ( numSpatialDimensions == 3 )
		{
			columns = addBoundingBoxColumn( CENTROID_X, BB_MIN_X, true );
			columns = addBoundingBoxColumn( CENTROID_Y, BB_MIN_Y, true );
			columns = addBoundingBoxColumn( CENTROID_Z, BB_MIN_Z, true );
			columns = addBoundingBoxColumn( CENTROID_X, BB_MAX_X, false );
			columns = addBoundingBoxColumn( CENTROID_Y, BB_MAX_Y, false );
			columns = addBoundingBoxColumn( CENTROID_Z, BB_MAX_Z, false );
		}

		final HashMap< SegmentProperty, List< ? > > segmentPropertyToColumn
				= createSegmentPropertyToColumnMap();

		final List< TableRowImageSegment > segments
				= SegmentUtils.tableRowImageSegmentsFromColumns(
						columns,
						segmentPropertyToColumn,
						true );

		return segments;
	}

	private LinkedHashMap< String, List<?>> addBoundingBoxColumn(
			String centroid,
			String bb,
			boolean min
	)
	{
		final int numRows = columns.values().iterator().next().size();

		final List< Object > column = new ArrayList<>();
		for ( int row = 0; row < numRows; row++ )
		{
			final double centre = Double.parseDouble(
					columns.get( centroid ).get( row ).toString() );

			final double meanBreadth = Double.parseDouble(
					columns.get( MEAN_BREADTH ).get( row ).toString() );

			if ( min )
				column.add( (long) ( centre - 0.5 * meanBreadth ) );
			else
				column.add( (long) ( centre + 0.5 * meanBreadth ) );
		}

		columns.put( bb, column );

		return columns;

	}


	private HashMap< SegmentProperty, List< ? > > createSegmentPropertyToColumnMap( )
	{
		final HashMap< SegmentProperty, List< ? > > segmentPropertyToColumn
				= new HashMap<>();

		segmentPropertyToColumn.put(
				SegmentProperty.LabelImage,
				columns.get( COLUMN_NAME_LABEL_IMAGE_ID ));

		segmentPropertyToColumn.put(
				SegmentProperty.ObjectLabel,
				columns.get( LABEL ) );

		segmentPropertyToColumn.put(
				SegmentProperty.X,
				columns.get( CENTROID_X ) );

		segmentPropertyToColumn.put(
				SegmentProperty.Y,
				columns.get( CENTROID_Y ) );

		if ( numSpatialDimensions == 3 )
		{
			segmentPropertyToColumn.put(
					SegmentProperty.Z,
					columns.get( CENTROID_Z ) );

			SegmentUtils.putDefaultBoundingBoxMapping( segmentPropertyToColumn, columns );
		}

		return segmentPropertyToColumn;
	}


}
