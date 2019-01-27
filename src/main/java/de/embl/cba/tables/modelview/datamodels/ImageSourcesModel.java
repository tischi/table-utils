package de.embl.cba.tables.modelview.datamodels;

import bdv.viewer.Source;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import java.util.ArrayList;
import java.util.Map;

public interface ImageSourcesModel
{
	Map< String, ArrayList< Source< ? > > > getImageSources( );

	boolean is2D();
}
