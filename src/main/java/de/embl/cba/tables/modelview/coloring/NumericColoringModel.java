package de.embl.cba.tables.modelview.coloring;

import de.embl.cba.tables.modelview.objects.TableRow;
import net.imglib2.type.numeric.ARGBType;

public interface NumericColoringModel< T > extends ColoringModel< T >
{
	@Override
	void convert( T input, ARGBType output );

	double getMin();

	double getMax();

	void setMin( double min );

	void setMax( double max );
}
