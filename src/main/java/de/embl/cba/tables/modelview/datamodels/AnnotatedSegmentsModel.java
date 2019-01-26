package de.embl.cba.tables.modelview.datamodels;

import de.embl.cba.tables.modelview.objects.AnnotatedImageSegment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds all the data
 */

public class AnnotatedSegmentsModel
{
	private final String name;
	private final ArrayList< ? extends AnnotatedImageSegment > annotatedSegments;
	private final LabelImageSourceModel labelImageSourceModel;
	private final String labelFeatureName;
	private final String timePointFeatureName;

	private Map< Object, AnnotatedImageSegment > labelTimePointKeyToSegmentMap;

	public AnnotatedSegmentsModel(
			String name,
			ArrayList< ? extends AnnotatedImageSegment > annotatedSegments,
			String labelFeatureName,
			String timePointFeatureName,
			LabelImageSourceModel labelImageSourceModel )
	{
		this.name = name;
		this.annotatedSegments = annotatedSegments;
		this.labelImageSourceModel = labelImageSourceModel;
		this.timePointFeatureName = timePointFeatureName;
		this.labelFeatureName = labelFeatureName;

		createKeyMap();
	}

	private void createKeyMap()
	{
		labelTimePointKeyToSegmentMap = new HashMap<>();

		for ( AnnotatedImageSegment annotatedSegment : this.annotatedSegments )
		{
			final Object key = SegmentUtils.getKey(
					annotatedSegment.label(),
					annotatedSegment.timePoint()
			);

			labelTimePointKeyToSegmentMap.put( key, annotatedSegment );
		}
	}

	public AnnotatedImageSegment getSegment( int listIndex )
	{
		return annotatedSegments.get( listIndex );
	}

	public AnnotatedImageSegment getSegment( Double label, int timePoint  )
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

	public ArrayList< ? extends AnnotatedImageSegment > getAnnotatedSegments()
	{
		return annotatedSegments;
	}

	public String getName()
	{
		return name;
	}
}
