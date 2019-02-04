package de.embl.cba.tables.modelview.segments;

import java.util.Objects;

public class ImageSegmentId
{
	private final String imageId;
	private final double label;
	private final int timePoint;

	public ImageSegmentId( String imageId, double label, int timePoint )
	{
		this.imageId = imageId;
		this.label = label;
		this.timePoint = timePoint;
	}

	@Override
	public boolean equals( Object o )
	{
		if ( this == o ) return true;
		if ( o == null || getClass() != o.getClass() ) return false;
		ImageSegmentId that = ( ImageSegmentId ) o;
		return Double.compare( that.label, label ) == 0 &&
				timePoint == that.timePoint &&
				Objects.equals( imageId, that.imageId );
	}

	@Override
	public int hashCode()
	{
		return Objects.hash( imageId, label, timePoint );
	}

	public String getImageId()
	{
		return imageId;
	}

	public double getLabel()
	{
		return label;
	}

	public int getTimePoint()
	{
		return timePoint;
	}
}
