package example;

import de.embl.cba.table.util.TableUtils;

import java.util.List;

public class OpenAndShowTable
{
	public static void main( String[] args )
	{
		final List< String > rows = TableUtils.loadRows( OpenAndShowTable.class.getResource( "tables/3d-image-lbl-morpho.csv" ).getFile() );

		TableUtils.columnBasedTableRowsFromColumns(  )

	}

}

