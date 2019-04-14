package de.embl.cba.tables;

import de.embl.cba.tables.modelview.segments.ColumnBasedTableRowImageSegment;
import de.embl.cba.tables.modelview.segments.TableRow;
import ij.IJ;

import javax.activation.UnsupportedDataTypeException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class TableRows
{

	public static < T extends TableRow >
	void addColumn( List< T > tableRows, String newColumnName, Object[] values )
	{
		if ( tableRows.get( 0 ) instanceof ColumnBasedTableRowImageSegment )
		{
			final LinkedHashMap< String, List< ? > > columns
					= ( ( ColumnBasedTableRowImageSegment ) tableRows.get( 0 ) ).getColumns();

			columns.put( newColumnName, new ArrayList<>( Arrays.asList( values ) ) );
		}
		else
		{
			throw new java.lang.UnsupportedOperationException(
					"TableRow class not supported yet: " + tableRows.get( 0 ).getClass());
		}

		int a = 1;

	}
}
