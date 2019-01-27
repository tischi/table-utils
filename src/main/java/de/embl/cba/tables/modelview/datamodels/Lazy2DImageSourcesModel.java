package de.embl.cba.tables.modelview.datamodels;

import bdv.util.RandomAccessibleIntervalSource;
import bdv.viewer.Interpolation;
import bdv.viewer.Source;
import ij.IJ;
import ij.ImagePlus;
import mpicbg.spim.data.sequence.VoxelDimensions;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccessible;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;

import java.io.File;
import java.util.*;

public class Lazy2DImageSourcesModel implements ImageSourcesModel
{
	private Map< String, ArrayList< Source < ? > > > imageSetToSources;
	private Map< Source< ? >, String > sourceToMetaData;

	private final boolean is2D;

	/**
	 * Lazily constructs Sources from 2D image files upon request.
	 * There is no caching.
	 */
	public Lazy2DImageSourcesModel( )
	{
		this.imageSetToSources = new HashMap<>(  );
		this.sourceToMetaData = new HashMap<>(  );
		this.is2D = true;
	}

	class Lazy2DFileSource < T extends NumericType< T > > implements Source< T >
	{
		private final File file;
		private RandomAccessibleIntervalSource< T > source;

		public Lazy2DFileSource( File file )
		{
			this.file = file;
		}

		@Override
		public boolean isPresent( int t )
		{
			return false;
		}

		@Override
		public RandomAccessibleInterval< T > getSource( int t, int level )
		{
			return wrappedSource().getSource( t, level );
		}

		private RandomAccessibleIntervalSource< T > wrappedSource()
		{
			if ( source == null )
			{
				final ImagePlus imagePlus = IJ.openImage( file.toString() );
				source =  imagePlus2DAsSource3D( imagePlus );
			}

			return source;
		}

		@Override
		public RealRandomAccessible< T > getInterpolatedSource( int t, int level, Interpolation method )
		{
			return wrappedSource().getInterpolatedSource( t, level, method );
		}

		@Override
		public void getSourceTransform( int t, int level, AffineTransform3D transform )
		{
			wrappedSource().getSourceTransform( t, level, transform  );
		}

		@Override
		public T getType()
		{
			return wrappedSource().getType();
		}

		@Override
		public String getName()
		{
			return wrappedSource().getName();
		}

		@Override
		public VoxelDimensions getVoxelDimensions()
		{
			return wrappedSource().getVoxelDimensions();
		}

		@Override
		public int getNumMipmapLevels()
		{
			return wrappedSource().getNumMipmapLevels();
		}
	}


	@Override
	public Map< String, ArrayList< Source< ? > > > getImageSources()
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

	public void addLabelSource( String imageId, File labelSource )
	{
		addKeyIfMissing( imageId );

		final Lazy2DFileSource lazy2DFileSource = new Lazy2DFileSource( labelSource );

		imageSetToSources.get( imageId ).add( lazy2DFileSource );

		sourceToMetaData.put( lazy2DFileSource, ImageSourcesMetaData.LABEL_SOURCE );
	}

	public void addIntensityImageSource( String imageId, File labelSource )
	{
		addKeyIfMissing( imageId );

		final Lazy2DFileSource source = new Lazy2DFileSource( labelSource );

		imageSetToSources.get( imageId ).add( source );

		sourceToMetaData.put( source, ImageSourcesMetaData.INTENSITY_SOURCE );
	}

	public void addKeyIfMissing( String imageId )
	{
		if ( ! imageSetToSources.containsKey( imageId ) )
		{
			imageSetToSources.put( imageId, new ArrayList<>(  ) );
		}
	}

	public static < T extends NumericType< T > >
	RandomAccessibleIntervalSource< T > imagePlus2DAsSource3D( ImagePlus imagePlus )
	{
		RandomAccessibleInterval< RealType > wrap = ImageJFunctions.wrapReal( imagePlus );

		// needs to be at least 3D
		wrap = Views.addDimension( wrap, 0, 0);

		return new RandomAccessibleIntervalSource( wrap, Util.getTypeFromInterval( wrap ), imagePlus.getTitle() );
	}
}
