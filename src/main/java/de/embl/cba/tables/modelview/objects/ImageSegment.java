package de.embl.cba.tables.modelview.objects;

import net.imglib2.FinalInterval;
import net.imglib2.Localizable;
import net.imglib2.RealLocalizable;

public interface ImageSegment extends RealLocalizable
{
	String imageSetName();

	double label();

	int timePoint();

	FinalInterval boundingBox();
}
