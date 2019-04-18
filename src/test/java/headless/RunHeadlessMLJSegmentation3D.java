package headless;

import de.embl.cba.tables.command.ExploreMorphoLibJLabelImage;
import ij.IJ;
import ij.ImagePlus;
import net.imagej.ImageJ;

public class RunHeadlessMLJSegmentation3D
{
	public static void main( String[] args )
	{
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();

		final ImagePlus intensities = IJ.openImage(
				RunHeadlessMLJSegmentation3D.class.getResource(
				"../3d-image.zip" ).getFile() );

		final ImagePlus labels = IJ.openImage(
				RunHeadlessMLJSegmentation3D.class.getResource(
				"../3d-image-lbl.zip" ).getFile() );

		IJ.open( RunHeadlessMLJSegmentation3D.class.getResource(
				"../3d-image-lbl-morpho.csv" ).getFile() );

		final ExploreMorphoLibJLabelImage explore =
				new ExploreMorphoLibJLabelImage(
						intensities, labels, "Results" );

	}
}