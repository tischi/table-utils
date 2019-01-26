package de.embl.cba.tables.modelview.datamodels;

import bdv.viewer.Source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ImageSourcesModel
{
	private Map< String, IntensityAndLabelImageSources > imageSourcesMap;

	private final boolean is2D;

	public ImageSourcesModel(
			boolean is2D )
	{
		this.imageSourcesMap = new HashMap<>(  );
		this.is2D = is2D;
	}

	public ArrayList< Source< ? > > getIntensityImageSources( String imageId )
	{
		return imageSourcesMap.get( imageId ).intensityImageSources;
	}

	public ArrayList< Source< ? > > getLabelImageSources( String imageId )
	{
		return imageSourcesMap.get( imageId ).labelImageSources;
	}


	public boolean is2D()
	{
		return is2D;
	}

	public void addLabelImageSource( String imageId, Source< ? > labelImageSource )
	{
		addWhenMissing( imageId );

		imageSourcesMap.get( imageId ).labelImageSources.add( labelImageSource );
	}

	public void addIntensityImageSource( String imageId, Source< ? > intensityImageSource )
	{
		addWhenMissing( imageId );

		imageSourcesMap.get( imageId ).intensityImageSources.add( intensityImageSource );
	}

	public void addWhenMissing( String imageId )
	{
		if ( ! imageSourcesMap.containsKey( imageId ) )
		{
			final IntensityAndLabelImageSources sources = new IntensityAndLabelImageSources();
			imageSourcesMap.put( imageId, sources );
		}
	}

	public ArrayList< String > getImageIds()
	{
		return new ArrayList( imageSourcesMap.keySet() );
	}

	private class IntensityAndLabelImageSources
	{
		ArrayList< Source< ? > > intensityImageSources = new ArrayList<>(  );
		ArrayList< Source< ? > > labelImageSources = new ArrayList<>(  );
	}

}
