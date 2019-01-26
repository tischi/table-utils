package de.embl.cba.tables.modelview.objects;

import net.imglib2.FinalInterval;

public interface ImageSegment2
{
	String imageId();

	double label();

	int timePoint();

	double[] position();

	FinalInterval boundingBox();
}
