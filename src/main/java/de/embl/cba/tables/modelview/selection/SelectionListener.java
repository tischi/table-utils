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

	public void selectionChanged( T selection, boolean selected );

}