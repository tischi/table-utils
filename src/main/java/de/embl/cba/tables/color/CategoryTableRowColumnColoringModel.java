package de.embl.cba.tables.color;

import de.embl.cba.bdv.utils.lut.ARGBLut;
import de.embl.cba.tables.tablerow.TableRow;
import net.imglib2.type.numeric.ARGBType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static de.embl.cba.bdv.utils.converters.RandomARGBConverter.goldenRatio;

public class CategoryTableRowColumnColoringModel< T extends TableRow >
		extends AbstractColoringModel< T > implements CategoryColoringModel< T >
{
	Map< Object, ARGBType > inputToColor;
	private final String column;
	ARGBLut argbLut;
	private int randomSeed;
	private final Map< Object, ARGBType > specialInputToColor;

	/**
	 *
	 * @param argbLut
	 */
	public CategoryTableRowColumnColoringModel( String column, ARGBLut argbLut )
	{
		this.column = column;
		this.argbLut = argbLut;
		this.inputToColor = new ConcurrentHashMap<>(  );
		this.randomSeed = 50;
		specialInputToColor = new ConcurrentHashMap<>();
	}

	@Override
	public void convert( T input, ARGBType output )
	{
		final Object featureValue = input.getCell( column );

		if ( specialInputToColor.keySet().contains( featureValue ) )
		{
			final int color = specialInputToColor.get( featureValue ).get();
			output.set( color );
		}
		else if ( inputToColor.keySet().contains( featureValue ) )
		{
			final int color = inputToColor.get( featureValue ).get();
			output.set( color );
		}
		else
		{
			final double random = createRandom( inputToColor.size() + 1 );
			inputToColor.put( featureValue, new ARGBType( argbLut.getARGB( random ) ) );
			final int color = inputToColor.get( featureValue ).get();
			output.set( color );
		}
	}

	public Map< Object, ARGBType > getSpecialInputToColor()
	{
		return specialInputToColor;
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
		inputToColor.clear();
		this.randomSeed++;

		notifyColoringListeners();
	}

}
