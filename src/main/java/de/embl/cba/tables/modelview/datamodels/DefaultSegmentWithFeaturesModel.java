package de.embl.cba.tables.modelview.datamodels;

import de.embl.cba.tables.modelview.objects.SegmentWithFeatures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds all the data
 */
public class DefaultSegmentWithFeaturesModel
{
	private final String name;
	private final ArrayList< ? extends SegmentWithFeatures > segmentWithFeatures;
	private final LabelImageSourceModel labelImageSourceModel;
	private final String labelFeatureName;
	private final String timePointFeatureName;

	private Map< Object, SegmentWithFeatures > labelTimePointKeyToSegmentMap;

	public DefaultSegmentWithFeaturesModel(
			String name,
			ArrayList< ? extends SegmentWithFeatures > segmentWithFeatures,
			String labelFeatureName,
			String timePointFeatureName,
			LabelImageSourceModel labelImageSourceModel )
	{
		this.name = name;
		this.segmentWithFeatures = segmentWithFeatures;
		this.labelImageSourceModel = labelImageSourceModel;
		this.timePointFeatureName = timePointFeatureName;
		this.labelFeatureName = labelFeatureName;

		createKeyMap();
	}

	private void createKeyMap()
	{
		labelTimePointKeyToSegmentMap = new HashMap<>();

		for ( SegmentWithFeatures segmentWithFeatures : this.segmentWithFeatures )
		{
			final Object key = SegmentUtils.getKey(
					segmentWithFeatures.getLabel(),
					segmentWithFeatures.getTimePoint()
			);

			labelTimePointKeyToSegmentMap.put( key, segmentWithFeatures );
		}
	}

	public SegmentWithFeatures getSegment( int listIndex )
	{
		return segmentWithFeatures.get( listIndex );
	}

	public SegmentWithFeatures getSegment( Double label, int timePoint  )
	{
		final Object segmentKey = getSegmentKey( label, timePoint );
		return labelTimePointKeyToSegmentMap.get( segmentKey );
	}

	public LabelImageSourceModel getLabelImageSourceModel()
	{
		return labelImageSourceModel;
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

//	public Map< Object, SegmentWithFeatures > getLabelTimePointKeyToSegmentMap()
//	{
//		return labelTimePointKeyToSegmentMap;
//	}

//	public ArrayList< String > getFeatureNames()
//	{
//		return featureNames;
//	}

	public ArrayList< ? extends SegmentWithFeatures > getSegmentWithFeatures()
	{
		return segmentWithFeatures;
	}

	public String getName()
	{
		return name;
	}
}
