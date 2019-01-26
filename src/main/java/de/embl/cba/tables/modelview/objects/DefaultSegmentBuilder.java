package de.embl.cba.tables.modelview.objects;

import net.imglib2.FinalInterval;

public class DefaultSegmentBuilder
{
	private String imageId = "";
	private double label = 0;
	private int timePoint = 0;
	private double x = 0.0;
	private double y = 0.0;
	private double z = 0.0;
	private FinalInterval boundingBox = null;

	public DefaultImageSegment build()
	{
		final DefaultImageSegment defaultImageSegment = new DefaultImageSegment(
				imageId,
				label,
				timePoint,
				x,
				y,
				z,
				boundingBox );
		return defaultImageSegment;
	}

	public DefaultSegmentBuilder setImageId( String imageId )
	{
		this.imageId = imageId;
		return this;
	}

	public DefaultSegmentBuilder setLabel( double label )
	{
		this.label = label;
		return this;
	}

	public DefaultSegmentBuilder setTimePoint( int timePoint )
	{
		this.timePoint = timePoint;
		return this;
	}


	public DefaultSegmentBuilder setBoundingBox( FinalInterval boundingBox )
	{
		this.boundingBox = boundingBox;
		return this;
	}

	public DefaultSegmentBuilder setX( double x )
	{
		this.x = x;
		return this;
	}

	public DefaultSegmentBuilder setY( double y )
	{
		this.y = y;
		return this;
	}

	public DefaultSegmentBuilder setZ( double z )
	{
		this.z = z;
		return this;
	}
}
