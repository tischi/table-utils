package de.embl.cba.tables.modelview.objects;

import de.embl.cba.bdv.utils.selection.Segment;
import net.imglib2.FinalInterval;
import net.imglib2.roi.labeling.LabelRegion;

public class DefaultSegment implements Segment
{
	private final String imageId;
	private final double label;
	private final int timePoint;
	private final FinalInterval boundingBox;
	private final double[] position;


	public DefaultSegment(
			String imageId,
			double label,
			int timePoint,
			double x,
			double y,
			double z,
			FinalInterval boundingBox )
	{
		this.imageId = imageId;
		this.label = label;
		this.timePoint = timePoint;
		this.boundingBox = boundingBox;
		this.position = new double[]{x,y,z};
	}

	@Override
	public String getImageId()
	{
		return imageId;
	}

	@Override
	public double getLabel()
	{
		return label;
	}

	@Override
	public int getTimePoint()
	{
		return timePoint;
	}

	@Override
	public double[] getPosition()
	{
		return position;
	}

	@Override
	public FinalInterval getBoundingBox()
	{
		return boundingBox;
	}
}
