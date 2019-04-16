package de.embl.cba.tables.coloring;

import de.embl.cba.tables.selection.Listeners;
import net.imglib2.converter.Converter;
import net.imglib2.type.numeric.ARGBType;

public interface ColoringModel< T > extends Converter< T, ARGBType >
{
	/**
	 * Get the list of coloring listeners. Add a {@link ColoringListener} to
	 * this list, for being notified when the object/edge selection changes.
	 *
	 * @return the list of listeners
	 */
	Listeners< ColoringListener > listeners();

}
