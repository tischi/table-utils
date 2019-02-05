package de.embl.cba.tables.commands;

import de.embl.cba.bdv.utils.lut.GlasbeyARGBLut;
import de.embl.cba.tables.TableUtils;
import de.embl.cba.tables.cellprofiler.FolderAndFileColumn;
import de.embl.cba.tables.modelview.coloring.DynamicCategoryColoringModel;
import de.embl.cba.tables.modelview.coloring.SelectionColoringModel;
import de.embl.cba.tables.modelview.combined.DefaultImageSegmentsModel;
import de.embl.cba.tables.modelview.combined.DefaultTableRowsModel;
import de.embl.cba.tables.modelview.images.FileImageSourcesModel;
import de.embl.cba.tables.modelview.images.ImageSourcesModelFromAnnotatedSegmentsFactory;
import de.embl.cba.tables.modelview.images.TableImageSourcesModelCreator;
import de.embl.cba.tables.modelview.segments.*;
import de.embl.cba.tables.modelview.selection.DefaultSelectionModel;
import de.embl.cba.tables.modelview.selection.SelectionModel;
import de.embl.cba.tables.modelview.views.bdv.ImageSegmentsBdvView;
import de.embl.cba.tables.modelview.views.table.TableRowsTableView;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;


@Plugin(type = Command.class, menuPath = "Plugins>Segmentation>Explore>CellProfiler Objects Table" )
public class ExploreCellProfilerObjectsTableCommand< R extends RealType< R > & NativeType< R > >
		implements Command
{
	public static final String CELLPROFILER_FOLDER_COLUMN_PREFIX = "PathName_";
	public static final String CELLPROFILER_FILE_COLUMN_PREFIX = "FileName_";
	public static final String OBJECTS = "Objects_";

	@Parameter ( label = "CellProfiler Table" )
	public File inputTableFile;

	@Parameter ( label = "Label Image File Name Column" )
	public String labelImageFileNameColumn = "FileName_Objects_Nuclei_Labels";

	@Parameter ( label = "Label Image Folder Name Column" )
	public String labelImagePathNameColumn = "PathName_Objects_Nuclei_Labels";

	@Parameter ( label = "Apply Path Mapping" )
	public boolean isPathMapping = false;

	@Parameter ( label = "Image Path Mapping (Table)" )
	public String imageRootPathInTable = "/Volumes/cba/exchange/Daja-Christian/20190116_for_classification_interphase_versus_mitotic";

	@Parameter ( label = "Image Path Mapping (This Computer)" )
	public String imageRootPathOnThisComputer = "/Users/tischer/Documents/daja-schichler-nucleoli-segmentation--data/2019-01-31";
	private HashMap< String, FolderAndFileColumn > imageNameToFolderAndFileColumns;
	private LinkedHashMap< String, ArrayList< Object > > columns;

	@Override
	public void run()
	{

		final ArrayList< AnnotatedImageSegment > annotatedImageSegments
				= createAnnotatedImageSegments( inputTableFile );

		final ImageSourcesModelFromAnnotatedSegmentsFactory< AnnotatedImageSegment > factory
				= new ImageSourcesModelFromAnnotatedSegmentsFactory(
					annotatedImageSegments,
					2 );

		final FileImageSourcesModel imageSourcesModel = factory.getImageSourcesModel();

		final ArrayList< String > categoricalColumns = new ArrayList<>();
		categoricalColumns.add( "Label" );

		final SelectionModel< AnnotatedImageSegment > selectionModel
				= new DefaultSelectionModel<>();

		final DynamicCategoryColoringModel< AnnotatedImageSegment > coloringModel
				= new DynamicCategoryColoringModel<>( new GlasbeyARGBLut(), 50 );

		final SelectionColoringModel< AnnotatedImageSegment > selectionColoringModel
				= new SelectionColoringModel<>(
					coloringModel,
					selectionModel );

		final DefaultImageSegmentsModel< AnnotatedImageSegment > imageSegmentsModel
				= new DefaultImageSegmentsModel<>( annotatedImageSegments );

		final ImageSegmentsBdvView imageSegmentsBdvView =
				new ImageSegmentsBdvView(
						imageSourcesModel,
						imageSegmentsModel,
						selectionModel,
						selectionColoringModel );

		final DefaultTableRowsModel< AnnotatedImageSegment > tableRowsModel
				= new DefaultTableRowsModel<>( annotatedImageSegments );

		final TableRowsTableView tableView = new TableRowsTableView(
				tableRowsModel,
				selectionModel,
				selectionColoringModel,
				categoricalColumns );

	}

	public FileImageSourcesModel createCellProfilerImageSourcesModel()
	{
		if ( !isPathMapping )
		{
			imageRootPathInTable = "";
			imageRootPathOnThisComputer = "";
		}

		final TableImageSourcesModelCreator modelCreator =
				new TableImageSourcesModelCreator(
					inputTableFile,
					imageRootPathInTable,
					imageRootPathOnThisComputer,
					"\t",
						2 );

		return modelCreator.getImageSourcesModel();
	}

	private ArrayList< AnnotatedImageSegment > createAnnotatedImageSegments( File tableFile )
	{
		columns = TableUtils.columnsFromTableFile( tableFile, null );

		replaceFoldersAndFilesByPathsColumn();

		int a = 1;


//			final HashMap< ImageSegmentCoordinate, String > coordinateToColumnNameAndIndexMap = new HashMap<>();
//
//		coordinateToColumnNameAndIndexMap.put(
//				ImageSegmentCoordinate.ImageId,
//				labelImageFileNameColumn + SegmentUtils.FOLDER_AND_FILE_COLUMNS + labelImagePathNameColumn  );
//
//		coordinateToColumnNameAndIndexMap.put( ImageSegmentCoordinate.Label, "Number_Object_Number");
//		coordinateToColumnNameAndIndexMap.put( ImageSegmentCoordinate.X, "Location_Center_X" );
//		coordinateToColumnNameAndIndexMap.put( ImageSegmentCoordinate.Y, "Location_Center_Y" );
//
//
//
//		final ArrayList< AnnotatedImageSegment > annotatedImageSegments
//				= TableUtils.segmentsFromTableFile(
//						tableFile,
//						null,
//						coordinateToColumnNameAndIndexMap,
//						new DefaultImageSegmentBuilder() );
//
//		return annotatedImageSegments;

		return null;
	}

	private void replaceFoldersAndFilesByPathsColumn()
	{
		final int numRows = columns.values().iterator().next().size();
		imageNameToFolderAndFileColumns = fetchFolderAndFileColumns( columns.keySet() );

		final String tableFile = inputTableFile.toString();

		for ( String imageName : imageNameToFolderAndFileColumns.keySet() )
		{
			final String fileColumnName = imageNameToFolderAndFileColumns.get( imageName ).fileColumn();
			final String folderColumnName = imageNameToFolderAndFileColumns.get( imageName ).folderColumn();
			final ArrayList< Object > fileColumn = columns.get( fileColumnName );
			final ArrayList< Object > folderColumn = columns.get( folderColumnName );

			final ArrayList< Object > pathColumn = new ArrayList<>();

			for ( int row = 0; row < numRows; row++ )
			{
				String imagePath = folderColumn.get( row ) + File.separator + fileColumn.get( row );

				if ( isPathMapping )
				{
					imagePath = getMappedPath( imagePath );
				}

				imagePath = TableUtils.getRelativeImagePath( tableFile, imagePath ).toString();

				pathColumn.add( imagePath );
			}

			columns.remove( fileColumnName );
			columns.remove( folderColumnName );

			final String pathColumnName = getPathColumnName( imageName );
			columns.put( pathColumnName, pathColumn );

		}
	}

	public String getPathColumnName( String imageName )
	{
		String pathColumn = "Path_" + imageName;

		return pathColumn;
	}

//	private String getImagePath( String imageName, LinkedHashMap< String, ArrayList< Object > > columns )
//	{
//		final String folderColumn = imageNameToFolderAndFileColumns.get( imageName ).folderColumn();
//		final String fileColumn = imageNameToFolderAndFileColumns.get( imageName ).fileColumn();
//
//		final String folderName = ( String ) table.getValueAt(
//				row,
//				table.getColumnModel().getColumnIndex( folderColumn ) );
//
//		final String fileName = ( String ) table.getValueAt(
//				row,
//				table.getColumnModel().getColumnIndex( fileColumn ) );
//
//		return folderName + File.separator + fileName;
//	}

	private String getMappedPath( String imagePath )
	{
		imagePath = imagePath.replace( imageRootPathInTable, imageRootPathOnThisComputer );
		return imagePath;
	}

	public HashMap< String, FolderAndFileColumn > fetchFolderAndFileColumns( Set< String > columns )
	{
		final HashMap< String, FolderAndFileColumn > imageNameToFolderAndFileColumns = new HashMap<>();

		for ( String column : columns )
		{
			if ( column.contains( CELLPROFILER_FOLDER_COLUMN_PREFIX ) )
			{
				final String image = column.split( CELLPROFILER_FOLDER_COLUMN_PREFIX )[ 1 ];
				String fileColumn = getMatchingFileColumn( image, columns );
				imageNameToFolderAndFileColumns.put( image, new FolderAndFileColumn( column, fileColumn ) );
			}
		}
		return imageNameToFolderAndFileColumns;
	}

	private String getMatchingFileColumn( String image, Set< String > columns )
	{
		for ( String column : columns )
		{
			if ( column.contains( CELLPROFILER_FILE_COLUMN_PREFIX ) && column.contains( image ) )
			{
				return column;
			}
		}

		return null;
	}
}
