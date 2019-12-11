package de.embl.cba.table.tablerow;

import java.util.Set;


public interface TableRow
{
	String getCell( String columnName );

	void setCell( String columnName, String value );

	Set< String > getColumnNames();

	/**
	 * The index of the row in the underlying model.
	 * TODO: Maybe this is not needed...
	 *
	 * @return row index
	 */
	int rowIndex();
}
