package de.embl.cba.tables.modelview.coloring;

import de.embl.cba.tables.modelview.selection.Listeners;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.volatiles.VolatileARGBType;

import java.util.Map;

public class CategoryColoringModel< T > extends AbstractColoringModel< T >
{
	Map< T, VolatileARGBType > objectColorMap;

	/**
	 * Objects are converted to colors by the specified
	 * {@code objectColorMap}. If the object is not found
	 * in the colorMap it converted to black.
	 *
	 * @param objectColorMap
	 */
	public CategoryColoringModel( Map< T, VolatileARGBType > objectColorMap )
	{
		this.objectColorMap = objectColorMap;
	}

	@Override
	public void convert( T input, VolatileARGBType output )
	{
		if( objectColorMap.keySet().contains( input ) )
		{
			output.get().set( objectColorMap.get( input ).get() );
		}
		else
		{
			output.set( 0 );
		}
	}
}
