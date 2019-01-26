package de.embl.cba.tables.modelview.coloring;

import de.embl.cba.tables.modelview.selection.Listeners;
import net.imglib2.type.numeric.ARGBType;

import java.util.Map;

public class CategoryColoringModel< T > implements ColoringModel< T >
{
	Map< T, ARGBType > objectColorMap;

	/**
	 * Objects are converted to colors by the specified
	 * {@code objectColorMap}. If the object is not found
	 * in the colorMap it converted to black.
	 *
	 * @param objectColorMap
	 */
	public CategoryColoringModel( Map< T, ARGBType > objectColorMap )
	{
		this.objectColorMap = objectColorMap;
	}

	@Override
	public void convert( T input, ARGBType output )
	{
		if( objectColorMap.keySet().contains( input ) )
		{
			output.set( objectColorMap.get( input ).get() );
		}
		else
		{
			output.set( 0 );
		}
	}

	@Override
	public Listeners< ColoringListener > listeners()
	{
		return null;
	}

}
