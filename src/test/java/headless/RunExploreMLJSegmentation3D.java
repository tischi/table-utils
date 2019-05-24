package headless;

import de.embl.cba.tables.morpholibj.ExploreMorphoLibJLabelImage;
import ij.IJ;
import ij.ImagePlus;
import net.imagej.ImageJ;

public class RunExploreMLJSegmentation3D
{
	public static void main( String[] args )
	{
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();

		final ImagePlus intensities = IJ.openImage(
				RunExploreMLJSegmentation3D.class.getResource(
				"../3d-image.zip" ).getFile() );

		final ImagePlus labels = IJ.openImage(
				RunExploreMLJSegmentation3D.class.getResource(
				"../3d-image-lbl.zip" ).getFile() );

		IJ.open( RunExploreMLJSegmentation3D.class.getResource(
				"../3d-image-lbl-morpho.csv" ).getFile() );

		new ExploreMorphoLibJLabelImage( intensities, labels, "Results" );

	}
}
