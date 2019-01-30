package de.embl.cba.tables.modelview.segments;

import java.util.LinkedHashMap;

public interface TableRow
{
	/**
	 * Contains feature names and values.
	 * Keeping it as a {@link LinkedHashMap} ensures that the
	 * order of both cells and values is fixed.
	 * This is convenient, e.g., when building a table from
	 * many {@link TableRow} instances that are derived from the
	 * same data source.
	 *
	 * @return
	 */
	LinkedHashMap< String, Object > cells();

	/**
	 * The index of the row in the underlying table.
	 *
	 * @return row index
	 */
	int rowIndex();
}
