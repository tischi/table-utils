package de.embl.cba.tables.modelview.datamodels;

import de.embl.cba.tables.modelview.objects.AnnotatedImageSegment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds all the data
 */

public class AnnotatedImageSegmentsModel < T extends AnnotatedImageSegment >
		implements ImageSegmentsModel, TableRowsModel
{
	private final String name;
	private final ArrayList< T > annotatedImageSegments;
	private final LabelImageSourceModel labelImageSourceModel;
	private final String labelFeatureName;
	private final String timePointFeatureName;

	private Map< Object, T > labelTimePointKeyToSegmentMap;

	public AnnotatedImageSegmentsModel(
			String name,
			ArrayList< T > annotatedImageSegments,
			String labelFeatureName,
			String timePointFeatureName,
			LabelImageSourceModel labelImageSourceModel )
	{
		this.name = name;
		this.annotatedImageSegments = annotatedImageSegments;
		this.labelImageSourceModel = labelImageSourceModel;
		this.timePointFeatureName = timePointFeatureName;
		this.labelFeatureName = labelFeatureName;

		createKeyMap();
	}

	private void createKeyMap()
	{
		labelTimePointKeyToSegmentMap = new HashMap<>();

		for ( T annotatedImageSegment : this.annotatedImageSegments )
		{
			final Object key = SegmentUtils.getKey(
					annotatedImageSegment.label(),
					annotatedImageSegment.timePoint()
			);

			labelTimePointKeyToSegmentMap.put( key, annotatedImageSegment );
		}
	}

	public AnnotatedImageSegment getSegment( int listIndex )
	{
		return annotatedImageSegments.get( listIndex );
	}

	@Override
	public AnnotatedImageSegment getSegment( Double label, int timePoint )
	{
		final Object segmentKey = getSegmentKey( label, timePoint );
		return labelTimePointKeyToSegmentMap.get( segmentKey );
	}

	@Override
	public LabelImageSourceModel getLabelImageSourceModel()
	{
		return labelImageSourceModel;
	}

	@Override
	public ArrayList< ? extends AnnotatedImageSegment > getImageSegments()
	{
		return annotatedImageSegments;
	}

	public static Object getSegmentKey( Double label, Integer timePoint )
	{
		return "L"+label.toString() + "_T" + timePoint.toString();
	}

	@Override
	public String getTimePointColumnName()
	{
		return timePointFeatureName;
	}

	@Override
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

	@Override
	public ArrayList< ? extends AnnotatedImageSegment > getTableRows()
	{
		return annotatedImageSegments;
	}

	public String getName()
	{
		return name;
	}
}
