package de.embl.cba.tables.modelview.views;

import bdv.viewer.TimePointListener;
import de.embl.cba.tables.modelview.coloring.ColoringModel;
import de.embl.cba.tables.modelview.datamodels.AnnotatedSegmentsModel;
import de.embl.cba.tables.modelview.objects.AnnotatedImageSegment;
import net.imglib2.Volatile;
import net.imglib2.converter.Converter;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.volatiles.VolatileARGBType;

public class AnnotatedSegmentLabelsARGBConverter
		implements Converter< RealType, VolatileARGBType >, TimePointListener
{
	private final AnnotatedSegmentsModel segmentsModel;
	private final ColoringModel< AnnotatedImageSegment > coloringModel;

	private int timePointIndex;

	public AnnotatedSegmentLabelsARGBConverter(
			AnnotatedSegmentsModel segmentsModel,
			ColoringModel< AnnotatedImageSegment > coloringModel )
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
				getAnnotatedSegment( label, timePointIndex ),
				color.get() );

	}

	public AnnotatedImageSegment getAnnotatedSegment( RealType label, int timePointIndex )
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
