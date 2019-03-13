package de.embl.cba.tables.modelview.coloring;

import de.embl.cba.bdv.utils.lut.ARGBLut;
import de.embl.cba.tables.TableUtils;
import de.embl.cba.tables.modelview.segments.TableRow;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.volatiles.VolatileARGBType;

import static de.embl.cba.bdv.utils.converters.RandomARGBConverter.goldenRatio;


// TODO: extract abstract class NumericFeatureColoringModel
public class NumericTableRowColumnColoringModel< T extends TableRow >
		extends AbstractColoringModel< T > implements NumericColoringModel< T >
{
	private String column;
	private ARGBLut lut;
	private double[] values;
	private double[] range;

	public NumericTableRowColumnColoringModel(
			String column,
			ARGBLut lut,
			double[] values,
			double[] range )
	{
		this.column = column;
		this.lut = lut;
		this.values = values;
		this.range = range;
	}

	@Override
	public void convert( T input, ARGBType output )
	{
		final Object featureValue = input.cells().get( column );
		setColorLinearly( featureValue, output );
	}


	@Override
	public double getMin()
	{
		return values[ 0 ];
	}


	@Override
	public double getMax()
	{
		return values[ 1 ];
	}


	@Override
	public void setMin( double min )
	{
		this.values[ 0 ] = min;
		notifyColoringListeners();
	}

	@Override
	public void setMax( double max )
	{
		this.values[ 1 ] = max;
		notifyColoringListeners();
	}

	public void setColumn( String column )
	{
		this.column = column;
	}

	public String getColumn()
	{
		return column;
	}


	public void setColorLinearly( Object featureValue, ARGBType output )
	{
		final double value = TableUtils.asDouble( featureValue );
		double normalisedValue = computeLinearNormalisedValue( value );
		final int colorIndex = lut.getARGB( normalisedValue );
		output.set( colorIndex );
	}

	public double computeLinearNormalisedValue( double value )
	{
		double normalisedValue = 0;
		if ( values[ 1 ] == values[ 0 ] )
		{
			if ( values[ 1 ] == range[ 0 ] )
			{
				normalisedValue = 1.0;
			}
			else if ( values[ 1 ] == range[ 1 ] )
			{
				normalisedValue = 0.0;
			}
		}
		else
		{
			normalisedValue =
					Math.max(
							Math.min(
									( value - values[ 0 ] )
											/ ( values[ 1 ] - values[ 0 ] ), 1.0 ), 0.0 );
		}
		return normalisedValue;
	}

}
