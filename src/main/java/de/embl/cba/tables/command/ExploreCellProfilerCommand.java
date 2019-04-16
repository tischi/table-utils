package de.embl.cba.tables.command;

import de.embl.cba.tables.TableColumns;
import de.embl.cba.tables.Tables;
import de.embl.cba.tables.cellprofiler.FolderAndFileColumn;
import de.embl.cba.tables.images.FileImageSourcesModel;
import de.embl.cba.tables.images.FileImageSourcesModelFactory;
import de.embl.cba.tables.imagesegment.*;
import de.embl.cba.tables.tablerow.TableRowImageSegment;
import de.embl.cba.tables.views.combined.SegmentsTableAndBdvViews;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.io.File;
import java.util.*;


@Plugin(type = Command.class,
		menuPath = "Plugins>Segmentation>Explore>Explore CellProfiler Objects Table" )
public class ExploreCellProfilerCommand< R extends RealType< R > & NativeType< R > >
		implements Command
{
	public static final String CELLPROFILER_FOLDER_COLUMN_PREFIX = "PathName_";
	public static final String CELLPROFILER_FILE_COLUMN_PREFIX = "FileName_";
	public static final String OBJECTS = "Objects_";
	public static final String COLUMN_NAME_OBJECT_LABEL = "Number_Object_Number";
	public static final String COLUMN_NAME_OBJECT_LOCATION_CENTER_X = "Location_Center_X";
	public static final String COLUMN_NAME_OBJECT_LOCATION_CENTER_Y = "Location_Center_Y";

	@Parameter ( label = "CellProfiler Table" )
	public File inputTableFile;

	@Parameter ( label = "Apply Path Mapping" )
	public boolean isPathMapping = false;

	@Parameter ( label = "Image Path Mapping (Table)" )
	public String imageRootPathInTable = "/Volumes/cba/exchange/Daja-Christian/20190116_for_classification_interphase_versus_mitotic";

	@Parameter ( label = "Image Path Mapping (This Computer)" )

	public String imageRootPathOnThisComputer = "/Users/tischer/Documents/daja-schichler-nucleoli-segmentation--data/2019-01-31";
	private HashMap< String, FolderAndFileColumn > imageNameToFolderAndFileColumns;
	private LinkedHashMap< String, List< ? > > columns;

	@Override
	public void run()
	{
		final List< TableRowImageSegment > tableRowImageSegments
				= createAnnotatedImageSegments( inputTableFile );

		final String tablePath = inputTableFile.toString();

		final FileImageSourcesModel imageSourcesModel =
				new FileImageSourcesModelFactory(
						tableRowImageSegments,
						tablePath,
						true ).getImageSourcesModel();

		new SegmentsTableAndBdvViews( tableRowImageSegments, imageSourcesModel, inputTableFile.getName() );
	}

	private List< TableRowImageSegment > createAnnotatedImageSegments( File tableFile )
	{
		columns = TableColumns.asTypedColumns(
				TableColumns.stringColumnsFromTableFile( tableFile ) );

		final List< String > pathColumnNames = replaceFolderAndFileColumnsByPathColumn();

		final Map< SegmentProperty, List< ? > > segmentPropertyToColumn
				= getSegmentPropertyToColumn( pathColumnNames );

		final List< TableRowImageSegment > segments
				= SegmentUtils.tableRowImageSegmentsFromColumns( columns, segmentPropertyToColumn, false );

		return segments;
	}

	private HashMap< SegmentProperty, List< ? > > getSegmentPropertyToColumn( List< String > pathColumnNames )
	{
		final HashMap< SegmentProperty, List< ? > > segmentPropertyToColumn
				= new HashMap<>();

		String labelImagePathColumnName = getLabelImagePathColumnName( pathColumnNames );

		segmentPropertyToColumn.put(
				SegmentProperty.LabelImage,
				columns.get( labelImagePathColumnName ));

		segmentPropertyToColumn.put(
				SegmentProperty.ObjectLabel,
				columns.get( COLUMN_NAME_OBJECT_LABEL ) );

		segmentPropertyToColumn.put(
				SegmentProperty.X,
				columns.get( COLUMN_NAME_OBJECT_LOCATION_CENTER_X ) );

		segmentPropertyToColumn.put(
				SegmentProperty.Y,
				columns.get( COLUMN_NAME_OBJECT_LOCATION_CENTER_Y ) );

		return segmentPropertyToColumn;
	}

	private String getLabelImagePathColumnName( List< String > pathColumnNames )
	{
		String labelImagePathColumnName = "";
		for ( String pathColumnName : pathColumnNames )
		{
			if ( pathColumnName.contains( OBJECTS ) )
			{
				labelImagePathColumnName = pathColumnName;
				break;
			}
		}
		return labelImagePathColumnName;
	}

	private List< String > replaceFolderAndFileColumnsByPathColumn()
	{
		final int numRows = columns.values().iterator().next().size();
		imageNameToFolderAndFileColumns = fetchFolderAndFileColumns( columns.keySet() );

		final String tableFile = inputTableFile.toString();

		final List< String > pathColumnNames = new ArrayList<>();

		for ( String imageName : imageNameToFolderAndFileColumns.keySet() )
		{
			final String fileColumnName = imageNameToFolderAndFileColumns.get( imageName ).fileColumn();
			final String folderColumnName = imageNameToFolderAndFileColumns.get( imageName ).folderColumn();
			final List< ? > fileColumn = columns.get( fileColumnName );
			final List< ? > folderColumn = columns.get( folderColumnName );

			final List< String > pathColumn = new ArrayList<>();

			for ( int row = 0; row < numRows; row++ )
			{
				String imagePath = folderColumn.get( row ) + File.separator + fileColumn.get( row );

				if ( isPathMapping )
				{
					imagePath = getMappedPath( imagePath );
				}

				imagePath = Tables.getRelativePath( tableFile, imagePath ).toString();

				pathColumn.add( imagePath );
			}

			columns.remove( fileColumnName );
			columns.remove( folderColumnName );

			final String pathColumnName = getPathColumnName( imageName );
			columns.put( pathColumnName, pathColumn );
			pathColumnNames.add( pathColumnName );
		}

		return pathColumnNames;
	}

	public String getPathColumnName( String imageName )
	{
		String pathColumn = "Path_" + imageName;

		return pathColumn;
	}

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
		String matchingFileColumn = null;

		for ( String column : columns )
		{
			if ( column.contains( CELLPROFILER_FILE_COLUMN_PREFIX ) && column.contains( image ) )
			{
				matchingFileColumn = column;
				break;
			}
		}

		return matchingFileColumn;
	}
}
