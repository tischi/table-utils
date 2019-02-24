package de.embl.cba.tables.ui;

import de.embl.cba.bdv.utils.lut.GlasbeyARGBLut;
import de.embl.cba.tables.modelview.coloring.LazyCategoryColoringModel;
import de.embl.cba.tables.modelview.coloring.SelectionColoringModel;
import de.embl.cba.tables.modelview.combined.GeneratingNoPositionImageSegmentsModel;
import de.embl.cba.tables.modelview.images.DefaultImageSourcesModel;
import de.embl.cba.tables.modelview.images.SourceMetadata;
import de.embl.cba.tables.modelview.images.SourceLoader;
import de.embl.cba.tables.modelview.segments.ImageSegment;
import de.embl.cba.tables.modelview.selection.DefaultSelectionModel;
import de.embl.cba.tables.modelview.selection.SelectionModel;
import de.embl.cba.tables.modelview.views.ImageSegmentsBdvView;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;

import java.io.File;
import java.util.ArrayList;


//@Plugin(type = Command.class, menuPath = "Plugins>Segmentation>Explore>Explore Label Image" )
public class ExploreLabelImageCommand< R extends RealType< R > & NativeType< R > >
		implements Command
{
	@Parameter ( label = "ObjectLabel image (single channel, 2D+t or 3D+t)" )
	public File inputLabelMasksFile;

	@Parameter ( label = "Intensities (optional)", required = false )
	public File inputIntensitiesFile;

	@Override
	public void run()
	{

		final SourceLoader labelSourceLoader = new SourceLoader( inputLabelMasksFile );

		final DefaultImageSourcesModel imageSourcesModel = new DefaultImageSourcesModel();

		imageSourcesModel.addSource(
				labelSourceLoader.getRandomAccessibleIntervalSource4D(),
				inputLabelMasksFile.getName(),
				SourceMetadata.Flavour.LabelSource,
				labelSourceLoader.getNumSpatialDimensions(),
				new AffineTransform3D() // TODO
		);

		final GeneratingNoPositionImageSegmentsModel generatingImageSegmentsModel
				= new GeneratingNoPositionImageSegmentsModel();

		final SelectionModel< ImageSegment > selectionModel =
				new DefaultSelectionModel< ImageSegment >();

		final LazyCategoryColoringModel< ImageSegment > coloringModel =
				new LazyCategoryColoringModel< ImageSegment >( new GlasbeyARGBLut() );

		final SelectionColoringModel< ImageSegment > selectionColoringModel
				= new SelectionColoringModel< ImageSegment >( coloringModel, selectionModel );

		final ArrayList< String > initialSources = new ArrayList<>();
		initialSources.add( imageSourcesModel.sources().keySet().iterator().next() );

		final ImageSegmentsBdvView imageSegmentsBdvView =
				new ImageSegmentsBdvView(
						imageSourcesModel,
						generatingImageSegmentsModel,
						selectionModel,
						selectionColoringModel
				);

	}


}
