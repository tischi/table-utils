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

public class GeneratingImageSegmentsModel implements ImageSegmentsModel< DefaultImageSegment >
{
	private final Map< ImageSegmentId, DefaultImageSegment > keyToSegment;
	private BdvHandle bdv;
	private SourceAndMetadata labelSourceAndMetadata;

	public GeneratingImageSegmentsModel( )
	{
		keyToSegment = new HashMap<>(  );
	}

	@Override
	public DefaultImageSegment getImageSegment( ImageSegmentId imageSegmentId )
	{
		if ( ! keyToSegment.keySet().contains( imageSegmentId ) )
		{
			addSegment();
		}

		return keyToSegment.get( imageSegmentId );

	}

	public void addSegment()
	{
		final String imageId = ( String ) labelSourceAndMetadata.metadata().get().get( Metadata.NAME );

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

		keyToSegment.put( new ImageSegmentId( imageSegment ), imageSegment );
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
