package de.embl.cba.tables.modelview.combined;

import de.embl.cba.tables.modelview.segments.TableRow;

import java.util.ArrayList;
import java.util.List;

public interface TableRowsModel < T extends TableRow >
{
	List< T > getTableRows();

	String getName();
}
