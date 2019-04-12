package de.embl.cba.tables.modelview.combined;

import de.embl.cba.tables.modelview.segments.DefaultImageSegment;
import de.embl.cba.tables.modelview.segments.ImageSegmentId;

import java.util.HashMap;
import java.util.Map;

public class LazyImageSegmentsModel
		implements ImageSegmentsModel< DefaultImageSegment >
{
	private final Map< ImageSegmentId, DefaultImageSegment > keyToSegment;
	private String modelName;

	public LazyImageSegmentsModel( String modelName )
	{
		this.modelName = modelName;
		keyToSegment = new HashMap<>(  );
	}

	@Override
	public DefaultImageSegment getImageSegment( ImageSegmentId imageSegmentId )
	{
		if ( ! keyToSegment.keySet().contains( imageSegmentId ) )
		{
			addSegment( imageSegmentId );
		}

		return keyToSegment.get( imageSegmentId );

	}

	@Override
	public String getName()
	{
		return modelName;
	}

	private synchronized void addSegment( ImageSegmentId imageSegmentId )
	{
		final DefaultImageSegment imageSegment = new DefaultImageSegment(
				imageSegmentId.getImageId(),
				imageSegmentId.getLabelId(),
				imageSegmentId.getTimePoint(),
				0,0,0,
				null );

		keyToSegment.put( imageSegmentId, imageSegment );
	}

}
