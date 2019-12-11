package de.embl.cba.table.tablerow;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ColumnBasedTableRow extends TableRow
{
	String getCell( String columnName );

	void setCell( String columnName, String value );

	Set< String > getColumnNames();

	Map< String, List< String > > getColumns();

	/**
	 * The index of the row in the underlying table.
	 * TODO: Maybe this is not needed...
	 *
	 * @return row index
	 */
	int rowIndex();
}