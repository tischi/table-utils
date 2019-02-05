package de.embl.cba.tables.modelview.combined;

import de.embl.cba.tables.modelview.segments.ImageSegment;
import de.embl.cba.tables.modelview.segments.ImageSegmentId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DefaultImageSegmentsModel< T extends ImageSegment > implements ImageSegmentsModel< T >
{
	private final Map< ImageSegmentId, T > idToSegment;

	public DefaultImageSegmentsModel( ArrayList< T > imageSegments )
	{
		idToSegment = new HashMap<>();

		for ( T imageSegment : imageSegments )
		{
			idToSegment.put( new ImageSegmentId( imageSegment ), imageSegment );
		}

	}

	@Override
	public T getImageSegment( ImageSegmentId imageSegmentId )
	{
		return idToSegment.get( imageSegmentId );
	}
}
