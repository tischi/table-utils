package de.embl.cba.tables.modelview.objects;

import java.util.LinkedHashMap;
import java.util.Map;

public interface Features
{
	/**
	 * Contains feature names and values.
	 * Keeping it as a {@link LinkedHashMap} ensures that the
	 * order of both features and values is fixed.
	 * This is convenient, e.g., when building a table from
	 * many {@link Features} instances that are derived from the
	 * same data source.
	 *
	 * @return
	 */
	LinkedHashMap< String, Object > features();
}
