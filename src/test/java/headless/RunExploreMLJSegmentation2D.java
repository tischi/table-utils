package headless;

import de.embl.cba.tables.view.combined.SegmentsTableAndBdvViews;
import de.embl.cba.tables.morpholibj.ExploreMorphoLibJLabelImage;
import ij.IJ;
import ij.ImagePlus;
import net.imagej.ImageJ;

public class RunExploreMLJSegmentation2D
{
	public static void main( String[] args )
	{
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();

		final ImagePlus intensities = IJ.openImage( RunExploreMLJSegmentation2D.class.getResource(
				"../blobs.zip" ).getFile() );

		final ImagePlus labels = IJ.openImage( RunExploreMLJSegmentation2D.class.getResource(
				"../mask-lbl.zip" ).getFile() );

		IJ.open( RunExploreMLJSegmentation2D.class.getResource(
				"../blobs-lbl-Morphometry.csv" ).getFile() );

		final ExploreMorphoLibJLabelImage explore =
				new ExploreMorphoLibJLabelImage(
						intensities, labels, "Results" );

		final SegmentsTableAndBdvViews views = explore.getTableAndBdvViews();


	}
}
