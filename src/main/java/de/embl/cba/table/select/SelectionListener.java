package de.embl.cba.table.select;

/**
 * Interface for listeners of a {@link SelectionModel}.
 *
 */
public interface SelectionListener< T >
{
	/**
	 * Notifies when the select has changed.
	 */
	public void selectionChanged();

	/**
	 * Notifies when a focus event happened.
	 * Focus events do not necessarily enter the select at all..
	 */
	public void focusEvent( T selection );

}