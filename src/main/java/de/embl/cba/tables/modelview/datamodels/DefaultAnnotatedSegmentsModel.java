package de.embl.cba.tables.modelview.datamodels;

import de.embl.cba.tables.modelview.objects.AnnotatedSegment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds all the data
 */
public class DefaultAnnotatedSegmentsModel
{
	private final String name;
	private final ArrayList< ? extends AnnotatedSegment > segmentWithFeatures;
	private final LabelImageSourceModel labelImageSourceModel;
	private final String labelFeatureName;
	private final String timePointFeatureName;

	private Map< Object, AnnotatedSegment > labelTimePointKeyToSegmentMap;

	public DefaultAnnotatedSegmentsModel(
			String name,
			ArrayList< ? extends AnnotatedSegment > segmentWithFeatures,
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

		for ( AnnotatedSegment annotatedSegment : this.segmentWithFeatures )
		{
			final Object key = SegmentUtilsDELETE.getKey(
					annotatedSegment.getLabel(),
					annotatedSegment.getTimePoint()
			);

			labelTimePointKeyToSegmentMap.put( key, annotatedSegment );
		}
	}

	public AnnotatedSegment getSegment( int listIndex )
	{
		return segmentWithFeatures.get( listIndex );
	}

	public AnnotatedSegment getSegment( Double label, int timePoint  )
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

	public ArrayList< ? extends AnnotatedSegment > getSegmentWithFeatures()
	{
		return segmentWithFeatures;
	}

	public String getName()
	{
		return name;
	}
}
