package de.embl.cba.tables.command;

import de.embl.cba.tables.TableColumns;
import de.embl.cba.tables.cellprofiler.CellProfilerUtils;
import de.embl.cba.tables.image.FileImageSourcesModel;
import de.embl.cba.tables.image.FileImageSourcesModelFactory;
import de.embl.cba.tables.imagesegment.SegmentProperty;
import de.embl.cba.tables.imagesegment.SegmentPropertyColumnsSelectionDialog;
import de.embl.cba.tables.imagesegment.SegmentUtils;
import de.embl.cba.tables.tablerow.TableRowImageSegment;
import de.embl.cba.tables.view.combined.SegmentsTableAndBdvViews;
import de.embl.cba.tables.view.combined.SegmentsTableBdvAnd3dViews;
import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.widget.Button;

import java.io.File;
import java.util.*;

import static de.embl.cba.tables.imagesegment.SegmentPropertyColumnsSelectionDialog.NO_COLUMN_SELECTED;

@Plugin(type = Command.class, menuPath =
		"Plugins>Segmentation>Explore>Explore Objects Table" )
public class ExploreObjectsTableCommand implements Command
{
	public static final String DEFAULT = "\'Path_\' (Default)";
	public static final String IMAGE_PATH_COLUMNS_ID_CELL_PROFILER = "\'FileName_\' and \'PathName_\' (CellProfiler)";
	public static final String IMAGE_PATH_COLUMNS_ID_DEFAULT = "Path_";

	@Parameter
	public LogService logService;

	@Parameter ( label = "Table" )
	public File tableFile;

	@Parameter ( label = "Image Path Column IDs", choices = { DEFAULT })
	public String imagePathColumnsId;

	@Parameter ( label = "Paths to images are relative" )
	public boolean isRelativeImagePath;

	@Parameter ( label = "Parent folder (for relative image paths)",
			required = false, style = "directory")
	public File imageRootFolder;

	@Parameter ( label = "Apply Path Mapping" )
	public boolean isPathMapping = false;

	@Parameter ( label = "Root path in table (for path mapping)" )
	public String imageRootPathInTable = "/Volumes/";

	@Parameter ( label = "Root path on this computer (for path mapping)" )
	public String imageRootPathOnThisComputer = "/g/";

	@Parameter ( label = "Log Image Paths", callback = "logImagePaths")
	private Button logImagePathsButton;

	@Parameter ( label = "All images are 2D" )
	public boolean is2D;

	@Parameter ( label = "Time points in table are one-based" )
	public boolean isOneBasedTimePoint;


	private LinkedHashMap< String, List< ? > > columns;
	private Map< SegmentProperty, String > coordinateToColumnName;


	public void run()
	{
		if ( ! isRelativeImagePath ) imageRootFolder = new File("" );

		final List< TableRowImageSegment > tableRowImageSegments
				= createSegments( tableFile );

		final FileImageSourcesModel imageSourcesModel =
				new FileImageSourcesModelFactory(
						tableRowImageSegments,
						imageRootFolder.toString(),
						is2D ).getImageSourcesModel();

		if ( is2D )
		{
			new SegmentsTableAndBdvViews(
					tableRowImageSegments,
					imageSourcesModel,
					tableFile.getName() );
		}
		else
		{
			new SegmentsTableBdvAnd3dViews(
					tableRowImageSegments,
					imageSourcesModel,
					tableFile.getName() );
		}

	}

	private List< TableRowImageSegment > createSegments(
			File tableFile )
	{
		if ( columns == null )
			loadColumnsFromFile( tableFile );

		final Map< SegmentProperty, List< ? > > coordinateToColumn
				= createCoordinateToColumnMap( columns.keySet() );

		final List< TableRowImageSegment > segments
				= SegmentUtils.tableRowImageSegmentsFromColumns(
						columns, coordinateToColumn, isOneBasedTimePoint );

		return segments;
	}

	private void loadColumnsFromFile( File tableFile )
	{
		columns = TableColumns.asTypedColumns(
				TableColumns.stringColumnsFromTableFile( tableFile ) );

		if ( imagePathColumnsId.equals( IMAGE_PATH_COLUMNS_ID_CELL_PROFILER ) )
		{
			CellProfilerUtils.replaceFolderAndFileColumnsByPathColumn( columns );
		}

		if ( isPathMapping )
		{
			// TODO
		}

	}

	private LinkedHashMap< SegmentProperty, List< ? > > createCoordinateToColumnMap(
			Set< String > columnNames )
	{
		final SegmentPropertyColumnsSelectionDialog selectionDialog
				= new SegmentPropertyColumnsSelectionDialog( columnNames );

		coordinateToColumnName = selectionDialog.fetchUserInput();

		final LinkedHashMap< SegmentProperty, List< ? > > coordinateToColumn
				= new LinkedHashMap<>();

		for( SegmentProperty coordinate : coordinateToColumnName.keySet() )
		{
			if ( coordinateToColumnName.get( coordinate ).equals( NO_COLUMN_SELECTED ) )
				continue;

			coordinateToColumn.put(
					coordinate,
					this.columns.get( coordinateToColumnName.get( coordinate ) ) );
		}

		return coordinateToColumn;
	}

	private void logImagePaths()
	{
		if ( columns == null )
			loadColumnsFromFile( tableFile );

		for ( String column : columns.keySet() )
		{
			if ( column.contains( IMAGE_PATH_COLUMNS_ID_DEFAULT ) )
			{
				final HashSet< String > pathColumnNames =
						new HashSet<>( ( List< String > ) columns.get( column ) );
				pathColumnNames.forEach( s -> logService.info( s ) );
			}
		}
	}

}
