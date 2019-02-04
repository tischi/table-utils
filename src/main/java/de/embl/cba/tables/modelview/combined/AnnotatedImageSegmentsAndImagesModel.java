package de.embl.cba.tables.modelview.combined;

import de.embl.cba.tables.modelview.images.ImageSourcesModel;
import de.embl.cba.tables.modelview.segments.AnnotatedImageSegment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AnnotatedImageSegmentsAndImagesModel< T extends AnnotatedImageSegment >
	implements ImagesAndSegmentsModel< T >, TableRowsModel< T >
{
	private final String name;
	private final ArrayList< T > annotatedImageSegments;
	private final ImageSourcesModel imageSourcesModel;

	private Map< Object, T > keyToSegmentMap;

	public AnnotatedImageSegmentsAndImagesModel(
			String name,
			ArrayList< T > annotatedImageSegments,
			ImageSourcesModel imageSourcesModel )
	{
		this.name = name;
		this.annotatedImageSegments = annotatedImageSegments;
		this.imageSourcesModel = imageSourcesModel;

		createKeyMap();
	}

	private void createKeyMap()
	{
		keyToSegmentMap = new HashMap<>();

		for ( T annotatedImageSegment : this.annotatedImageSegments )
		{
			final Object key = getSegmentKey(
					annotatedImageSegment.imageId(),
					annotatedImageSegment.label(),
					annotatedImageSegment.timePoint()
			);

			keyToSegmentMap.put( key, annotatedImageSegment );
		}
	}

	@Override
	public T getSegment( String imageSetName, Double label, int timePoint )
	{
		final Object segmentKey = getSegmentKey( imageSetName, label, timePoint );
		return keyToSegmentMap.get( segmentKey );
	}

	public ImageSourcesModel getImageSourcesModel()
	{
		return imageSourcesModel;
	}

	public static Object getSegmentKey( String imageSetName, Double label, Integer timePoint )
	{
		return imageSetName + "_L"+label.toString() + "_T" + timePoint.toString();
	}

	@Override
	public ArrayList< T > getTableRows()
	{
		return annotatedImageSegments;
	}

	public String getName()
	{
		return name;
	}
}
