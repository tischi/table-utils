package de.embl.cba.tables.modelview.datamodels;

import de.embl.cba.tables.modelview.objects.ImageSegment;

import java.util.ArrayList;

public interface ImagesAndSegmentsModel< T extends ImageSegment >
{
	T getSegment( Double label, int timePoint );

	ImageSourcesModel getImageSourcesModel();

	ArrayList< T > getImageSegments();
}
