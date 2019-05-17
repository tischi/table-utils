package de.embl.cba.tables;

import de.embl.cba.bdv.utils.wrap.Wraps;
import de.embl.cba.tables.image.DefaultImageSourcesModel;
import de.embl.cba.tables.image.Metadata;
import de.embl.cba.tables.imagesegment.SegmentProperty;
import de.embl.cba.tables.imagesegment.SegmentPropertyColumnsSelectionDialog;
import de.embl.cba.tables.imagesegment.SegmentUtils;
import de.embl.cba.tables.tablerow.TableRowImageSegment;
import de.embl.cba.tables.view.combined.SegmentsTableAndBdvViews;
import de.embl.cba.tables.view.combined.SegmentsTableBdvAnd3dViews;
import ij.IJ;
import ij.ImagePlus;

import java.io.File;
import java.util.*;

import static de.embl.cba.tables.imagesegment.SegmentPropertyColumnsSelectionDialog.NO_COLUMN_SELECTED;


public class ExploreIntensityImageAndLabelImageAndTable
{
	private final ImagePlus intensityImage;
	private final ImagePlus labelImage;
	private final File tablePath;

	private Map< String, List< String > > columns;
	private int numSpatialDimensions;
	private String labelImageId;
	private SegmentsTableBdvAnd3dViews tableBdvAnd3dViews;
	private SegmentsTableAndBdvViews tableAndBdvViews;

	private boolean timePointsInTableAreOneBased;
	private boolean coordinatesInTableAreCalibrated;

	public ExploreIntensityImageAndLabelImageAndTable(
			File intensityImagePath,
			File labelImagePath,
			File tablePath,
			boolean timePointsInTableAreOneBased,
			boolean coordinatesInTableAreCalibrated )
	{
		this.tablePath = tablePath;
		this.timePointsInTableAreOneBased = timePointsInTableAreOneBased;
		this.coordinatesInTableAreCalibrated = coordinatesInTableAreCalibrated;

		Logger.info("Opening intensity image: " + intensityImagePath );
		intensityImage = IJ.openImage( intensityImagePath.toString() );

		Logger.info("Opening label image: " + labelImagePath );
		labelImage = IJ.openImage( labelImagePath.toString() );

		Logger.info("Opening table: " + tablePath );
		final List< TableRowImageSegment > tableRowImageSegments
				= createSegments( tablePath, timePointsInTableAreOneBased );

		numSpatialDimensions = labelImage.getNSlices() > 1 ? 3 : 2;
		labelImageId = labelImage.getTitle();

		final DefaultImageSourcesModel imageSourcesModel = createImageSourcesModel();

		if ( numSpatialDimensions == 2 )
		{
			tableAndBdvViews = new SegmentsTableAndBdvViews(
					tableRowImageSegments,
					imageSourcesModel,
					labelImageId );
		}
		else
		{
			tableBdvAnd3dViews = new SegmentsTableBdvAnd3dViews(
					tableRowImageSegments,
					imageSourcesModel,
					labelImageId );

			tableBdvAnd3dViews.getSegments3dView().setSegmentFocusZoomLevel( 0.1 );

			final double pixelWidth = labelImage.getCalibration().pixelWidth;
			tableBdvAnd3dViews.getSegments3dView().setVoxelSpacing3DView( pixelWidth );

		}
	}

	private DefaultImageSourcesModel createImageSourcesModel()
	{
		final DefaultImageSourcesModel imageSourcesModel =
				new DefaultImageSourcesModel( numSpatialDimensions == 2 );

		Logger.info( "Adding to image sources: " + labelImageId );

		if ( !coordinatesInTableAreCalibrated )
		{
			Logger.info( "Since the coordinates in the table are not calibrated, the" +
					" images will be shown in pixel units as well." );
			Utils.removeCalibration( labelImage );
			Utils.removeCalibration( intensityImage );
		}

		imageSourcesModel.addSourceAndMetadata(
				Wraps.imagePlusAsSource4DChannelList( labelImage ).get( 0 ),
				labelImageId,
				Metadata.Flavour.LabelSource,
				numSpatialDimensions,
				Calibrations.getScalingTransform( labelImage ),
				null
		);

		imageSourcesModel.sources().get( labelImageId ).metadata().showInitially = true;

		if ( intensityImage != labelImage )
		{
			final String intensityImageId = intensityImage.getTitle();

			Logger.info( "Adding to image sources: " + intensityImageId );

			imageSourcesModel.addSourceAndMetadata(
					Wraps.imagePlusAsSource4DChannelList( intensityImage ).get( 0 ),
					intensityImageId,
					Metadata.Flavour.IntensitySource,
					numSpatialDimensions,
					Calibrations.getScalingTransform( intensityImage ),
					null
			);

			imageSourcesModel.sources().get( labelImageId )
					.metadata().imageSetIDs.add( intensityImageId );

			imageSourcesModel.sources().get( intensityImageId )
					.metadata().displayRangeMax = intensityImage.getProcessor().getMax();

		}



		imageSourcesModel.sources().get( labelImageId )
				.metadata().displayRangeMax = 500D;



		return imageSourcesModel;
	}

	private List< TableRowImageSegment >
	createSegments( File tableFile, boolean isOneBasedTimePoint )
	{
		columns = TableColumns.stringColumnsFromTableFile( tableFile );

		final Map< SegmentProperty, List< String > > propertyToColumn
				= createPropertyToColumnMap( columns.keySet() );

		final List< TableRowImageSegment > segments
				= SegmentUtils.tableRowImageSegmentsFromColumns(
				columns, propertyToColumn, isOneBasedTimePoint );

		return segments;
	}

	private Map< SegmentProperty, List< String > > createPropertyToColumnMap(
			Set< String > columnNames )
	{
		final SegmentPropertyColumnsSelectionDialog selectionDialog
				= new SegmentPropertyColumnsSelectionDialog( columnNames );

		final Map< SegmentProperty, String > segmentPropertyToColumn =
				selectionDialog.fetchUserInput();

		final Map< SegmentProperty, List< String > > propertyToColumn
				= new LinkedHashMap<>();

		for( SegmentProperty property : segmentPropertyToColumn.keySet() )
		{
			if ( segmentPropertyToColumn.get( property ).equals( NO_COLUMN_SELECTED ) )
				continue;

			propertyToColumn.put(
					property,
					this.columns.get( segmentPropertyToColumn.get( property ) ) );
		}

		return propertyToColumn;
	}


}
