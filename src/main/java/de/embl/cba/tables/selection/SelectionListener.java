package de.embl.cba.tables.selection;

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
	 * Notifies when a focus event happened.
	 * Focus events do not necessarily enter the selection at all..
	 */
	public void focusEvent( T selection );

}