package de.embl.cba.tables.modelview.objects;

import de.embl.cba.bdv.utils.selection.Segment;
import net.imglib2.FinalInterval;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DefaultAnnotatedSegment implements AnnotatedSegment
{
	private final LinkedHashMap< String, Object > featureMap;
	private final Segment segment;

	public DefaultAnnotatedSegment( Segment segment,
									LinkedHashMap< String, Object > featureMap )
	{
		this.segment = segment;
		this.featureMap = featureMap;
	}

	@Override
	public String getImageId()
	{
		return segment.getImageId();
	}

	@Override
	public double getLabel()
	{
		return segment.getLabel();
	}

	@Override
	public int getTimePoint()
	{
		return segment.getTimePoint();
	}

	@Override
	public double[] getPosition()
	{
		return segment.getPosition();
	}

	@Override
	public FinalInterval getBoundingBox()
	{
		return segment.getBoundingBox();
	}

	@Override
	public LinkedHashMap< String, Object > features()
	{
		return featureMap;
	}
}
