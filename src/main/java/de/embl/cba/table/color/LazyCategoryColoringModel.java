package de.embl.cba.table.color;

import de.embl.cba.table.lut.ARGBLut;
import net.imglib2.type.numeric.ARGBType;

import java.util.HashMap;
import java.util.Map;

public class LazyCategoryColoringModel< T > extends AbstractColoringModel< T > implements CategoryColoringModel< T >
{
	private Map< T, ARGBType > inputToColorMap;
	private ARGBLut argbLut;
	private int randomSeed;

	final static public double goldenRatio = 1.0 / ( 0.5 * Math.sqrt( 5 ) + 0.5 );

	/**
	 * Colors are lazily assigned to input elements,
	 * using the given {@code argbLut}.
	 *
	 * TODO: better to use here a "generating LUT" rather than a 0...1 LUT
	 *
	 * @param argbLut
	 */
	public LazyCategoryColoringModel( ARGBLut argbLut )
	{
		super();
		this.argbLut = argbLut;
		this.inputToColorMap = new HashMap<>(  );
		this.randomSeed = 50;
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
		double random = ( x * randomSeed ) * goldenRatio; // TODO: why golden ratio?
		random = random - ( long ) Math.floor( random );
		return random;
	}

	@Override
	public void incRandomSeed( )
	{
		inputToColorMap.clear();
		this.randomSeed++;

		notifyColoringListeners();
	}

}
