package de.embl.cba.tables.modelview.datamodels;

import bdv.viewer.Source;

import java.util.ArrayList;

public interface ImageSourcesModel
{
	ArrayList< Source< ? > > getIntensityImageSources( String imageSetId );

	Source< ? > getLabelImageSource( String imageSetId );

	boolean is2D();

	ArrayList< String > getImageSetIds();
}
