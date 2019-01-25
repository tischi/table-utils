package de.embl.cba.tables.modelview.coloring;

import de.embl.cba.bdv.utils.lut.ARGBLut;
import de.embl.cba.bdv.utils.lut.Luts;
import de.embl.cba.tables.modelview.objects.AnnotatedSegment;
import de.embl.cba.tables.modelview.objects.DefaultAnnotatedSegment;
import de.embl.cba.tables.modelview.selection.Listeners;
import net.imglib2.converter.Converter;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.volatiles.VolatileARGBType;

import static de.embl.cba.bdv.utils.converters.RandomARGBConverter.goldenRatio;

public class DefaultAnnotatedSegmentsColoringModel
		implements FeatureColoringModel< AnnotatedSegment >
{

	private final Listeners.SynchronizedList< ColoringListener > listeners;
	private String coloringFeature;

	public DefaultAnnotatedSegmentsColoringModel( String coloringFeature )
	{
		this.listeners = new Listeners.SynchronizedList< ColoringListener >(  );
		this.coloringFeature = coloringFeature;
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
	public void setColoringFeature(
			String coloringFeature,
			ARGBLut lut,
			double rangeMin,
			double rangeMax )
	{
		// TODO
	}

	@Override
	public void convert( AnnotatedSegment annotatedSegment, ARGBType output )
	{
		final Object featureValue = annotatedSegment.featureValue( coloringFeature );

		// TODO: implement well
		final double random = createRandom( annotatedSegment.getLabel() );

		output.set( Luts.getARGBIndex( ( byte ) ( 255.0 * random ), Luts.GLASBEY ) );
	}

	public double createRandom( double x )
	{
		double random = ( x * 50 ) * goldenRatio;
		random = random - ( long ) Math.floor( random );
		return random;
	}


}
