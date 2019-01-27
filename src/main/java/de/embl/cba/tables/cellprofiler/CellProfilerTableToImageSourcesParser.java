package de.embl.cba.tables.cellprofiler;

import de.embl.cba.tables.TableUtils;
import de.embl.cba.tables.modelview.datamodels.Lazy2DImageSourcesModel;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;


public class CellProfilerTableToImageSourcesParser
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
	private Lazy2DImageSourcesModel imageSourcesModel;

	public CellProfilerTableToImageSourcesParser(
			File tableFile,
			String imageRootPathInTable,
			String imageRootPathOnThisComputer,
			String delim ) throws IOException
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

	public Lazy2DImageSourcesModel getImageSourcesModel()
	{
		return imageSourcesModel;
	}

	private Lazy2DImageSourcesModel createImageSourcesModel(
			JTable table,
			int imageSetIdColumnIndex,
			HashMap< String, FolderAndFileColumn > images ) throws IOException
	{
		final Lazy2DImageSourcesModel imageSourcesModel = new Lazy2DImageSourcesModel();

		for ( int row = 0; row < table.getModel().getRowCount(); row++ )
		{
			final String imageSetId = table.getValueAt( row, imageSetIdColumnIndex ).toString();

			//if ( ! datasets.keySet().contains( datasetIndex ) )
			//{
			//	final CellProfilerDataset dataset = new CellProfilerDataset( datasetIndex );

			for ( String image : images.keySet() )
			{
				final String folderColumn = images.get( image ).getFolderColumn();
				final String fileColumn = images.get( image ).getFileColumn();

				final String folderName = ( String ) table.getValueAt(
						row,
						table.getColumnModel().getColumnIndex( folderColumn ) );

				final String fileName = ( String ) table.getValueAt(
						row,
						table.getColumnModel().getColumnIndex( fileColumn ) );

				String imagePath = folderName + File.separator + fileName;

				// path mapping if needed
				if ( imageRootPathInTable != null )
				{
					imagePath = imagePath.replace( imageRootPathInTable, imageRootPathOnThisComputer );
				}

				if ( image.contains( OBJECTS ) )
				{
					imageSourcesModel.addLabelImageSource( imageSetId, new File( imagePath ) );
				}
				else
				{
					imageSourcesModel.addIntensityImageSource( imageSetId, new File( imagePath ) );
				}
			}
		}

		return imageSourcesModel;
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
