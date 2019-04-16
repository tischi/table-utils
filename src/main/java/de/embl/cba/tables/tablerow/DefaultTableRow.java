package de.embl.cba.tables.tablerow;

import java.util.LinkedHashMap;

public class DefaultTableRow implements TableRow
{
	private LinkedHashMap< String, Object > cells;
	private int rowIndex;

	public DefaultTableRow( LinkedHashMap< String, Object > cells, int rowIndex )
	{
		this.cells = cells;
		this.rowIndex = rowIndex;
	}

	@Override
	public LinkedHashMap< String, Object > cells()
	{
		return cells;
	}

	@Override
	public int rowIndex()
	{
		return rowIndex;
	}
}
