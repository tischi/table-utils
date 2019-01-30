package de.embl.cba.tables.modelview.segments;

import java.util.*;

public class DefaultTableRowMap implements TableRowMap
{
	private final LinkedHashMap< String, ArrayList< Object > > columnToValues;
	private final int row;

	public DefaultTableRowMap( LinkedHashMap< String, ArrayList< Object > > columnToValues, int row )
	{
		this.columnToValues = columnToValues;
		this.row = row;
	}

	@Override
	public int size()
	{
		return 0;
	}

	@Override
	public boolean isEmpty()
	{
		return false;
	}

	@Override
	public boolean containsKey( Object key )
	{
		return columnToValues.containsKey( key );
	}

	@Override
	public boolean containsValue( Object value )
	{
		return false;
	}

	@Override
	public Object get( Object key )
	{
		return columnToValues.get( key ).get( row );
	}

	@Override
	public Object put( String key, Object value )
	{
		if (  containsKey( key ) )
		{
			columnToValues.get( key ).set( row, value );
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
	public Set< String > keySet()
	{
		return null;
	}

	@Override
	public Collection< Object > values()
	{
		return null;
	}

	@Override
	public Set< Entry< String, Object > > entrySet()
	{
		return null;
	}
}
