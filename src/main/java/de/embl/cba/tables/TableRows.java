package de.embl.cba.tables;

import de.embl.cba.tables.imagesegment.ColumnBasedTableRowImageSegment;
import de.embl.cba.tables.tablerow.TableRow;

import java.util.*;

public abstract class TableRows
{
	public static < T extends TableRow >
	void addColumn( List< T > tableRows, String columnName, Object[] values )
	{
		if ( tableRows.get( 0 ) instanceof ColumnBasedTableRowImageSegment )
		{
			final Map< String, List< String > > columns
					= ( ( ColumnBasedTableRowImageSegment ) tableRows.get( 0 ) ).getColumns();

			final ArrayList< String > strings = new ArrayList<>();
			for ( int i = 0; i < values.length; i++ )
				strings.add( values.toString() );

			columns.put( columnName, strings );
		}
		else
		{
			throw new java.lang.UnsupportedOperationException(
					"TableRow class not supported yet: " + tableRows.get( 0 ).getClass());
		}
	}

	public static < T extends TableRow >
	void addColumn( List< T > tableRows, String columnName, Object value )
	{
		final Object[] values = new Object[ tableRows.size() ];
		Arrays.fill( values, value );

		addColumn( tableRows, columnName, values );
	}

}
