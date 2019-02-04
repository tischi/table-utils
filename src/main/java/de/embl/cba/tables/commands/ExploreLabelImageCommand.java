package de.embl.cba.tables.commands;

import de.embl.cba.bdv.utils.lut.GlasbeyARGBLut;
import de.embl.cba.tables.modelview.coloring.DynamicCategoryColoringModel;
import de.embl.cba.tables.modelview.coloring.SelectionColoringModel;
import de.embl.cba.tables.modelview.combined.GeneratingImageSegmentsModel;
import de.embl.cba.tables.modelview.images.DefaultImageSourcesModel;
import de.embl.cba.tables.modelview.images.Metadata;
import de.embl.cba.tables.modelview.images.SourceLoader;
import de.embl.cba.tables.modelview.segments.ImageSegment;
import de.embl.cba.tables.modelview.selection.DefaultSelectionModel;
import de.embl.cba.tables.modelview.selection.SelectionModel;
import de.embl.cba.tables.modelview.views.bdv.ImageSegmentsBdvView;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;


@Plugin(type = Command.class, menuPath = "Plugins>Measurement>Browse Table And Image" )
public class ExploreLabelImageCommand< R extends RealType< R > & NativeType< R > >
		implements Command
{
	@Parameter ( label = "Label image (single channel, 2D+t or 3D+t)" )
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
				Metadata.Flavour.LabelSource,
				labelSourceLoader.getNumSpatialDimensions() );

		final GeneratingImageSegmentsModel generatingImageSegmentsModel = new GeneratingImageSegmentsModel();

		final SelectionModel< ImageSegment > selectionModel =
				new DefaultSelectionModel< ImageSegment >();

		final DynamicCategoryColoringModel< ImageSegment > coloringModel =
				new DynamicCategoryColoringModel< ImageSegment >( new GlasbeyARGBLut(), 50 );

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

		/**
		 * Provide information to generate image segments on the fly when the user clicks
		 */
		generatingImageSegmentsModel.setBdv( imageSegmentsBdvView.getBdv() );
		generatingImageSegmentsModel.setSourceAndMetadata( imageSourcesModel.sources().values().iterator().next() );


	}


}
