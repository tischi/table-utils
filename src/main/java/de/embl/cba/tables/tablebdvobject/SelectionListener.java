package de.embl.cba.tables.tablebdvobject;

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

	public void selectionAdded( T selection );

	public void selectionRemoved( T selection );

}