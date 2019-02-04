package de.embl.cba.tables.modelview.combined;

import bdv.util.BdvHandle;
import bdv.viewer.Source;
import de.embl.cba.bdv.utils.BdvUtils;
import de.embl.cba.tables.modelview.images.Metadata;
import de.embl.cba.tables.modelview.images.SourceAndMetadata;
import de.embl.cba.tables.modelview.segments.DefaultImageSegment;
import de.embl.cba.tables.modelview.segments.ImageSegment;
import de.embl.cba.tables.modelview.segments.ImageSegmentId;
import net.imglib2.FinalInterval;
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
		final String imageId = ( String ) labelSourceAndMetadata.getMetadata().get().get( Metadata.NAME );

		final RealPoint userClickCoordinate = BdvUtils.getGlobalMouseCoordinates( bdv );
		final int timepoint = bdv.getBdvHandle().getViewerPanel().getState().getCurrentTimepoint();
		final double labelId = BdvUtils.getValueAtGlobalCoordinates(
				labelSourceAndMetadata.getSource(),
				userClickCoordinate,
				timepoint );

		final ImageSegmentId imageSegmentId = new ImageSegmentId( imageId, labelId, timepoint );

		final DefaultImageSegment imageSegment = new DefaultImageSegment(
				imageSegmentId,
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
