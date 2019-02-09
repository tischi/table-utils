package de.embl.cba.tables.modelview.coloring;

import net.imglib2.type.volatiles.VolatileARGBType;

public interface NumericColoringModel< T > extends ColoringModel< T >
{
	@Override
	void convert( T input, VolatileARGBType output );

	double getMin();

	double getMax();

	void setMin( double min );

	void setMax( double max );
}
