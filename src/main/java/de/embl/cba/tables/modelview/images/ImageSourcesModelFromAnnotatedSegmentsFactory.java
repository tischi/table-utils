package de.embl.cba.tables.modelview.images;

import de.embl.cba.tables.cellprofiler.FolderAndFileColumn;
import de.embl.cba.tables.modelview.segments.AnnotatedImageSegment;
import de.embl.cba.tables.modelview.segments.TableRow;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class ImageSourcesModelFromAnnotatedSegmentsFactory< T extends AnnotatedImageSegment >
{
	public static final String IMAGE_SET_INDEX_COLUMN = "ImageNumber";
	public static final String FOLDER = "PathName_";
	public static final String FILE = "FileName_";
	public static final String OBJECTS = "Objects_";

	private final ArrayList< T > annotatedImageSegments;
	private final String imageRootPathInTable;
	private final String imageRootPathOnThisComputer;

	private Set< String > columns;
	private FileImageSourcesModel imageSourcesModel;
	private final HashMap< String, FolderAndFileColumn > imageNameToPathColumns;
	private final int numSpatialDimensions;

	public ImageSourcesModelFromAnnotatedSegmentsFactory(
			ArrayList< T > annotatedImageSegments,
			String imageRootPathInTable,
			String imageRootPathOnThisComputer,
			int numSpatialDimensions )
	{
		this.annotatedImageSegments = annotatedImageSegments;

		this.imageRootPathInTable = imageRootPathInTable;
		this.imageRootPathOnThisComputer = imageRootPathOnThisComputer;

		this.numSpatialDimensions = numSpatialDimensions;

		columns = annotatedImageSegments.get( 0 ).cells().keySet();

		imageNameToPathColumns = identifyImagePathColumns();

		imageSourcesModel = createImageSourcesModel();
	}

	public FileImageSourcesModel getImageSourcesModel()
	{
		return imageSourcesModel;
	}

	private FileImageSourcesModel createImageSourcesModel( )
	{
		final FileImageSourcesModel imageSourcesModel = new FileImageSourcesModel();

		for ( AnnotatedImageSegment annotatedImageSegment : annotatedImageSegments )
		{
			ArrayList< String > imageSetIds = getImageSetIds( annotatedImageSegment );

			for ( String imageName : imageNameToPathColumns.keySet() )
			{
				String imagePath = getImagePath( imageName, annotatedImageSegment );
				String imageId = annotatedImageSegment.imageId();
				final Metadata.Flavour imageFlavour = getImageFlavour( imageName );

				// TODO: to make it faster
				// one could check here whether this image
				// has been added already
				addImageToModel(
						imageSourcesModel,
						imageId,
						imagePath,
						imageFlavour,
						imageSetIds );
			}
		}

		return imageSourcesModel;
	}

	private ArrayList< String > getImageSetIds( TableRow tableRow )
	{
		ArrayList< String > imageSetIds = new ArrayList<>(  );
		for ( String imageName : imageNameToPathColumns.keySet() )
		{
			String imageId = getImagePath( imageName, tableRow );
			imageSetIds.add( imageId );
		}
		return imageSetIds;
	}

	private Metadata.Flavour getImageFlavour( String imageName )
	{
		Metadata.Flavour flavour;
		if ( imageName.contains( OBJECTS ) )
		{
			flavour = Metadata.Flavour.LabelSource;
		}
		else
		{
			flavour = Metadata.Flavour.IntensitySource;
		}

		return flavour;
	}

	private String getImagePath( String imageName, TableRow tableRow )
	{
		final String folderColumn = imageNameToPathColumns.get( imageName ).getFolderColumn();
		final String fileColumn = imageNameToPathColumns.get( imageName ).getFileColumn();
		final String folderName = ( String ) tableRow.cells().get( folderColumn );
		final String fileName = ( String ) tableRow.cells().get( fileColumn );

		return folderName + File.separator + fileName;
	}

	private void addImageToModel(
			FileImageSourcesModel imageSourcesModel,
			String imageId,
			String imagePath,
			Metadata.Flavour imageFlavour,
			ArrayList< String > imageSetIds )
	{
		imagePath = getMappedPath( imagePath );

		imageSourcesModel.addSource(
				imageId,
				new File( imagePath ),
				imageSetIds,
				imageFlavour,
				numSpatialDimensions );
	}

	private String getMappedPath( String imagePath )
	{
		if ( imageRootPathInTable != null )
		{
			imagePath = imagePath.replace( imageRootPathInTable, imageRootPathOnThisComputer );
		}
		return imagePath;
	}

	public HashMap< String, FolderAndFileColumn > identifyImagePathColumns( )
	{
		final HashMap< String, FolderAndFileColumn > images = new HashMap<>();
		for ( String column : columns )
		{
			if ( column.contains( FOLDER ) )
			{
				final String image = column.split( FOLDER )[ 1 ];
				String fileColumn = getMatchingFileColumn( image );
				images.put( image, new FolderAndFileColumn( column, fileColumn ) );
			}
		}
		return images;
	}

	private String getMatchingFileColumn( String image )
	{
		for ( String column : columns )
		{
			if ( column.contains( FILE ) && column.contains( image ) )
			{
				return column;
			}
		}

		return null;
	}
}
