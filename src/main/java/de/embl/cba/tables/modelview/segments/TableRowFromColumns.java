package de.embl.cba.tables.modelview.segments;

import java.util.*;

public class TableRowFromColumns implements TableRow
{
	private final ColumnToValue columnToValue;
	private final int rowIndex;

	public TableRowFromColumns(
			LinkedHashMap< String, ArrayList< Object > > columnToValues,
			int rowIndex )
	{
		this.rowIndex = rowIndex;
		columnToValue = new ColumnToValue( columnToValues, rowIndex );
	}

	@Override
	public LinkedHashMap< String, Object > cells()
	{
		return columnToValue;
	}

	@Override
	public int rowIndex()
	{
		return rowIndex;
	}


	/**
	 * Extracts values from columns at {@code rowIndex}
	 * and presents them as a {@code LinkedHashMap}
	 * with column names as keys03vffg.
	 *
	 */
	class ColumnToValue extends LinkedHashMap< String, Object >
	{
		private final LinkedHashMap< String, ArrayList< Object > > columnToValues;
		private final int rowIndex;

		public ColumnToValue(
				LinkedHashMap< String, ArrayList< Object > > columnToValues,
				int rowIndex )
		{
			super( columnToValues );
			this.columnToValues = columnToValues;
			this.rowIndex = rowIndex;
		}

		@Override
		public boolean containsValue( Object value )
		{
			return false; // TODO
		}

		@Override
		public Object get( Object key )
		{
			return columnToValues.get( key ).get( rowIndex );
		}

		@Override
		public Object put( String key, Object value )
		{
			if (  containsKey( key ) )
			{
				columnToValues.get( key ).set( rowIndex, value );
			}
			else
			{
				columnToValues.put( key, new ArrayList<>(  ) );
				// Fill array with some default and then set??
			}
			return null;
		}

		@Override
		public Object remove( Object key )
		{
			// TODO
			return null;
		}

		@Override
		public void putAll( Map< ? extends String, ? > m )
		{

		}

		@Override
		public void clear()
		{

		}

		@Override
		public Collection< Object > values()
		{
			// TODO
			return null;
		}

	}

}
