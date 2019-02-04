package de.embl.cba.tables.modelview.combined;

import de.embl.cba.tables.modelview.segments.TableRow;

import java.util.ArrayList;

public class DefaultTableRowsModel< T extends TableRow > implements TableRowsModel< T >
{
	private final ArrayList< T > tableRows;

	public DefaultTableRowsModel( ArrayList< T > tableRows )
	{
		this.tableRows = tableRows;
	}

	@Override
	public ArrayList< T > getTableRows()
	{
		return tableRows;
	}
}
