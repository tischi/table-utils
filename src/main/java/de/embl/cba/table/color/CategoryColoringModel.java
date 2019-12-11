package de.embl.cba.table.color;

import de.embl.cba.table.select.Listeners;
import net.imglib2.converter.Converter;
import net.imglib2.type.numeric.ARGBType;


public interface CategoryColoringModel< T > extends Converter< T, ARGBType >
{
	/**
	 * Get the list of color listeners. Add a {@link ColoringListener} to
	 * this list, for being notified when the object/edge select changes.
	 *
	 * @return the list of listeners
	 */
	Listeners< ColoringListener > listeners();

	void incRandomSeed();

}
