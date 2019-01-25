package de.embl.cba.tables.modelview.coloring;

import de.embl.cba.bdv.utils.lut.ARGBLut;
import de.embl.cba.bdv.utils.lut.GlasbeyARGBLut;
import de.embl.cba.tables.TableUtils;
import de.embl.cba.tables.modelview.objects.AnnotatedSegment;
import de.embl.cba.tables.modelview.selection.Listeners;
import net.imglib2.type.numeric.ARGBType;

import java.util.ArrayList;

import static de.embl.cba.bdv.utils.converters.RandomARGBConverter.goldenRatio;

public class DefaultAnnotatedSegmentsColoringModel
		implements FeatureColoringModel< AnnotatedSegment >
{

	private final Listeners.SynchronizedList< ColoringListener > listeners;
	private String coloringFeature;
	private ARGBLut lut;
	private ColoringMode coloringMode;
	private double min;
	private double max;
	private ArrayList< Object > featureValues;
	private double initialMin;
	private double initialMax;

	public DefaultAnnotatedSegmentsColoringModel( String coloringFeature )
	{
		this.listeners = new Listeners.SynchronizedList< ColoringListener >(  );
		this.coloringFeature = coloringFeature;
		this.lut = new GlasbeyARGBLut();
		this.coloringMode = ColoringMode.Categorical;
		this.min = 0.0;
		this.max = 1.0;
		this.featureValues = new ArrayList<>(  );
	}

	public void notifyColoringListeners()
	{
		for ( ColoringListener listener : listeners.list )
		{
			listener.coloringChanged();
		}
	}

	@Override
	public Listeners< ColoringListener > listeners()
	{
		return listeners;
	}

	@Override
	public void setCategoricalColoring( String coloringFeature, ARGBLut lut )
	{
		this.coloringMode = ColoringMode.Categorical;
		this.coloringFeature = coloringFeature;
		this.lut = lut;

		this.featureValues = new ArrayList<>(  );

		notifyColoringListeners();
	}

	@Override
	public void setLinearColoring( String coloringFeature, ARGBLut lut, double min, double max )
	{
		this.coloringMode = ColoringMode.Linear;
		this.coloringFeature = coloringFeature;
		this.lut = lut;

		this.min = min;
		this.max = max;
		this.initialMin = min;
		this.initialMax = max;

		notifyColoringListeners();
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

	@Override
	public String getColoringFeature()
	{
		return coloringFeature;
	}

	@Override
	public void convert( AnnotatedSegment annotatedSegment,
						 ARGBType output )
	{
		final Object featureValue = annotatedSegment.features().get( coloringFeature );

		if ( coloringMode.equals( ColoringMode.Categorical ) )
		{
			setColorCategorically( output, featureValue );
		}
		else if ( coloringMode.equals( ColoringMode.Linear ) )
		{
			setColorLinearly( output, featureValue );
		}
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
			if ( max == initialMin )
			{
				normalisedValue = 1.0;
			}
			else if ( max == initialMax )
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

	public void setColorCategorically( ARGBType output, Object featureValue )
	{
		if( ! featureValues.contains( featureValue ) ) featureValues.add( featureValue );

		final double random = createRandom( featureValues.indexOf( featureValue ) + 1 );

		output.set( lut.getARGB( random ) );
	}

	public double createRandom( double x )
	{
		double random = ( x * 50 ) * goldenRatio;
		random = random - ( long ) Math.floor( random );
		return random;
	}


}
