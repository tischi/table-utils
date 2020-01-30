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
	private Map< Object, ARGBType > inputToFixedColor;
	private Map< Object, ARGBType > inputToRandomColor;
	private final String column;
	private ARGBLut argbLut;
	private int randomSeed;

	/**
	 *
	 * @param argbLut
	 */
	public CategoryTableRowColumnColoringModel( String column, ARGBLut argbLut )
	{
		this.column = column;
		this.argbLut = argbLut;
		this.inputToRandomColor = new ConcurrentHashMap<>(  );
		this.inputToFixedColor = new ConcurrentHashMap<>(  );
		this.randomSeed = 50;
	}

	@Override
	public void convert( T input, ARGBType output )
	{
		final String cellContent = input.getCell( column );

		if ( inputToFixedColor.keySet().contains( cellContent ) )
		{
			final int color = inputToFixedColor.get( cellContent ).get();
			output.set( color );
		}
 		else if ( inputToRandomColor.keySet().contains( cellContent ) )
		{
			final int color = inputToRandomColor.get( cellContent ).get();
			output.set( color );
		}
		else
		{
			// final double random = createRandom( inputToRandomColor.size() + 1 );
			final double random = createRandom( cellContent.hashCode() );
			inputToRandomColor.put( cellContent, new ARGBType( argbLut.getARGB( random ) ) );
			final int color = inputToRandomColor.get( cellContent ).get();
			output.set( color );
		}
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
		inputToRandomColor.clear();
		this.randomSeed++;

		notifyColoringListeners();
	}

	public void addInputToFixedColor( Object input, ARGBType color )
	{
		inputToFixedColor.put( input, color );
	}

}
