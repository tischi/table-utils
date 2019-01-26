package de.embl.cba.tables.modelview.coloring;

import de.embl.cba.bdv.utils.lut.ARGBLut;
import de.embl.cba.tables.TableUtils;
import de.embl.cba.tables.modelview.objects.AnnotatedImageSegment;
import de.embl.cba.tables.modelview.objects.TableRow;
import net.imglib2.type.numeric.ARGBType;

import static de.embl.cba.bdv.utils.converters.RandomARGBConverter.goldenRatio;


// TODO: extract abstract class NumericFeatureColoringModel
public class TableRowColumnColoringModel< T extends TableRow >
		extends AbstractColoringModel< T > implements NumericColoringModel< T >
{
	private String feature;
	private ARGBLut lut;
	private double min;
	private double max;
	private double rangeMin;
	private double rangeMax;

	public TableRowColumnColoringModel(
			String column,
			ARGBLut lut,
			double rangeMin,
			double rangeMax )
	{

		this.feature = column;
		this.lut = lut;
		this.min = rangeMin;
		this.max = rangeMax;
		this.rangeMin = rangeMin;
		this.rangeMax = rangeMax;
	}

	@Override
	public void convert( T input, ARGBType output )
	{
		final Object featureValue = input.cells().get( feature );
		setColorLinearly( output, featureValue );
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
		//notifyColoringListeners();
	}

	@Override
	public void setMax( double max )
	{
		this.max = max;
		//notifyColoringListeners();
	}


	public String getFeature()
	{
		return feature;
	}


	public void setColorLinearly( ARGBType output, Object featureValue )
	{
		final double value = TableUtils.asDouble( featureValue );
		double normalisedValue = computeLinearNormalisedValue( value );
		final int argb = lut.getARGB( normalisedValue );
		output.set( argb );
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

	public double createRandom( double x )
	{
		double random = ( x * 50 ) * goldenRatio;
		random = random - ( long ) Math.floor( random );
		return random;
	}
}
