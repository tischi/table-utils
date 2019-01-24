package de.embl.cba.tables.modelview.objects;

import de.embl.cba.tables.modelview.DataSetTimePointLabel;

public interface ImageSegment
{
	// Object getImageId(); maybe good to add for multi-image data sets
	double getLabel();
	int getTimePoint();
}
