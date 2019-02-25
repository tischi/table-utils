package de.embl.cba.tables.ui;

import de.embl.cba.tables.TableColumns;
import de.embl.cba.tables.modelview.images.FileImageSourcesModel;
import de.embl.cba.tables.modelview.images.FileImageSourcesModelFactory;
import de.embl.cba.tables.modelview.segments.ImageSegmentCoordinate;
import de.embl.cba.tables.modelview.segments.SegmentUtils;
import de.embl.cba.tables.modelview.segments.TableRowImageSegment;
import de.embl.cba.tables.modelview.views.DefaultTableAndBdvViews;
import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.widget.Button;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Plugin(type = Command.class, menuPath =
		"Plugins>Segmentation>Explore>Explore Objects Table" )
public class ExploreSegmentsTableWithImagePathsCommand implements Command
{
	@Parameter
	LogService logService;

	@Parameter ( label = "Table" )
	File tableFile;

	@Parameter ( label = "Image Path Columns Id" )
	String imagePathColumnId = "Path_";

	@Parameter ( label = "Log Image Paths", callback = "logImagePaths")
	Button logImagePathsButton;

	@Parameter ( label = "All Images are 2D" )
	boolean is2D;

	@Parameter ( label = "Timepoints in table are one-based" )
	boolean isOneBasedTimePoint;

	@Parameter ( label = "Paths to images in table are relative" )
	boolean isRelativeImagePath;

	@Parameter ( label = "Parent folder (for relative image paths)",
			required = false, style = "directory")
	File imageRootFolder;


	private LinkedHashMap< String, List< ? > > columns;
	private Map< ImageSegmentCoordinate, String > coordinateToColumnName;


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

		final DefaultTableAndBdvViews views =
				new DefaultTableAndBdvViews( tableRowImageSegments, imageSourcesModel );

		views.getTableRowsTableView().categoricalColumnNames().add(
				coordinateToColumnName.get( ImageSegmentCoordinate.ObjectLabel ) );

	}

	private List< TableRowImageSegment > createSegments(
			File tableFile )
	{
		columns = TableColumns.asTypedColumns(
				       TableColumns.stringColumnsFromTableFile( tableFile ) );

		final Map< ImageSegmentCoordinate, List< ? > > coordinateToColumn
				= createCoordinateToColumnMap();

		final List< TableRowImageSegment > segments
				= SegmentUtils.tableRowImageSegmentsFromColumns(
						columns, coordinateToColumn, isOneBasedTimePoint );

		return segments;
	}

	private LinkedHashMap< ImageSegmentCoordinate, List< ? > > createCoordinateToColumnMap( )
	{
		final ImageSegmentCoordinateColumnsSelectionDialog selectionDialog
				= new ImageSegmentCoordinateColumnsSelectionDialog( columns.keySet() );

		coordinateToColumnName = selectionDialog.fetchUserInput();

		final LinkedHashMap< ImageSegmentCoordinate, List< ? > > coordinateToColumn
				= new LinkedHashMap<>();

		for( ImageSegmentCoordinate coordinate : coordinateToColumnName.keySet() )
		{
			coordinateToColumn.put(
					coordinate,
					columns.get( coordinateToColumnName.get( coordinate ) ) );
		}

		return coordinateToColumn;
	}

	private void logImagePaths()
	{
		final LinkedHashMap< String, List< String > > columns =
				TableColumns.stringColumnsFromTableFile( tableFile );

		for ( String column : columns.keySet() )
		{
			imagePathColumnId = "Path_";
			if ( column.contains( imagePathColumnId ) )
			{
				final HashSet< String > paths = new HashSet<>( columns.get( column ) );
				paths.forEach( s -> logService.info( s ) );
			}
		}


	}

}
