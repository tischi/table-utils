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
	private double min;
	private double max;
	private double rangeMin;
	private double rangeMax;

	public NumericTableRowColumnColoringModel(
			String column,
			ARGBLut lut,
			double rangeMin,
			double rangeMax )
	{
		this.column = column;
		this.lut = lut;
		this.min = rangeMin;
		this.max = rangeMax;
		this.rangeMin = rangeMin;
		this.rangeMax = rangeMax;
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
		return min;
	}


	@Override
	public double getMax()
	{
		return max;
	}


	@Override
	public void setMin( double min )
	{
		this.min = min;
		notifyColoringListeners();
	}

	@Override
	public void setMax( double max )
	{
		this.max = max;
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
		if ( max == min )
		{
			if ( max == rangeMin )
			{
				normalisedValue = 1.0;
			}
			else if ( max == rangeMax )
			{
				normalisedValue = 0.0;
			}
		}
		else
		{
			normalisedValue =
					Math.max(
							Math.min(
									( value - min )
											/ ( max - min ), 1.0 ), 0.0 );
		}
		return normalisedValue;
	}

}
