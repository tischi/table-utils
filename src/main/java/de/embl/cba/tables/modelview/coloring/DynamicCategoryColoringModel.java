package de.embl.cba.tables.modelview.coloring;

import de.embl.cba.bdv.utils.lut.ARGBLut;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.volatiles.VolatileARGBType;

import java.util.HashMap;
import java.util.Map;

import static de.embl.cba.bdv.utils.converters.RandomARGBConverter.goldenRatio;

public class DynamicCategoryColoringModel< T > extends AbstractColoringModel< T >
{
	Map< T, ARGBType > inputToColorMap;
	ARGBLut argbLut;
	private int randomSeed;

	/**
	 * Objects are converted to colors.
	 * Colors are dynamically assigned at random
	 * from the given {@code argbLut} and stored in a map,
	 * such that same segments will always be converted to same
	 * colors.
	 *
	 * @param argbLut
	 */
	public DynamicCategoryColoringModel( ARGBLut argbLut, int randomSeed )
	{
		this.argbLut = argbLut;
		this.randomSeed = randomSeed;
		this.inputToColorMap = new HashMap<>(  );
	}

	@Override
	public void convert( T input, VolatileARGBType output )
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

	private void notifyColoringListeners()
	{
		for ( ColoringListener listener : listeners.list )
		{
			listener.coloringChanged();
		}
	}
}
