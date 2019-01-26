package de.embl.cba.tables.modelview.coloring;

import de.embl.cba.bdv.utils.lut.ARGBLut;
import de.embl.cba.bdv.utils.lut.GlasbeyARGBLut;
import de.embl.cba.tables.modelview.selection.Listeners;
import net.imglib2.type.numeric.ARGBType;

import java.util.HashMap;
import java.util.Map;

import static de.embl.cba.bdv.utils.converters.RandomARGBConverter.goldenRatio;

public class DynamicCategoryColoringModel< T > implements ColoringModel< T >
{
	Map< T, ARGBType > inputToColorMap;
	ARGBLut argbLut;

	/**
	 * Objects are converted to colors.
	 * Colors are dynamically assigned at random
	 * from the given {@code argbLut} and stored in a map,
	 * such that same objects will always be converted to same
	 * colors.
	 *
	 * @param argbLut
	 */
	public DynamicCategoryColoringModel( ARGBLut argbLut )
	{
		this.argbLut = argbLut;
		inputToColorMap = new HashMap<>(  );
	}

	@Override
	public void convert( T input, ARGBType output )
	{
		if( ! inputToColorMap.keySet().contains( input ) )
		{
			final double random = createRandom( inputToColorMap.size() );
			inputToColorMap.put( input, new ARGBType( argbLut.getARGB( random ) ) );
		}

		output.set( inputToColorMap.get( input ).get() );
	}

	@Override
	public Listeners< ColoringListener > listeners()
	{
		return null;
	}

	public double createRandom( double x )
	{
		double random = ( x * 50 ) * goldenRatio;
		random = random - ( long ) Math.floor( random );
		return random;
	}

}
