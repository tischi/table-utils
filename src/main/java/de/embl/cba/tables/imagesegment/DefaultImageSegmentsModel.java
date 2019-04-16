package de.embl.cba.tables.imagesegment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultImageSegmentsModel< T extends ImageSegment > implements ImageSegmentsModel< T >
{
	private Map< LabelFrameAndImage, T > idToSegment;
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
			final LabelFrameAndImage key = new LabelFrameAndImage( imageSegment );
			idToSegment.put( key, imageSegment );
		}
	}

	@Override
	public T getImageSegment( LabelFrameAndImage labelFrameAndImage )
	{
		return idToSegment.get( labelFrameAndImage );
	}

	@Override
	public String getName()
	{
		return modelName;
	}
}
