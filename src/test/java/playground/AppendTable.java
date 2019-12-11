package playground;

import de.embl.cba.table.TableColumns;
import de.embl.cba.table.command.ExploreMorphoLibJLabelImageCommand;
import de.embl.cba.table.imagesegment.SegmentProperty;
import de.embl.cba.table.imagesegment.SegmentUtils;
import de.embl.cba.table.morpholibj.ExploreMorphoLibJLabelImage;
import de.embl.cba.table.view.combined.SegmentsTableBdvAnd3dViews;
import ij.IJ;
import net.imagej.ImageJ;

import java.util.List;
import java.util.Map;

public class AppendTable
{
	public static void main( String[] args )
	{
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();

		IJ.open( AppendTable.class.getResource(
				"../test-data/3d-image-lbl-morpho.csv" ).getFile() );

		final ExploreMorphoLibJLabelImage explore = new ExploreMorphoLibJLabelImage(
				IJ.openImage( AppendTable.class.getResource(
						"../test-data/3d-image.zip" ).getFile() ),
				IJ.openImage( AppendTable.class.getResource(
						"../test-data/3d-image-lbl.zip" ).getFile() ),
				"3d-image-lbl-morpho" );


		final SegmentsTableBdvAnd3dViews views = explore.getTableBdvAnd3dViews();

		Map< String, List< String > > columns =
				TableColumns.stringColumnsFromTableFile( AppendTable.class.getResource(
						"../test-data/3d-image-lbl-morpho-addOn.csv" ).getFile()  );

		views.getTableRowsTableView().addColumns( columns );
	}

}

