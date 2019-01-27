package de.embl.cba.tables.modelview.datamodels;

import bdv.viewer.Source;
import net.imglib2.type.numeric.RealType;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DefaultImageSourcesModel implements ImageSourcesModel
{
	private Map< String, ArrayList< Source< ? > > > imageSourcesMap;

	private final boolean is2D;

	public DefaultImageSourcesModel( boolean is2D )
	{
		this.imageSourcesMap = new HashMap<>(  );
		this.is2D = is2D;
	}

	@Override
	public Map< String, ArrayList< Source< ? > > > getImageSources()
	{
		return null;
	}

	@Override
	public boolean is2D()
	{
		return is2D;
	}


	public void addLabelSource( String imageId, Source< ? extends RealType< ? > > labelSource )
	{
		addIfMissing( imageId );

		imageSourcesMap.get( imageId ).add( labelSource );
	}

	public void addIntensitySource( String imageId, Source< ? > intensitySource )
	{
		addIfMissing( imageId );

		imageSourcesMap.get( imageId ).add( intensitySource );
	}

	public void addIfMissing( String imageId )
	{
		if ( ! imageSourcesMap.containsKey( imageId ) )
		{
			imageSourcesMap.put( imageId, new ArrayList<>(  ) );
		}
	}

}
