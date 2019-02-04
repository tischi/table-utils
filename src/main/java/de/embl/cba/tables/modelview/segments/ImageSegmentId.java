package de.embl.cba.tables.modelview.segments;

import java.util.Objects;

public class ImageSegmentId
{
	private final String imageId;
	private final double labelId;
	private final int timePoint;

	public ImageSegmentId( String imageId, double labelId, int timePoint )
	{
		this.imageId = imageId;
		this.labelId = labelId;
		this.timePoint = timePoint;
	}

	public ImageSegmentId( ImageSegment imageSegment )
	{
		this.imageId = imageSegment.imageId();
		this.labelId = imageSegment.labelId();
		this.timePoint = imageSegment.timePoint();
	}

	@Override
	public boolean equals( Object o )
	{
		if ( this == o ) return true;
		if ( o == null || getClass() != o.getClass() ) return false;
		ImageSegmentId that = ( ImageSegmentId ) o;
		return Double.compare( that.labelId, labelId ) == 0 &&
				timePoint == that.timePoint &&
				Objects.equals( imageId, that.imageId );
	}

	@Override
	public int hashCode()
	{
		return Objects.hash( imageId, labelId, timePoint );
	}

	public String getImageId()
	{
		return imageId;
	}

	public double getLabelId()
	{
		return labelId;
	}

	public int getTimePoint()
	{
		return timePoint;
	}
}
