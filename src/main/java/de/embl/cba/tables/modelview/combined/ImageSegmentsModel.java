package de.embl.cba.tables.modelview.combined;

import de.embl.cba.tables.modelview.segments.ImageSegment;
import de.embl.cba.tables.modelview.segments.ImageSegmentId;

public interface ImageSegmentsModel < T extends ImageSegment >
{
	T getImageSegment( ImageSegmentId imageSegmentId );
}
