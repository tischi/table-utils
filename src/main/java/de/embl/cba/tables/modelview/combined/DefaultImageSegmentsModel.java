package de.embl.cba.tables.modelview.combined;

import de.embl.cba.tables.modelview.segments.ImageSegment;
import de.embl.cba.tables.modelview.segments.ImageSegmentId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultImageSegmentsModel< T extends ImageSegment > implements ImageSegmentsModel< T >
{
	private Map< ImageSegmentId, T > idToSegment;
	private String modelName;

	public DefaultImageSegmentsModel( List< T > imageSegments, String modelName )
	{
		this.modelName = modelName;
		createSegmentMap( imageSegments );
	}

	public void createSegmentMap( List< T > imageSegments )
	{
		idToSegment = new HashMap<>();
		for ( T imageSegment : imageSegments )
		{
			final ImageSegmentId key = new ImageSegmentId( imageSegment );
			idToSegment.put( key, imageSegment );
		}
	}

	@Override
	public T getImageSegment( ImageSegmentId imageSegmentId )
	{
		return idToSegment.get( imageSegmentId );
	}

	@Override
	public String getName()
	{
		return modelName;
	}
}
