package de.embl.cba.tables.imagesegment;

public interface ImageSegmentsModel < T extends ImageSegment >
{
	T getImageSegment( LabelFrameAndImage labelFrameAndImage );

	String getName();
}
