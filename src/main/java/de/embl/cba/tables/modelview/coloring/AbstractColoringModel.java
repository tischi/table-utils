package de.embl.cba.tables.modelview.coloring;

import de.embl.cba.tables.modelview.selection.Listeners;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.volatiles.VolatileARGBType;

public abstract class AbstractColoringModel< T > implements ColoringModel< T >
{
	protected final Listeners.SynchronizedList< ColoringListener > listeners
			= new Listeners.SynchronizedList< ColoringListener >(  );

	@Override
	public Listeners< ColoringListener > listeners()
	{
		return listeners;
	}

	@Override
	public void convert( T input, VolatileARGBType output )
	{
		output.set( 0 );
	}
}
