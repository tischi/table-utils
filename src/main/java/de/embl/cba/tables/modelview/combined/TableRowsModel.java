package de.embl.cba.tables.modelview.combined;

import de.embl.cba.tables.modelview.segments.TableRow;

import java.util.ArrayList;

public interface TableRowsModel < T extends TableRow >
{
	ArrayList< T > getTableRows();
}
