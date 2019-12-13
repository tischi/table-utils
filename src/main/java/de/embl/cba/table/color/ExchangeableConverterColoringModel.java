package de.embl.cba.table.color;

import de.embl.cba.table.select.Listeners;
import net.imglib2.converter.Converter;
import net.imglib2.type.numeric.ARGBType;

import javax.swing.*;

public abstract class ExchangeableConverterColoringModel< T > implements ColoringModel< T >
{
	protected final Listeners.SynchronizedList< ColoringListener > listeners
			= new Listeners.SynchronizedList< ColoringListener >(  );

	protected Converter< T , ARGBType > converter;

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

	public void setConverter( Converter< T, ARGBType > converter )
	{
		this.converter = converter;
	}
}
