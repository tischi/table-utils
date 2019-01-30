package de.embl.cba.tables.modelview.datamodels;

import bdv.viewer.Source;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import java.util.ArrayList;
import java.util.Map;


// TODO: make it implement map? or just use a map?
// the point of putting a get() into it was to be able to make it lazy...
public interface ImageSourcesModel
{
	/**
	 *
	 */
	Map< String, SourceAndMetadata > get();

}
