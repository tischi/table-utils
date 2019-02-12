package de.embl.cba.tables.modelview.coloring;

import de.embl.cba.bdv.utils.lut.ARGBLut;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.volatiles.VolatileARGBType;

import java.util.HashMap;
import java.util.Map;

import static de.embl.cba.bdv.utils.converters.RandomARGBConverter.goldenRatio;

public class LazyCategoryColoringModel< T > extends AbstractColoringModel< T >
{
	Map< T, ARGBType > inputToColorMap;
	ARGBLut argbLut;
	private int randomSeed;

	/**
	 * Colors are lazily assigned to input elements,
	 * using the given {@code argbLut}.
	 *
	 * TODO: better to use here a "generating LUT" rather than a 0...1 LUT
	 *
	 * @param argbLut
	 */
	public LazyCategoryColoringModel( ARGBLut argbLut, int randomSeed )
	{
		this.argbLut = argbLut;
		this.randomSeed = randomSeed;
		this.inputToColorMap = new HashMap<>(  );
	}

	@Override
	public void convert( T input, ARGBType output )
	{
		if( ! inputToColorMap.keySet().contains( input ) )
		{
			final double random = createRandom( inputToColorMap.size() + 1 );
			inputToColorMap.put( input, new ARGBType( argbLut.getARGB( random ) ) );
		}

		output.set( inputToColorMap.get( input ).get() );
	}

	private double createRandom( double x )
	{
		double random = ( x * randomSeed ) * goldenRatio;
		random = random - ( long ) Math.floor( random );
		return random;
	}

	public void incRandomSeed( )
	{
		inputToColorMap.clear();
		this.randomSeed++;

		notifyColoringListeners();
	}

}
