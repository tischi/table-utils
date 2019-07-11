package tests;

import de.embl.cba.tables.morpholibj.ExploreMorphoLibJLabelImage;
import de.embl.cba.tables.select.SelectionModel;
import de.embl.cba.tables.tablerow.TableRowImageSegment;
import de.embl.cba.tables.view.Segments3dView;
import de.embl.cba.tables.view.combined.SegmentsTableBdvAnd3dViews;
import headless.RunExploreMLJSegmentation3D;
import ij.IJ;
import ij.ImagePlus;
import net.imagej.ImageJ;

import java.util.List;

public class Test3DView
{
	public void showObjectsIn3D()
	{
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();

		final ImagePlus intensities = IJ.openImage(
				RunExploreMLJSegmentation3D.class.getResource(
						"../test-data/3d-image.zip" ).getFile() );

		final ImagePlus labels = IJ.openImage(
				RunExploreMLJSegmentation3D.class.getResource(
						"../test-data/3d-image-lbl.zip" ).getFile() );

		IJ.open( RunExploreMLJSegmentation3D.class.getResource(
				"../test-data/3d-image-lbl-morpho.csv" ).getFile() );

		final ExploreMorphoLibJLabelImage explore = new ExploreMorphoLibJLabelImage( intensities, labels, "Results" );

		final SegmentsTableBdvAnd3dViews views = explore.getTableBdvAnd3dViews();

		final SelectionModel< TableRowImageSegment > selectionModel = views.getSelectionModel();
		final List< TableRowImageSegment > tableRowImageSegments = views.getTableRowImageSegments();

		selectionModel.setSelected( tableRowImageSegments.get( 0 ), true );
		selectionModel.focus( tableRowImageSegments.get( 0 ) );

	}

	public static void main( String[] args )
	{
		new Test3DView().showObjectsIn3D();
	}
}
