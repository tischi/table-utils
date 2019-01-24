package de.embl.cba.tables.modelview.datamodels;

import de.embl.cba.tables.modelview.objects.Segment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds all the data
 */
public class SegmentModel< T extends Segment >
{
	private final String name;
	private final ArrayList< T > segments;
	private final ArrayList< String > featureNames;
	private final LabelImageSource labelImageSource;
	private final String labelFeatureName;
	private final String timePointFeatureName;

	private Map< Object, T > labelTimePointKeyToSegmentMap;

	public SegmentModel(
			String name, ArrayList< T > segments,
			ArrayList< String > featureNames,
			String labelFeatureName,
			String timePointFeatureName,
			LabelImageSource labelImageSource )
	{
		this.name = name;
		this.segments = segments;
		this.featureNames = featureNames;
		this.labelImageSource = labelImageSource;

		createKeyMap();

		this.timePointFeatureName = timePointFeatureName;
		this.labelFeatureName = labelFeatureName;
	}

	private void createKeyMap()
	{
		labelTimePointKeyToSegmentMap = new HashMap<>();

		for ( T segment : segments )
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

	public String getTimePointFeatureName()
	{
		return timePointFeatureName;
	}

	public String getLabelFeatureName()
	{
		return labelFeatureName;
	}

	public Map< Object, T > getLabelTimePointKeyToSegmentMap()
	{
		return labelTimePointKeyToSegmentMap;
	}

	public ArrayList< String > getFeatureNames()
	{
		return featureNames;
	}

	public ArrayList< T > getSegments()
	{
		return segments;
	}

	public String getName()
	{
		return name;
	}
}
