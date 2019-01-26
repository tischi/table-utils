package de.embl.cba.tables.modelview.datamodels;

import de.embl.cba.tables.modelview.objects.AnnotatedImageSegment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AnnotatedImageSegmentsAndImagesModel< T extends AnnotatedImageSegment >
	implements ImagesAndSegmentsModel< T >, TableRowsModel< T >
{
	private final String name;
	private final ArrayList< T > annotatedImageSegments;
	private final ImageSourcesModel imageSourcesModel;
	private final String labelFeatureName;

	private Map< Object, T > labelTimePointKeyToSegmentMap;

	public AnnotatedImageSegmentsAndImagesModel(
			String name,
			ArrayList< T > annotatedImageSegments,
			String labelFeatureName,
			String timePointFeatureName,
			ImageSourcesModel imageSourcesModel )
	{
		this.name = name;
		this.labelFeatureName = labelFeatureName;
		this.annotatedImageSegments = annotatedImageSegments;
		this.imageSourcesModel = imageSourcesModel;

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

	public T getSegment( int listIndex )
	{
		return annotatedImageSegments.get( listIndex );
	}

	@Override
	public T getSegment( Double label, int timePoint )
	{
		final Object segmentKey = getSegmentKey( label, timePoint );
		return labelTimePointKeyToSegmentMap.get( segmentKey );
	}

	public ImageSourcesModel getImageSourcesModel()
	{
		return imageSourcesModel;
	}

	@Override
	public ArrayList< T > getImageSegments()
	{
		return annotatedImageSegments;
	}

	public static Object getSegmentKey( Double label, Integer timePoint )
	{
		return "L"+label.toString() + "_T" + timePoint.toString();
	}

	@Override
	public ArrayList< T > getTableRows()
	{
		return annotatedImageSegments;
	}
}
