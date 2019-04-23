package de.embl.cba.tables.image;

import bdv.util.RandomAccessibleIntervalSource4D;
import bdv.viewer.Interpolation;
import bdv.viewer.Source;
import de.embl.cba.bdv.utils.sources.LazySpimSource;
import de.embl.cba.bdv.utils.wrap.Wraps;
import de.embl.cba.tables.Logger;
import ij.IJ;
import ij.ImagePlus;
import mpicbg.spim.data.sequence.VoxelDimensions;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccessible;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import java.io.File;
import java.util.*;

import static de.embl.cba.tables.image.SourceMetadata.*;

public class FileImageSourcesModel implements ImageSourcesModel
{
	private final Map< String, SourceAndMetadata< ? > > nameToSourceAndMetadata;
	private final boolean is2D;

	public FileImageSourcesModel( boolean is2D )
	{
		this.is2D = is2D;
		nameToSourceAndMetadata = new HashMap<>();
	}

	@Override
	public Map< String, SourceAndMetadata< ? > > sources()
	{
		return nameToSourceAndMetadata;
	}

	@Override
	public boolean is2D()
	{
		return is2D;
	}

	public void addSourceAndMetadata(
			String imageId,
			String imageDisplayName,
			File file,
			List< String > imageSetIDs,
			Flavour flavor )
	{
		if ( nameToSourceAndMetadata.containsKey( imageId ) ) return;

		final SourceMetadata metadata = new SourceMetadata( imageId );
		metadata.flavour = flavor;

		// TODO: implement proper logic, issue is that sometimes there is a table associated, but no file
		if ( metadata.flavour.equals( Flavour.LabelSource ) )
			metadata.segmentsTable = new File("");

		metadata.imageSetIDs = imageSetIDs;
		metadata.displayName = imageDisplayName;

		if ( file.toString().endsWith( ".xml" ) )
		{
			final LazySpimSource lazySpimSource = new LazySpimSource( imageId, file );
			nameToSourceAndMetadata.put(
					imageId,
					new SourceAndMetadata( lazySpimSource, metadata ) );
		}
		else
		{
			final DefaultImageFileSource defaultImageFileSource =
					new DefaultImageFileSource( metadata, file );
			nameToSourceAndMetadata.put(
					imageId,
					new SourceAndMetadata( defaultImageFileSource, metadata ) );
		}

	}


	// TODO: put somewhere else...maybe bdv-utils?
	class DefaultImageFileSource< R extends RealType< R > & NativeType< R > > implements Source< R >
	{
		private final SourceMetadata metadata;
		private final File file;
		private RandomAccessibleIntervalSource4D source;
		private ImagePlus imagePlus;

		public DefaultImageFileSource( SourceMetadata metadata, File file )
		{
			this.metadata = metadata;
			this.file = file;
		}

		@Override
		public boolean isPresent( int t )
		{
			return wrappedSource().isPresent( t );
		}

		@Override
		public RandomAccessibleInterval< R > getSource( int t, int level )
		{
			return wrappedSource().getSource( t, level );
		}

		private RandomAccessibleIntervalSource4D< R > wrappedSource()
		{
			if ( source == null )
			{
				loadAndCreateSource();
			}

			return source;
		}

		private void loadAndCreateSource()
		{
			openImagePlus();

			metadata.numSpatialDimensions = imagePlus.getNSlices() > 1 ? 3 : 2;

			if( metadata.flavour == Flavour.LabelSource || imagePlus.getBitDepth() == 8 )
			{
				metadata.displayRangeMin = 0.0;
				metadata.displayRangeMax = 500.0;
			}
			else if( imagePlus.getBitDepth() == 16 )
			{
				metadata.displayRangeMin = 0.0;
				metadata.displayRangeMax = 65535.0;
			}

			source = Wraps.imagePlusAsSource4DChannelList( imagePlus ).get( 0 );
		}

		private void openImagePlus()
		{
			imagePlus = IJ.openImage( file.toString() );
			if ( imagePlus == null )
			{
				Logger.error("Could not open image: " + file.toString());
			}
			imagePlus.setTitle( metadata.displayName );
		}

		@Override
		public RealRandomAccessible< R > getInterpolatedSource( int t, int level, Interpolation method )
		{
			return wrappedSource().getInterpolatedSource( t, level, method );
		}

		@Override
		public void getSourceTransform( int t, int level, AffineTransform3D transform )
		{
			wrappedSource().getSourceTransform( t, level, transform  );
		}

		@Override
		public R getType()
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
}
