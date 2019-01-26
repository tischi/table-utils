package de.embl.cba.tables.modelview.coloring;

import de.embl.cba.bdv.utils.lut.ARGBLut;
import de.embl.cba.bdv.utils.lut.Luts;
import de.embl.cba.tables.modelview.selection.Listeners;
import net.imglib2.converter.Converter;
import net.imglib2.type.numeric.ARGBType;

public interface ColumnColoringModel< T > extends ColoringModel< T >
{

	/**
	 * Assigns colors to values in a column.
	 *
	 * For {@code ColoringMode.Linear}, {@code min} and {@code max} values
	 * can be set and dynamically modified in order to change the mapping
	 * of the values onto the given {@code lut} (Lookup Table).
	 *
	 * For {@code ColoringMode.Categorical}, the input values
	 * of the values onto the given {@code lut} (Lookup Table).
	 *
	 *
	 */

	enum ColoringMode
	{
		Categorical,
		Linear
	}

	/**
	 * TODO
	 */
	void setCategoricalColoring( String column, ARGBLut lut );

	void setLinearColoring( String column, ARGBLut lut, double min, double max );

	String getColumn();

	double getMin();

	double getMax();

	void setMin( double min );

	void setMax( double max );


	/**
	 * Get the list of coloring listeners. Add a {@link ColoringListener} to
	 * this list, for being notified when the object/edge selection changes.
	 *
	 * @return the list of listeners
	 */
	Listeners< ColoringListener > listeners();



}
