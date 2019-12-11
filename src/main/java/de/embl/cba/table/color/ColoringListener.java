package de.embl.cba.table.color;

import de.embl.cba.table.select.SelectionModel;

/**
 * Interface for listeners of a {@link SelectionModel}.
 *
 */
public interface ColoringListener
{
	/**
	 * Notifies when the color has changed.
	 */
	public void coloringChanged();

}