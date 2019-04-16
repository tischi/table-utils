package de.embl.cba.tables.coloring;

import de.embl.cba.bdv.utils.lut.ARGBLut;
import de.embl.cba.tables.tablerow.TableRow;
import net.imglib2.type.numeric.ARGBType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static de.embl.cba.bdv.utils.converters.RandomARGBConverter.goldenRatio;

public class CategoryTableRowColumnColoringModel< T extends TableRow >
		extends AbstractColoringModel< T > implements CategoryColoringModel< T >
{
	Map< Object, ARGBType > inputToColorMap;
	private final String column;
	ARGBLut argbLut;
	private int randomSeed;

	/**
	 *
	 * @param argbLut
	 */
	public CategoryTableRowColumnColoringModel( String column, ARGBLut argbLut )
	{
		this.column = column;
		this.argbLut = argbLut;
		this.inputToColorMap = new ConcurrentHashMap<>(  );
		this.randomSeed = 50;
	}

	@Override
	public void convert( T input, ARGBType output )
	{
		final Object featureValue = input.cells().get( column );

		if( ! inputToColorMap.keySet().contains( featureValue ) )
		{
			// TODO: replace by other type of LUT ( not 0..1 )
			final double random = createRandom( inputToColorMap.size() + 1 );
			inputToColorMap.put( featureValue,
					new ARGBType( argbLut.getARGB( random ) ) );
		}

		final int colorIndex = inputToColorMap.get( featureValue ).get();
		output.set( colorIndex );
	}

	private double createRandom( double x )
	{
		double random = ( x * randomSeed ) * goldenRatio;
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
