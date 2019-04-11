package de.embl.cba.tables.ui;

import de.embl.cba.bdv.utils.lut.GlasbeyARGBLut;
import de.embl.cba.bdv.utils.wrap.Wraps;
import de.embl.cba.tables.Calibrations;
import de.embl.cba.tables.Logger;
import de.embl.cba.tables.modelview.coloring.LazyCategoryColoringModel;
import de.embl.cba.tables.modelview.coloring.SelectionColoringModel;
import de.embl.cba.tables.modelview.combined.LazyImageSegmentsModel;
import de.embl.cba.tables.modelview.images.DefaultImageSourcesModel;
import de.embl.cba.tables.modelview.images.SourceMetadata;
import de.embl.cba.tables.modelview.segments.ImageSegment;
import de.embl.cba.tables.modelview.selection.DefaultSelectionModel;
import de.embl.cba.tables.modelview.selection.SelectionModel;
import de.embl.cba.tables.modelview.views.ImageSegmentsBdvView;
import ij.ImagePlus;
import net.imglib2.type.numeric.RealType;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.io.File;


@Plugin(type = Command.class,
		menuPath = "Plugins>Segmentation>Explore>Explore Label Image" )
public class ExploreLabelImageCommand < R extends RealType< R > > implements Command
{
	@Parameter ( label = "Label image" )
	public ImagePlus labelImage;

	@Parameter ( label = "Intensity image (optional)", required = false )
	public ImagePlus intensityImage;


	@Override
	public void run()
	{
		final DefaultImageSourcesModel imageSourcesModel = createImageSourcesModel();

		final LazyImageSegmentsModel lazyImageSegmentsModel
				= new LazyImageSegmentsModel();

		final SelectionModel< ImageSegment > selectionModel =
				new DefaultSelectionModel< ImageSegment >();

		final LazyCategoryColoringModel< ImageSegment > coloringModel =
				new LazyCategoryColoringModel< ImageSegment >( new GlasbeyARGBLut() );

		final SelectionColoringModel< ImageSegment > selectionColoringModel
				= new SelectionColoringModel< ImageSegment >( coloringModel, selectionModel );

		new ImageSegmentsBdvView(
				imageSourcesModel,
				lazyImageSegmentsModel,
				selectionModel,
				selectionColoringModel
		);

	}

	public boolean is2D()
	{
		boolean is2D = labelImage.getNSlices() == 1;
		if ( intensityImage != null )
		{
			if ( intensityImage.getNSlices() > labelImage.getNSlices() )
			{
				is2D = false;
			}
		}

		return is2D;
	}

	private DefaultImageSourcesModel createImageSourcesModel()
	{
		final DefaultImageSourcesModel imageSourcesModel =
				new DefaultImageSourcesModel( is2D() );

		final String labelImageId = labelImage.getTitle();

		Logger.info( "Adding to image sources: " + labelImageId );

		imageSourcesModel.addSourceAndMetadata(
				Wraps.imagePlusAsSource4DChannelList( labelImage ).get( 0 ),
				labelImageId,
				SourceMetadata.Flavour.LabelSource,
				getNumSpatialDimensions( labelImage.getNSlices() ),
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
					SourceMetadata.Flavour.IntensitySource,
					getNumSpatialDimensions( intensityImage.getNSlices() ),
					Calibrations.getScalingTransform( intensityImage ),
					null
			);

			imageSourcesModel.sources().get( labelImageId )
					.metadata().imageSetIDs.add( intensityImageId );
		}

		return imageSourcesModel;
	}

	private int getNumSpatialDimensions( int nSlices )
	{
		return nSlices > 1 ? 3 : 2;
	}


}
