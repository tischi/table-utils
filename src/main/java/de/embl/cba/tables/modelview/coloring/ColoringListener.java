package de.embl.cba.tables.modelview.coloring;

import de.embl.cba.tables.modelview.selection.SelectionModel;

/**
 * Interface for listeners of a {@link SelectionModel}.
 *
 */
public interface ColoringListener
{
	/**
	 * Notifies when the coloring has changed.
	 */
	public void coloringChanged();

}