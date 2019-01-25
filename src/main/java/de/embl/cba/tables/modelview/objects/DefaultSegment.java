package de.embl.cba.tables.modelview.objects;

import de.embl.cba.bdv.utils.selection.Segment;
import net.imglib2.RealInterval;
import net.imglib2.roi.labeling.LabelRegion;

public class DefaultSegment implements Segment
{
	private final double label;
	private final int timePoint;
	private final double[] position;
	private final RealInterval boundingBox;

	public DefaultSegment( double label, int timePoint, double[] position, RealInterval boundingBox )
	{
		this.label = label;
		this.timePoint = timePoint;
		this.position = position;
		this.boundingBox = boundingBox;

		final LabelRegion labelRegion = new LabelRegion();
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
	public RealInterval getBoundingBox()
	{
		return boundingBox;
	}
}
