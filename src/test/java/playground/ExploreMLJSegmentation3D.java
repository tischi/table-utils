package playground;

import de.embl.cba.tables.modelview.views.combined.SegmentsTableAndBdvViews;
import de.embl.cba.tables.ui.ExploreMorphoLibJLabelImage;
import ij.IJ;
import ij.ImagePlus;
import net.imagej.ImageJ;

public class ExploreMLJSegmentation3D
{
	public static void main( String[] args )
	{
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();

		final ImagePlus intensities = IJ.openImage(
				ExploreMLJSegmentation3D.class.getResource(
				"../3d-image.zip" ).getFile() );

		final ImagePlus labels = IJ.openImage(
				ExploreMLJSegmentation3D.class.getResource(
				"../3d-image-lbl.zip" ).getFile() );

		IJ.open( ExploreMLJSegmentation3D.class.getResource(
				"../3d-image-lbl-morpho.csv" ).getFile() );

		final ExploreMorphoLibJLabelImage explore =
				new ExploreMorphoLibJLabelImage(
						intensities, labels, "Results" );

	}
}
