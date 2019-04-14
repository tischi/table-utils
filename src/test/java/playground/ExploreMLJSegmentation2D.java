package playground;

import commands.RunExploreMorphoLibJ2DSegmentationCommand;
import de.embl.cba.tables.modelview.views.combined.SegmentsTableAndBdvViews;
import de.embl.cba.tables.ui.ExploreMorphoLibJLabelImage;
import ij.IJ;
import ij.ImagePlus;
import net.imagej.ImageJ;

public class ExploreMLJSegmentation2D
{
	public static void main( String[] args )
	{
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();

		final ImagePlus intensities = IJ.openImage( RunExploreMorphoLibJ2DSegmentationCommand.class.getResource(
				"../blobs.zip" ).getFile() );

		final ImagePlus labels = IJ.openImage( RunExploreMorphoLibJ2DSegmentationCommand.class.getResource(
				"../mask-lbl.zip" ).getFile() );

		IJ.open( RunExploreMorphoLibJ2DSegmentationCommand.class.getResource(
				"../blobs-lbl-Morphometry.csv" ).getFile() );

		final ExploreMorphoLibJLabelImage explore = new ExploreMorphoLibJLabelImage( intensities, labels, "Results" );


		final SegmentsTableAndBdvViews views = explore.getTableAndBdvViews();

		// TODO: how to programatically select few rows? ...and start the measurement?
		// views.getTableRowsTableView().

	}
}
