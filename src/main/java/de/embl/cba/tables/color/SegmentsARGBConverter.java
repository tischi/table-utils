package de.embl.cba.tables.color;

import de.embl.cba.tables.imagesegment.ImageSegment;
import de.embl.cba.tables.imagesegment.LabelFrameAndImage;
import net.imglib2.Volatile;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.volatiles.VolatileARGBType;

import java.util.Map;

public class SegmentsARGBConverter< T extends ImageSegment >
		implements LabelsARGBConverter
{
	private final Map< LabelFrameAndImage, T > labelFrameAndImageToSegment;
	private final String imageId;
	private final ColoringModel< T > coloringModel;

	private int frame;

	public SegmentsARGBConverter(
			Map< LabelFrameAndImage, T > labelFrameAndImageToSegment,
			String imageId,
			ColoringModel coloringModel )
	{
		this.labelFrameAndImageToSegment = labelFrameAndImageToSegment;
		this.imageId = imageId;
		this.coloringModel = coloringModel;
		this.frame = 0;
	}

	@Override
	public void convert( RealType label, VolatileARGBType color )
	{
		if ( label instanceof Volatile )
		{
			if ( ! ( ( Volatile ) label ).isValid() )
			{
				color.set( 0 );
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

		final LabelFrameAndImage labelFrameAndImage =
				new LabelFrameAndImage( label.getRealDouble(), frame, imageId  );

		final T imageSegment = labelFrameAndImageToSegment.get( labelFrameAndImage );

		if ( imageSegment == null )
		{
			color.set( 0 );
			color.setValid( true );
		} else {
			coloringModel.convert( imageSegment, color.get() );
			color.setValid( true );
		}

	}

	@Override
	public void timePointChanged( int timePointIndex )
	{
		this.frame = timePointIndex;
	}
}
