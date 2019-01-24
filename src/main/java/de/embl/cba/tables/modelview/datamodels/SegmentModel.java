package de.embl.cba.tables.modelview.datamodels;

import de.embl.cba.tables.modelview.DataSetTimePointLabel;
import de.embl.cba.tables.modelview.objects.Segment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds all the data
 */
public class SegmentModel< T extends Segment >
{
	private final ArrayList< T > segments;
	private final LabelImageSource labelImageSource;

	private Map< Object, T > labelTimePointKeyToSegmentMap;

	public SegmentModel(
			ArrayList< T > segments,
			LabelImageSource labelImageSource )
	{
		this.segments = segments;
		this.labelImageSource = labelImageSource;
		createKeyMap();
	}

	private void createKeyMap()
	{
		labelTimePointKeyToSegmentMap = new HashMap<>();

		for ( Segment segment : segments )
		{
			final Object key = SegmentUtils.getKey(
					segment.getLabel(),
					segment.getTimePoint()
			);

			labelTimePointKeyToSegmentMap.put( key, segment );
		}
	}

	public T getSegment( int listIndex )
	{
		return segments.get( listIndex );
	}

	public T getSegment( Double label, int timePoint  )
	{
		final Object segmentKey = getSegmentKey( label, timePoint );
		return labelTimePointKeyToSegmentMap.get( segmentKey );
	}

	public LabelImageSource getLabelImageSource()
	{
		return labelImageSource;
	}

	public static Object getSegmentKey( Double label, Integer timePoint )
	{
		return "L"+label.toString() + "_T" + timePoint.toString();
	}
}
