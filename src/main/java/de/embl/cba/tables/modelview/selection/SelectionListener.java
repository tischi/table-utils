package de.embl.cba.tables.modelview.selection;

/**
 * Interface for listeners of a {@link SelectionModel}.
 *
 */
public interface SelectionListener< T >
{
	/**
	 * Notifies when the selection has changed.
	 */
	public void selectionChanged();

	/**
	 * Notifies when a selection event happened,
	 * also when this did not change the list
	 * of currently selected items.
	 */
	public void selectionEvent( T selection, boolean selected );

}