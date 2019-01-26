package de.embl.cba.tables.modelview.datamodels;

import de.embl.cba.tables.modelview.objects.AnnotatedImageSegment;
import de.embl.cba.tables.modelview.objects.ImageSegment;

import java.util.ArrayList;

public interface ImageSegmentsModel < T extends ImageSegment >
{
	T getSegment( Double label, int timePoint );

	LabelImageSourceModel getLabelImageSourceModel();

	ArrayList< T > getImageSegments();
}
