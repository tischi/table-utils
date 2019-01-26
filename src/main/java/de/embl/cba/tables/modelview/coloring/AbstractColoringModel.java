package de.embl.cba.tables.modelview.coloring;

import de.embl.cba.tables.modelview.selection.Listeners;
import net.imglib2.type.numeric.ARGBType;

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
	public void convert( T input, ARGBType output )
	{
		output.set( 0 );
	}
}
