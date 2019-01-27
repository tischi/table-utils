package de.embl.cba.tables.modelview.objects;

import net.imglib2.FinalInterval;

public class DefaultImageSegment implements ImageSegment
{
	private final String imageSetId;
	private final double label;
	private final int timePoint;
	private final FinalInterval boundingBox;
	private final double[] position;

	public DefaultImageSegment(
			String imageSetId,
			double label,
			int timePoint,
			double x,
			double y,
			double z,
			FinalInterval boundingBox )
	{
		this.imageSetId = imageSetId;
		this.label = label;
		this.timePoint = timePoint;
		this.boundingBox = boundingBox;
		this.position = new double[]{x,y,z};
	}

	@Override
	public String imageSetName()
	{
		return imageSetId;
	}

	@Override
	public double label()
	{
		return label;
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
