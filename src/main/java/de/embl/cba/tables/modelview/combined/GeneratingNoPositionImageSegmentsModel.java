package de.embl.cba.tables.modelview.combined;

import bdv.util.BdvHandle;
import de.embl.cba.bdv.utils.BdvUtils;
import de.embl.cba.tables.modelview.images.Metadata;
import de.embl.cba.tables.modelview.images.SourceAndMetadata;
import de.embl.cba.tables.modelview.segments.DefaultImageSegment;
import de.embl.cba.tables.modelview.segments.ImageSegmentId;
import net.imglib2.RealPoint;

import java.util.HashMap;
import java.util.Map;

public class GeneratingNoPositionImageSegmentsModel
		implements ImageSegmentsModel< DefaultImageSegment >
{
	private final Map< ImageSegmentId, DefaultImageSegment > keyToSegment;

	public GeneratingNoPositionImageSegmentsModel( )
	{
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
