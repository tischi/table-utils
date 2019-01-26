package de.embl.cba.tables.modelview.objects;

import net.imglib2.FinalInterval;

public class DefaultImageSegmentBuilder
{
	private String imageId = getDefaultImageId();
	private double label = getDefaultLabel();
	private int timePoint = getDefaultTimePoint();
	private double x = getDefaultX();
	private double y = getDefaultY();
	private double z = getDefaultZ();
	private FinalInterval boundingBox = getDefaultBoundingBox();

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

	public DefaultImageSegmentBuilder setImageId( String imageId )
	{
		this.imageId = imageId;
		return this;
	}

	public DefaultImageSegmentBuilder setLabel( double label )
	{
		this.label = label;
		return this;
	}

	public DefaultImageSegmentBuilder setTimePoint( int timePoint )
	{
		this.timePoint = timePoint;
		return this;
	}


	public DefaultImageSegmentBuilder setBoundingBox( FinalInterval boundingBox )
	{
		this.boundingBox = boundingBox;
		return this;
	}

	public DefaultImageSegmentBuilder setX( double x )
	{
		this.x = x;
		return this;
	}

	public DefaultImageSegmentBuilder setY( double y )
	{
		this.y = y;
		return this;
	}

	public DefaultImageSegmentBuilder setZ( double z )
	{
		this.z = z;
		return this;
	}

	public static String getDefaultImageId()
	{
		return "";
	}

	public static double getDefaultLabel()
	{
		return 1;
	}

	public static int getDefaultTimePoint()
	{
		return 0;
	}

	public static double getDefaultX()
	{
		return 0.0;
	}

	public static double getDefaultY()
	{
		return 0.0;
	}

	public static double getDefaultZ()
	{
		return 0.0;
	}

	public static FinalInterval getDefaultBoundingBox()
	{
		return null;
	}
}
