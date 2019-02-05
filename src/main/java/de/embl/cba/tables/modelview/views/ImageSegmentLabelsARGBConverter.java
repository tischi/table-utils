package de.embl.cba.tables.modelview.views;

import bdv.viewer.TimePointListener;
import de.embl.cba.tables.modelview.coloring.ColoringModel;
import de.embl.cba.tables.modelview.combined.ImageSegmentsModel;
import de.embl.cba.tables.modelview.segments.ImageSegment;
import de.embl.cba.tables.modelview.segments.ImageSegmentId;
import net.imglib2.Volatile;
import net.imglib2.converter.Converter;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.volatiles.VolatileARGBType;

public class ImageSegmentLabelsARGBConverter< T extends ImageSegment >
		implements Converter< RealType, VolatileARGBType >, TimePointListener
{
	private final ImageSegmentsModel< T > imageSegmentsModel;
	private final String imageId;
	private final ColoringModel< T > coloringModel;

	private int timePointIndex;

	public ImageSegmentLabelsARGBConverter(
			ImageSegmentsModel< T > imageSegmentsModel,
			String imageId,
			ColoringModel coloringModel )
	{
		this.imageSegmentsModel = imageSegmentsModel;
		this.imageId = imageId;
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

		final ImageSegmentId imageSegmentId =
				new ImageSegmentId( imageId, label.getRealDouble(), timePointIndex );

		final T imageSegment = imageSegmentsModel.getImageSegment( imageSegmentId );

		coloringModel.convert( imageSegment, color.get() );

	}

	@Override
	public void timePointChanged( int timePointIndex )
	{
		this.timePointIndex = timePointIndex;
	}
}
