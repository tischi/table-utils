package de.embl.cba.tables.cellprofiler;

import de.embl.cba.tables.TableUtils;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class CellProfilerTableParser
{

	public static final String DATASET_INDEX = "ImageNumber";
	public static final String FOLDER = "PathName_";
	public static final String FILE = "FileName_";

	public static final String OBJECTS = "Objects_";


	private ArrayList< String > columns;
	private HashMap< Object, CellProfilerDataset > datasets;

	public CellProfilerTableParser( JTable table )
	{
		columns = TableUtils.getColumnNames( table );

		final HashMap< String, FolderAndFileColumn > images = getImageFileAndFolderColumns( );

		datasets = getDatasets( table, table.getColumnModel().getColumnIndex( DATASET_INDEX ), images );
	}

	public HashMap getDatasets( JTable table, int dataSetColumnIndex, HashMap< String, FolderAndFileColumn > images )
	{
		HashMap datasets = new HashMap< Integer, CellProfilerDataset >(  );

		for ( int row = 0; row < table.getModel().getRowCount(); row++ )
		{
			final Object datasetIndex = table.getValueAt( row, dataSetColumnIndex );

			if ( ! datasets.keySet().contains( datasetIndex ) )
			{
				final CellProfilerDataset dataset = new CellProfilerDataset( datasetIndex );

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

					final String pathName = folderName + File.separator + fileName;

					String type = CellProfilerDataset.INTENSITY_IMAGE;

					if ( image.contains( OBJECTS ) )
					{
						type = CellProfilerDataset.OBJECT_LABEL_MASK;
					}

					dataset.addImagePath( type, pathName );
				}

				datasets.put( datasetIndex, dataset );
			}

		}

		return datasets;
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

	public HashMap< Object, CellProfilerDataset > getDatasets()
	{
		return datasets;
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
