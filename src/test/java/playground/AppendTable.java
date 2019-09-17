package playground;

import de.embl.cba.tables.TableColumns;
import de.embl.cba.tables.command.ExploreMorphoLibJLabelImageCommand;
import de.embl.cba.tables.imagesegment.SegmentProperty;
import de.embl.cba.tables.imagesegment.SegmentUtils;
import de.embl.cba.tables.morpholibj.ExploreMorphoLibJLabelImage;
import de.embl.cba.tables.tablerow.TableRowImageSegment;
import de.embl.cba.tables.view.combined.SegmentsTableBdvAnd3dViews;
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

		final String columnName = columns.keySet().iterator().next();
		final List< String > strings = columns.get( columnName );

		//Object[] objects = new Object[ strings.size() ];
		Object[] objects = strings.toArray();
		views.getTableRowsTableView().addColumn( columnName, objects );

	}


}

