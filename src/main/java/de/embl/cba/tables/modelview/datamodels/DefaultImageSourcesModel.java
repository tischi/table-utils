package de.embl.cba.tables.modelview.datamodels;

import bdv.viewer.Source;
import net.imglib2.type.numeric.RealType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DefaultImageSourcesModel implements ImageSourcesModel
{
	private Map< String, ArrayList< Source< ? > > > imageSetToSources;
	private Map< Source< ? >, String > sourceToMetaData;

	private final boolean is2D;

	public DefaultImageSourcesModel( boolean is2D )
	{
		this.imageSetToSources = new HashMap<>(  );
		this.sourceToMetaData = new HashMap<>(  );
		this.is2D = is2D;
	}

	@Override
	public Map< String, ArrayList< Source< ? > > > get()
	{
		return imageSetToSources;
	}

	@Override
	public String getImageSourceMetaData( Source< ? > source )
	{
		return sourceToMetaData.get( source );
	}

	@Override
	public boolean is2D()
	{
		return is2D;
	}


	public void addLabelSource( String imageId, Source< ? extends RealType< ? > > labelSource )
	{
		addIfMissing( imageId );

		imageSetToSources.get( imageId ).add( labelSource );
		sourceToMetaData.put( labelSource, Metadata.FLAVOUR );
	}

	public void addIntensitySource( String imageId, Source< ? > intensitySource )
	{
		addIfMissing( imageId );

		imageSetToSources.get( imageId ).add( intensitySource );
		sourceToMetaData.put( intensitySource, Metadata.DIMENSIONS );

	}

	public void addIfMissing( String imageId )
	{
		if ( ! imageSetToSources.containsKey( imageId ) )
		{
			imageSetToSources.put( imageId, new ArrayList<>(  ) );
		}
	}

}
