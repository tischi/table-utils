package de.embl.cba.tables.modelview.combined;

import de.embl.cba.tables.modelview.segments.TableRow;

import java.util.ArrayList;
import java.util.List;

public class DefaultTableRowsModel< T extends TableRow > implements TableRowsModel< T >
{
	// TODO: add columnTypes here?

	private final List< T > tableRows;

	public DefaultTableRowsModel( List< T > tableRows )
	{
		this.tableRows = tableRows;
	}

	@Override
	public ArrayList< T > getTableRows()
	{
		return tableRows;
	}
}
