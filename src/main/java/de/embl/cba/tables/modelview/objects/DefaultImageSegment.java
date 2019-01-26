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
	public String imageSetId()
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
	public double[] position()
	{
		return position;
	}

	@Override
	public FinalInterval boundingBox()
	{
		return boundingBox;
	}
}
