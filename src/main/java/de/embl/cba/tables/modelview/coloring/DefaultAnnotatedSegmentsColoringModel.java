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
	private double featureRangeMin;
	private double featureRangeMax;
	private ArrayList< Object > featureValues;

	public DefaultAnnotatedSegmentsColoringModel( String coloringFeature )
	{
		this.listeners = new Listeners.SynchronizedList< ColoringListener >(  );
		this.coloringFeature = coloringFeature;
		this.lut = new GlasbeyARGBLut();
		this.coloringMode = ColoringMode.Categorical;
		this.featureRangeMin = 0.0;
		this.featureRangeMax = 1.0;
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
	public void setColoringFeatureAndLut(
			String coloringFeature,
			ARGBLut lut,
			ColoringMode coloringMode,
			double featureRangeMin,
			double featureRangeMax )
	{
		this.coloringFeature = coloringFeature;
		this.lut = lut;
		this.coloringMode = coloringMode;
		this.featureRangeMin = featureRangeMin;
		this.featureRangeMax = featureRangeMax;
		this.featureValues = new ArrayList<>(  );

		notifyColoringListeners();
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
		double value = TableUtils.asDouble( featureValue );

		final double normalisedValue = Math.max( Math.min( ( value - featureRangeMin )
				/ ( featureRangeMax - featureRangeMin ), 1.0 ), 0.0 );

		output.set( lut.getARGBIndex( normalisedValue ) );
	}

	public void setColorCategorically( ARGBType output, Object featureValue )
	{
		if( ! featureValues.contains( featureValue ) ) featureValues.add( featureValue );

		final double random = createRandom( featureValues.indexOf( featureValue ) + 1 );

		output.set( lut.getARGBIndex( random ) );
	}

	public double createRandom( double x )
	{
		double random = ( x * 50 ) * goldenRatio;
		random = random - ( long ) Math.floor( random );
		return random;
	}


}
