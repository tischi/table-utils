package de.embl.cba.tables.modelview.views;

import bdv.viewer.TimePointListener;
import de.embl.cba.tables.modelview.coloring.ColoringModel;
import de.embl.cba.tables.modelview.datamodels.ImagesAndSegmentsModel;
import de.embl.cba.tables.modelview.objects.ImageSegment;
import net.imglib2.Volatile;
import net.imglib2.converter.Converter;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.volatiles.VolatileARGBType;

public class ImageSegmentLabelsARGBConverter< T extends ImageSegment >
		implements Converter< RealType, VolatileARGBType >, TimePointListener
{
	private final ImagesAndSegmentsModel< T > segmentsModel;
	private final ColoringModel< T > coloringModel;

	private int timePointIndex;

	public ImageSegmentLabelsARGBConverter(
			ImagesAndSegmentsModel< T > segmentsModel,
			ColoringModel< T > coloringModel )
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
				segmentsModel.getSegment( label.getRealDouble(), timePointIndex ),
				color.get() );

	}

	@Override
	public void timePointChanged( int timePointIndex )
	{
		this.timePointIndex = timePointIndex;
	}
}
