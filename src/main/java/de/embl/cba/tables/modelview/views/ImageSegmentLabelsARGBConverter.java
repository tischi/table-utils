package de.embl.cba.tables.modelview.views;

import bdv.viewer.TimePointListener;
import de.embl.cba.tables.modelview.coloring.ColoringModel;
import de.embl.cba.tables.modelview.combined.ImagesAndSegmentsModel;
import de.embl.cba.tables.modelview.segments.ImageSegment;
import net.imglib2.Volatile;
import net.imglib2.converter.Converter;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.volatiles.VolatileARGBType;

public class ImageSegmentLabelsARGBConverter< T extends ImageSegment >
		implements Converter< RealType, VolatileARGBType >, TimePointListener
{
	private final ImagesAndSegmentsModel< T > segmentsModel;
	private final String imageSetId;
	private final ColoringModel< T > coloringModel;

	private int timePointIndex;

	public ImageSegmentLabelsARGBConverter(
			ImagesAndSegmentsModel< T > segmentsModel,
			String imageSetId,
			ColoringModel< T > coloringModel )
	{
		this.segmentsModel = segmentsModel;
		this.imageSetId = imageSetId;
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
				segmentsModel.getSegment( imageSetId, label.getRealDouble(), timePointIndex ),
				color.get() );

	}

	@Override
	public void timePointChanged( int timePointIndex )
	{
		this.timePointIndex = timePointIndex;
	}
}
