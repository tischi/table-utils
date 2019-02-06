package de.embl.cba.tables.modelview.images;

import bdv.util.RandomAccessibleIntervalSource4D;
import bdv.viewer.Interpolation;
import bdv.viewer.Source;
import de.embl.cba.bdv.utils.wrap.Wraps;
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

import static de.embl.cba.tables.modelview.images.Metadata.*;

public class FileImageSourcesModel implements ImageSourcesModel
{
	private final Map< String, SourceAndMetadata > nameToSourceAndMetadata;

	public FileImageSourcesModel( )
	{
		nameToSourceAndMetadata = new HashMap<>();
	}

	@Override
	public Map< String, SourceAndMetadata > sources()
	{
		return nameToSourceAndMetadata;
	}

	class FileSource< R extends RealType< R > & NativeType< R > >
			implements Source< R >
	{
		private final String name;
		private final File file;
		private RandomAccessibleIntervalSource4D source;

		public FileSource( String name, File file )
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
		public RandomAccessibleInterval< R > getSource( int t, int level )
		{
			return wrappedSource().getSource( t, level );
		}

		private RandomAccessibleIntervalSource4D< R > wrappedSource()
		{
			if ( source == null )
			{
				final ImagePlus imagePlus = IJ.openImage( file.toString() );
				imagePlus.setTitle( name );
				source = Wraps.imagePlusAsSource4DChannelList( imagePlus ).get( 0 );
			}

			return source;
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

	public void addSource(
			String imageId,
			String imageDisplayName,
			File file,
			ArrayList< String > imageSetIDs,
			Flavour flavor,
			int numSpatialDimensions )
	{
		if ( nameToSourceAndMetadata.containsKey( imageId ) ) return;

		final FileSource fileSource = new FileSource( imageDisplayName, file );

		final Metadata metadata = new Metadata();
		metadata.getMap().put( FLAVOUR, flavor );
		metadata.getMap().put( NUM_SPATIAL_DIMENSIONS, numSpatialDimensions );
		metadata.getMap().put( EXCLUSIVE_IMAGE_SET, imageSetIDs );
		metadata.getMap().put( DISPLAY_NAME, imageDisplayName );
		metadata.getMap().put( IMAGE_ID, imageId );


		nameToSourceAndMetadata.put( imageId, new SourceAndMetadata( fileSource, metadata ) );
	}

}
