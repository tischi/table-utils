package de.embl.cba.tables.modelview.segments;

import net.imglib2.FinalInterval;
import net.imglib2.RealLocalizable;

public interface ImageSegment extends RealLocalizable
{
	String imageId();

	double labelId();

	int timePoint();

	FinalInterval boundingBox();




}
