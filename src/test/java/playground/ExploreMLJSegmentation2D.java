package playground;

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

		final ImagePlus intensities = IJ.openImage( ExploreMLJSegmentation2D.class.getResource(
				"../blobs.zip" ).getFile() );

		final ImagePlus labels = IJ.openImage( ExploreMLJSegmentation2D.class.getResource(
				"../mask-lbl.zip" ).getFile() );

		IJ.open( ExploreMLJSegmentation2D.class.getResource(
				"../blobs-lbl-Morphometry.csv" ).getFile() );

		final ExploreMorphoLibJLabelImage explore =
				new ExploreMorphoLibJLabelImage(
						intensities, labels, "Results" );

		final SegmentsTableAndBdvViews views = explore.getTableAndBdvViews();


	}
}
