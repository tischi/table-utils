package de.embl.cba.tables.modelview.objects;

import net.imglib2.FinalInterval;

public interface ImageSegment
{
	String imageSetId();

	double label();

	int timePoint();

	double[] position();

	FinalInterval boundingBox();
}
