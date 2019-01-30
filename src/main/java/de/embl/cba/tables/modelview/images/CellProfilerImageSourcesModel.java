package de.embl.cba.tables.modelview.images;

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

import static de.embl.cba.tables.modelview.images.Metadata.*;

public class CellProfilerImageSourcesModel implements ImageSourcesModel
{

	private final Map< String, SourceAndMetadata > nameToSourceAndMetadata;


	public CellProfilerImageSourcesModel( )
	{
		nameToSourceAndMetadata = new HashMap<>();
	}

	@Override
	public Map< String, SourceAndMetadata > get()
	{
		return nameToSourceAndMetadata;
	}

	class Lazy2DFileSource < T extends NumericType< T > > implements Source< T >
	{
		private final String name;
		private final File file;
		private RandomAccessibleIntervalSource< T > source;

		public Lazy2DFileSource( String name, File file )
		{
			this.name = name;
			this.file = file;
		}

		@Override
		public boolean isPresent( int t )
		{
			return true;
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
				source =  imagePlus2DAsSource3D( name, imagePlus );
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

	public void addSource( String imageId, File file, ArrayList< String > imageSetIDs, String flavor )
	{
		final Lazy2DFileSource lazy2DFileSource = new Lazy2DFileSource( imageId, file );

		final Metadata metadata = new Metadata();
		metadata.get().put( FLAVOUR, flavor );
		metadata.get().put( DIMENSIONS, 2 );
		metadata.get().put( EXCLUSIVE_IMAGE_SET, imageSetIDs );
		metadata.get().put( NAME, imageId );

		nameToSourceAndMetadata.put( imageId, new SourceAndMetadata( lazy2DFileSource, metadata ) );
	}

//	public void addIntensityImageSource( String imageId, File labelSource, ArrayList< String > imageSetIDs )
//	{
//		final Lazy2DFileSource lazy2DFileSource = new Lazy2DFileSource( imageId, labelSource );
//
//		final Metadata metadata = new Metadata();
//		metadata.get().put( FLAVOUR, INTENSITY_SOURCE_FLAVOUR );
//		metadata.get().put( DIMENSIONS, 2 );
//		metadata.get().put( EXCLUSIVE_IMAGE_SET, imageSetIDs );
//		metadata.get().put( NAME, imageId );
//
//		nameToSourceAndMetadata.put( imageId, new SourceAndMetadata( lazy2DFileSource, metadata ) );
//	}


	public static < T extends NumericType< T > >
	RandomAccessibleIntervalSource< T > imagePlus2DAsSource3D( String name, ImagePlus imagePlus )
	{
		RandomAccessibleInterval< RealType > wrap = ImageJFunctions.wrapReal( imagePlus );

		// needs to be at least 3D
		wrap = Views.addDimension( wrap, 0, 0);

		return new RandomAccessibleIntervalSource( wrap, Util.getTypeFromInterval( wrap ), name );
	}
}
