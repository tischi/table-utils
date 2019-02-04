package de.embl.cba.tables.modelview.images;

import de.embl.cba.tables.TableUtils;
import de.embl.cba.tables.cellprofiler.FolderAndFileColumn;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class CellProfilerImageSourcesModelCreator
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
	private CellProfilerImageSourcesModel imageSourcesModel;

	public CellProfilerImageSourcesModelCreator(
			File tableFile,
			String imageRootPathInTable,
			String imageRootPathOnThisComputer,
			String delim )
	{
		this.tableFile = tableFile;
		this.imageRootPathInTable = imageRootPathInTable;
		this.imageRootPathOnThisComputer = imageRootPathOnThisComputer;
		this.delim = delim;

		final JTable table = TableUtils.loadTable( this.tableFile, delim );

		columns = TableUtils.getColumnNames( table );

		final HashMap< String, FolderAndFileColumn > images = getImageFileAndFolderColumns( );

		imageSourcesModel = createImageSourcesModel(
				table,
				table.getColumnModel().getColumnIndex( IMAGE_SET_INDEX_COLUMN ),
				images );
	}

	public CellProfilerImageSourcesModel getModel()
	{
		return imageSourcesModel;
	}

	private CellProfilerImageSourcesModel createImageSourcesModel(
			JTable table,
			int imageSetIdColumnIndex,
			HashMap< String, FolderAndFileColumn > images )
	{

		final HashSet< String > imageFilePaths = new HashSet<>();

		final CellProfilerImageSourcesModel imageSourcesModel = new CellProfilerImageSourcesModel();

		for ( int row = 0; row < table.getModel().getRowCount(); row++ )
		{
			final String imageSetId = table.getValueAt( row, imageSetIdColumnIndex ).toString();

			//if ( ! datasets.keySet().contains( datasetIndex ) )
			//{
			//	final CellProfilerDataset dataset = new CellProfilerDataset( datasetIndex );

			ArrayList< String > imageSetIds = new ArrayList<>(  );

			for ( String imageName : images.keySet() )
			{
				final String fileColumn = images.get( imageName ).getFileColumn();

				final String fileName = ( String ) table.getValueAt(
						row,
						table.getColumnModel().getColumnIndex( fileColumn ) );

				imageSetIds.add( imageSetId + fileName );

			}

			for ( String imageName : images.keySet() )
			{
				final String folderColumn = images.get( imageName ).getFolderColumn();
				final String fileColumn = images.get( imageName ).getFileColumn();

				final String folderName = ( String ) table.getValueAt(
						row,
						table.getColumnModel().getColumnIndex( folderColumn ) );

				final String fileName = ( String ) table.getValueAt(
						row,
						table.getColumnModel().getColumnIndex( fileColumn ) );

				String imagePath = folderName + File.separator + fileName;

				addImageToModel(
						imageFilePaths,
						imageSourcesModel,
						imageSetId + fileName,
						imagePath,
						imageSetIds );

			}
		}

		return imageSourcesModel;
	}

	public static String getImageId( String imageSetId, String imageName )
	{
		return imageSetId + imageName;
	}

	private void addImageToModel(
			HashSet< String > imageFilePaths,
			CellProfilerImageSourcesModel imageSourcesModel,
			String imageId,
			String imagePath,
			ArrayList< String > imageSetIds )
	{

		if ( imageRootPathInTable != null )
		{
			imagePath = imagePath.replace( imageRootPathInTable, imageRootPathOnThisComputer );
		}

		if ( ! imageFilePaths.contains( imagePath  ) )
		{
			if ( imageId.contains( OBJECTS ) )
			{
				imageSourcesModel.addSource( imageId, new File( imagePath ), imageSetIds, Metadata.Flavour.LabelSource );
			}
			else
			{
				imageSourcesModel.addSource( imageId, new File( imagePath ), imageSetIds, Metadata.Flavour.IntensitySource );
			}
		}

		imageFilePaths.add( imagePath );
	}

	public HashMap< String, FolderAndFileColumn > getImageFileAndFolderColumns( )
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
