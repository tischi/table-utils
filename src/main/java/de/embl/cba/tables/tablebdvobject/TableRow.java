package de.embl.cba.tables.tablebdvobject;

import java.util.ArrayList;

public interface TableRow
{
	ArrayList< String > tableColumnNames();

	Object valueInTableColumn( String columnName );

	int tableRowIndex();
}
