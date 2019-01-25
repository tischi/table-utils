package de.embl.cba.tables.modelview.views;

import bdv.viewer.TimePointListener;
import de.embl.cba.tables.modelview.coloring.FeatureColoringModel;
import de.embl.cba.tables.modelview.datamodels.DefaultAnnotatedSegmentsModel;
import de.embl.cba.tables.modelview.objects.AnnotatedSegment;
import net.imglib2.Volatile;
import net.imglib2.converter.Converter;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.volatiles.VolatileARGBType;

public class AnnotatedSegmentsColoringARGBConverter
		implements Converter< RealType, VolatileARGBType >, TimePointListener
{
	private final DefaultAnnotatedSegmentsModel segmentsModel;
	private final FeatureColoringModel< AnnotatedSegment > coloringModel;

	private int timePointIndex;

	public AnnotatedSegmentsColoringARGBConverter(
			DefaultAnnotatedSegmentsModel segmentsModel,
			FeatureColoringModel< AnnotatedSegment > coloringModel )
	{
		this.segmentsModel = segmentsModel;
		this.coloringModel = coloringModel;
		timePointIndex = 0;
	}

	@Override
	public void convert( RealType label, VolatileARGBType color )
	{
		if ( label instanceof Volatile )
		{
			if ( ! ( ( Volatile ) label ).isValid() )
			{
				color.setValid( false );
				return;
			}
		}

		if ( label.getRealDouble() == 0 )
		{
			color.setValid( true );
			color.set( 0 );
			return;
		}

		coloringModel.convert(
				getAnnotatedSegment( label ),
				color.get() );

	}

	public AnnotatedSegment getAnnotatedSegment( RealType label )
	{
		return segmentsModel.getSegment(
				label.getRealDouble(),
				timePointIndex );
	}

	@Override
	public void timePointChanged( int timePointIndex )
	{
		this.timePointIndex = timePointIndex;
	}
}
