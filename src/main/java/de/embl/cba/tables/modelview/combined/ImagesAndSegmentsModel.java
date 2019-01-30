package de.embl.cba.tables.modelview.combined;

import de.embl.cba.tables.modelview.images.ImageSourcesModel;
import de.embl.cba.tables.modelview.segments.ImageSegment;

import java.util.ArrayList;

public interface ImagesAndSegmentsModel< T extends ImageSegment >
{
	T getSegment( String imageSetName, Double label, int timePoint );

	ImageSourcesModel getImageSourcesModel();

	ArrayList< T > getImageSegments();
}
