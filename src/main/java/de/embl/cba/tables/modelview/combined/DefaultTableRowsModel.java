package de.embl.cba.tables.modelview.combined;

import de.embl.cba.tables.modelview.segments.TableRow;

import java.util.ArrayList;
import java.util.List;

public class DefaultTableRowsModel< T extends TableRow > implements TableRowsModel< T >
{
	// TODO: add columnTypes here?

	private final List< T > tableRows;
	private final String tableName;

	public DefaultTableRowsModel( List< T > tableRows, String tableName )
	{
		this.tableRows = tableRows;
		this.tableName = tableName;
	}

	@Override
	public List< T > getTableRows()
	{
		return tableRows;
	}

	@Override
	public String getName()
	{
		return tableName;
	}
}
