package de.embl.cba.tables.coloring;

import de.embl.cba.tables.selection.Listeners;
import net.imglib2.type.numeric.ARGBType;

import javax.swing.*;

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

	protected void notifyColoringListeners()
	{
		for ( ColoringListener listener : listeners.list )
		{
			SwingUtilities.invokeLater( () -> listener.coloringChanged() );
		}
	}
}
