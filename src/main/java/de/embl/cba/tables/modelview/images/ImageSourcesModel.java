package de.embl.cba.tables.modelview.images;

import net.imglib2.type.numeric.RealType;

import java.util.Map;

// TODO: make it rather implement a map?
public interface ImageSourcesModel
{
	Map< String, SourceAndMetadata< ? > > sources();

	boolean is2D();
}
