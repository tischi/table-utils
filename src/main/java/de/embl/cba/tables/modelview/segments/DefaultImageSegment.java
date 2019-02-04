package de.embl.cba.tables.modelview.segments;

import net.imglib2.FinalInterval;

public class DefaultImageSegment implements ImageSegment
{
	private final FinalInterval boundingBox;
	private final double[] position;
	private final String imageId;
	private final double labelId;
	private final int timePoint;

	public DefaultImageSegment(
			String imageId,
			double labelId,
			int timePoint,
			double x,
			double y,
			double z,
			FinalInterval boundingBox )
	{
		this.imageId = imageId;
		this.labelId = labelId;
		this.timePoint = timePoint;
		this.boundingBox = boundingBox;
		this.position = new double[]{ x, y, z };
	}

	@Override
	public String imageId()
	{
		return imageId;
	}

	@Override
	public double labelId()
	{
		return labelId;
	}

	@Override
	public int timePoint()
	{
		return timePoint;
	}

	@Override
	public FinalInterval boundingBox()
	{
		return boundingBox;
	}

	@Override
	public void localize( float[] position )
	{
		for ( int d = 0; d < position.length; d++ )
		{
			position[ d ] = (float) this.position[ d ];
		}
	}

	@Override
	public void localize( double[] position )
	{
		for ( int d = 0; d < position.length; d++ )
		{
			position[ d ] = this.position[ d ];
		}
	}

	@Override
	public float getFloatPosition( int d )
	{
		return (float) position[ d ];
	}

	@Override
	public double getDoublePosition( int d )
	{
		return position[ d ];
	}

	@Override
	public int numDimensions()
	{
		return position.length;
	}

}
