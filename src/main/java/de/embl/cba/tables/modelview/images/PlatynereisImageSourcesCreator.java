package de.embl.cba.tables.modelview.images;

import de.embl.cba.tables.FileUtils;
import de.embl.cba.tables.TableUtils;
import de.embl.cba.tables.cellprofiler.FolderAndFileColumn;
import org.fife.rsta.ac.js.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;


public class PlatynereisImageSourcesCreator
{

	public static final String DEFAULT_EM_RAW_FILE_ID = "em-raw-full-res"; //"em-raw-100nm"; //"em-raw-10nm-10nm-25nm"; //"em-raw-100nm"; //
	public static final String DEFAULT_LABELS_FILE_ID = "em-segmented-cells-labels" ;
	public static final String LABELS_FILE_ID = "-labels" ;

	public static final String BDV_XML_SUFFIX = ".xml";
	public static final String IMARIS_SUFFIX = ".ims";
	public static final double PROSPR_SCALING_IN_MICROMETER = 0.5;
	public static final String EM_RAW_FILE_ID = "em-raw-"; //"em-raw-100nm"; //"em-raw-10nm-10nm-25nm"; //"em-raw-100nm"; //
	public static final String EM_SEGMENTED_FILE_ID = "em-segmented";
	public static final String EM_FILE_ID = "em-";
	public static final String SELECTION_UI = "Data sources";
	public static final String POSITION_UI = "Move to position";
	public static final Color DEFAULT_GENE_COLOR = new Color( 255, 0, 255, 255 );
	public static final Color DEFAULT_EM_RAW_COLOR = new Color( 255, 255, 255, 255 );
	public static final Color DEFAULT_EM_SEGMENTATION_COLOR = new Color( 255, 0, 0, 255 );
	public static final double ZOOM_REGION_SIZE = 50.0;
	public static final String NEW_PROSPR = "-new";
	public static final String AVG_PROSPR = "-avg";

	public static final String CELLULAR_MODELS = "cellular-models";
	public static final CharSequence MEDS = "-MEDs" ;
	public static final CharSequence SPMS = "-SPMs";
	public static final String OLD = "-OLD";

	public static final String IMAGE_SET_INDEX_COLUMN = "ImageNumber";
	public static final String FOLDER = "PathName_";
	public static final String FILE = "FileName_";

	public static final String OBJECTS = "Objects_";

	private ArrayList< String > columns;
	private CellProfilerImageSourcesModel imageSourcesModel;

	public PlatynereisImageSourcesCreator( File directory ) throws IOException
	{
		ArrayList< File > imageFiles = getImageFiles( directory, BDV_XML_SUFFIX );

		PlatynereisImageSourcesModel.getSourceName( imageFiles );
//		final HashMap< String, FolderAndFileColumn > images = getImageFileAndFolderColumns( );
//
//		imageSourcesModel = createImageSourcesModel(
//				table,
//				table.getColumnModel().getColumnIndex( IMAGE_SET_INDEX_COLUMN ),
//				images );
	}

	public ArrayList< File > getImageFiles( File inputDirectory, String filePattern )
	{
		Logger.log( "Fetching image files..." );
		final ArrayList< File > fileList = FileUtils.getFileList( inputDirectory, filePattern );
		Logger.log( "Number of image files: " +  fileList.size() );
		Collections.sort( fileList, new SortFilesIgnoreCase());
		return fileList;
	}


	public class SortFilesIgnoreCase implements Comparator<File>
	{
		public int compare( File o1, File o2 )
		{
			String s1 = o1.getName();
			String s2 = o2.getName();
			return s1.toLowerCase().compareTo(s2.toLowerCase());
		}
	}


	public CellProfilerImageSourcesModel getImageSourcesModel()
	{
		return imageSourcesModel;
	}

	private CellProfilerImageSourcesModel createImageSourcesModel(
			JTable table,
			int imageSetIdColumnIndex,
			HashMap< String, FolderAndFileColumn > images ) throws IOException
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
				imageSourcesModel.addSource( imageId, new File( imagePath ), imageSetIds );
			}
			else
			{
				imageSourcesModel.addIntensityImageSource( imageId, new File( imagePath ), imageSetIds );
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
