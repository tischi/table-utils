package de.embl.cba.tables.modelview.coloring;

import de.embl.cba.bdv.utils.lut.ARGBLut;
import de.embl.cba.bdv.utils.lut.Luts;
import de.embl.cba.tables.modelview.selection.Listeners;
import net.imglib2.converter.Converter;
import net.imglib2.type.numeric.ARGBType;

public interface FeatureColoringModel< T > extends ColoringModel< T >
{
	/**
	 * Get the list of coloring listeners. Add a {@link ColoringListener} to
	 * this list, for being notified when the object/edge selection changes.
	 *
	 * @return the list of listeners
	 */
	Listeners< ColoringListener > listeners();

	enum ColoringMode
	{
		Categorical,
		Linear
	}

	/**
	 * TODO
	 */
	void setCategoricalColoring( String coloringFeature, ARGBLut lut );

	void setLinearColoring( String coloringFeature, ARGBLut lut, double min, double max );


}
