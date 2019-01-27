package de.embl.cba.tables.modelview.datamodels;

import bdv.viewer.Source;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import java.util.ArrayList;

public interface ImageSourcesModel
{
	ArrayList< Source< ? > > getIntensityImageSources( String imageSetId );

	Source< ? extends RealType< ? > > getLabelImageSource( String imageSetId );

	boolean is2D();

	ArrayList< String > getImageSetIds();
}
