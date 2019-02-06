package de.embl.cba.tables.modelview.images;

import de.embl.cba.tables.TableUtils;
import de.embl.cba.tables.cellprofiler.FolderAndFileColumn;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


public class TableImageSourcesModelFactory
{
	public static final String IMAGE_SET_INDEX_COLUMN = "ImageNumber";
	public static final String FOLDER = "PathName_";
	public static final String FILE = "FileName_";
	public static final String OBJECTS = "Objects_";

	private final File tableFile;
	private final String imageRootPathInTable;
	private final String imageRootPathOnThisComputer;
	private final String delim;

	private ArrayList< String > columns;
	private FileImageSourcesModel imageSourcesModel;
	private final HashMap< String, FolderAndFileColumn > imageNameToPathColumns;
	private final JTable table;
	private final int numSpatialDimensions;

	public TableImageSourcesModelFactory(
			File tableFile,
			String imageRootPathInTable,
			String imageRootPathOnThisComputer,
			String delim, int numSpatialDimensions )
	{
		this.tableFile = tableFile;
		this.imageRootPathInTable = imageRootPathInTable;
		this.imageRootPathOnThisComputer = imageRootPathOnThisComputer;
		this.delim = delim;

		table = TableUtils.loadTable( this.tableFile, delim );
		this.numSpatialDimensions = numSpatialDimensions;

		columns = TableUtils.getColumnNames( table );

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

		for ( int row = 0; row < table.getModel().getRowCount(); row++ )
		{
			ArrayList< String > imageSetIds = getImageSetIds( row );

			for ( String imageName : imageNameToPathColumns.keySet() )
			{
				String imagePath = getImagePath( imageName, row );
				String imageId = imagePath;
				final Metadata.Flavour imageFlavour = getImageFlavour( imageName );

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

	private ArrayList< String > getImageSetIds( int row )
	{
		ArrayList< String > imageSetIds = new ArrayList<>(  );
		for ( String imageName : imageNameToPathColumns.keySet() )
		{
			String imageId = getImagePath( imageName, row );
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

	private String getImagePath( String imageName, int row )
	{
		final String folderColumn = imageNameToPathColumns.get( imageName ).folderColumn();
		final String fileColumn = imageNameToPathColumns.get( imageName ).fileColumn();

		final String folderName = ( String ) table.getValueAt(
				row,
				table.getColumnModel().getColumnIndex( folderColumn ) );

		final String fileName = ( String ) table.getValueAt(
				row,
				table.getColumnModel().getColumnIndex( fileColumn ) );

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

	private HashMap< String, FolderAndFileColumn > identifyImagePathColumns( )
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
