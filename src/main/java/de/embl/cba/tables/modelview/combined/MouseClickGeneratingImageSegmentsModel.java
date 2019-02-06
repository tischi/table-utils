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

public class MouseClickGeneratingImageSegmentsModel
		implements ImageSegmentsModel< DefaultImageSegment >
{
	private final Map< ImageSegmentId, DefaultImageSegment > keyToSegment;
	private BdvHandle bdv;
	private SourceAndMetadata labelSourceAndMetadata;

	public MouseClickGeneratingImageSegmentsModel( )
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
		final String imageId = ( String ) labelSourceAndMetadata.metadata().getMap().get( Metadata.DISPLAY_NAME );

		final RealPoint userClickCoordinate = BdvUtils.getGlobalMouseCoordinates( bdv );
		final int timepoint = bdv.getBdvHandle().getViewerPanel().getState().getCurrentTimepoint();
		final double labelId = BdvUtils.getValueAtGlobalCoordinates(
				labelSourceAndMetadata.source(),
				userClickCoordinate,
				timepoint );

		final DefaultImageSegment imageSegment = new DefaultImageSegment(
				imageId, labelId, timepoint,
				userClickCoordinate.getDoublePosition( 0 ),
				userClickCoordinate.getDoublePosition( 1 ),
				userClickCoordinate.getDoublePosition( 2 ),
				null );

		keyToSegment.put( imageSegmentId, imageSegment );
	}

	public void setBdv( BdvHandle bdv )
	{
		this.bdv = bdv;
	}

	public void setSourceAndMetadata( SourceAndMetadata sourceAndMetadata )
	{
		this.labelSourceAndMetadata = sourceAndMetadata;
	}
}
