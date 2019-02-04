package de.embl.cba.tables.modelview.segments;

import net.imglib2.FinalInterval;

import java.util.Objects;

public class DefaultImageSegment implements ImageSegment
{
	private final ImageSegmentId imageSegmentId;
	private final FinalInterval boundingBox;
	private final double[] position;

	public DefaultImageSegment(
			ImageSegmentId imageSegmentId,
			double x,
			double y,
			double z,
			FinalInterval boundingBox )
	{
		this.imageSegmentId = imageSegmentId;
		this.boundingBox = boundingBox;
		this.position = new double[]{ x, y, z };
	}

	@Override
	public ImageSegmentId getImageSegmentId()
	{
		return imageSegmentId;
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
