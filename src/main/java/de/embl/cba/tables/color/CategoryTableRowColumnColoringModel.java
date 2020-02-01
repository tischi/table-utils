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
	// TODO: The maps could go to int instead of ARGBType
	private Map< Object, ARGBType > inputToFixedColor;
	private Map< Object, ARGBType > inputToRandomColor;
	private final String column;
	private ARGBLut argbLut;
	private int randomSeed;
	private boolean fixedColorMode = false;

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
		convert( input.getCell( column ), output );
	}

	public void convert( String cellContent, ARGBType output )
	{
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
 		else if ( cellContent.equals( "NaN" ) || cellContent.equals( "None" ) )
		{
			final int color = ARGBType.rgba( 0, 0, 0, 0 );
			inputToRandomColor.put( cellContent, new ARGBType( color ) );
			output.set( color );
			return;
		}
		else
		{
			// final double random = createRandom( inputToRandomColor.size() + 1 );
			final double random = createRandom( cellContent.hashCode() );
			final int color = argbLut.getARGB( random );
			inputToRandomColor.put( cellContent, new ARGBType( color ) );
			output.set( color );
			return;
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
		if ( fixedColorMode ) return;

		inputToRandomColor.clear();
		this.randomSeed++;
		notifyColoringListeners();
	}

	public void fixedColorMode( boolean fixedColorMode )
	{
		this.fixedColorMode = fixedColorMode;
	}

	public void putInputToFixedColor( Object input, ARGBType color )
	{
		inputToFixedColor.put( input, color );
		notifyColoringListeners();
	}

}
