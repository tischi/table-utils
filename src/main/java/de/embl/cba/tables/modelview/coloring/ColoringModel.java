package de.embl.cba.tables.modelview.coloring;

import de.embl.cba.tables.modelview.selection.Listeners;
import de.embl.cba.tables.modelview.selection.SelectionListener;
import net.imglib2.converter.Converter;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.volatiles.VolatileARGBType;

public interface ColoringModel< T > extends Converter< T, VolatileARGBType >
{
	/**
	 * Get the list of coloring listeners. Add a {@link ColoringListener} to
	 * this list, for being notified when the object/edge selection changes.
	 *
	 * @return the list of listeners
	 */
	Listeners< ColoringListener > listeners();

}
