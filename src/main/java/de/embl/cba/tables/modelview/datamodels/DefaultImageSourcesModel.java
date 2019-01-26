package de.embl.cba.tables.modelview.datamodels;

import bdv.viewer.Source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DefaultImageSourcesModel implements ImageSourcesModel
{
	private Map< String, IntensityAndLabelImageSources > imageSourcesMap;

	private final boolean is2D;

	public DefaultImageSourcesModel( boolean is2D )
	{
		this.imageSourcesMap = new HashMap<>(  );
		this.is2D = is2D;
	}

	@Override
	public ArrayList< Source< ? > > getIntensityImageSources( String imageSetId )
	{
		return imageSourcesMap.get( imageSetId ).intensityImageSources;
	}

	@Override
	public Source< ? > getLabelImageSource( String imageSetId )
	{
		return imageSourcesMap.get( imageSetId ).labelImageSource;
	}

	@Override
	public boolean is2D()
	{
		return is2D;
	}

	@Override
	public ArrayList< String > getImageSetIds()
	{
		return new ArrayList( imageSourcesMap.keySet() );
	}

	public void addLabelImageSource( String imageId, Source< ? > labelImageSource )
	{
		addIfMissing( imageId );

		imageSourcesMap.get( imageId ).labelImageSource = labelImageSource;
	}

	public void addIntensityImageSource( String imageId, Source< ? > intensityImageSource )
	{
		addIfMissing( imageId );

		imageSourcesMap.get( imageId ).intensityImageSources.add( intensityImageSource );
	}

	public void addIfMissing( String imageId )
	{
		if ( ! imageSourcesMap.containsKey( imageId ) )
		{
			final IntensityAndLabelImageSources sources = new IntensityAndLabelImageSources();
			imageSourcesMap.put( imageId, sources );
		}
	}



	private class IntensityAndLabelImageSources
	{
		ArrayList< Source< ? > > intensityImageSources = new ArrayList<>(  );
		Source< ? > labelImageSource;
	}

}
