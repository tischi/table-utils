package de.embl.cba.tables.command;

import de.embl.cba.tables.TableColumns;
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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static de.embl.cba.tables.imagesegment.SegmentPropertyColumnsSelectionDialog.NO_COLUMN_SELECTED;

@Plugin(type = Command.class, menuPath =
		"Plugins>Segmentation>Explore>Explore Objects Table" )
public class ExploreObjectsTableCommand implements Command
{
	@Parameter
	public LogService logService;

	@Parameter ( label = "Table" )
	public File tableFile;

	@Parameter ( label = "Image Path Columns Id" )
	public String imagePathColumnId = "Path_";

	@Parameter ( label = "Log Image Paths", callback = "logImagePaths")
	private Button logImagePathsButton;

	@Parameter ( label = "All image are 2D" )
	public boolean is2D;

	@Parameter ( label = "Timepoints in table are one-based" )
	public boolean isOneBasedTimePoint;

	@Parameter ( label = "Paths to image in table are relative" )
	public boolean isRelativeImagePath;

	@Parameter ( label = "Parent folder (for relative image paths)",
			required = false, style = "directory")
	public File imageRootFolder;


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
		columns = TableColumns.asTypedColumns(
				       TableColumns.stringColumnsFromTableFile( tableFile ) );

		final Map< SegmentProperty, List< ? > > coordinateToColumn
				= createCoordinateToColumnMap();

		final List< TableRowImageSegment > segments
				= SegmentUtils.tableRowImageSegmentsFromColumns(
						columns, coordinateToColumn, isOneBasedTimePoint );

		return segments;
	}

	private LinkedHashMap< SegmentProperty, List< ? > > createCoordinateToColumnMap( )
	{
		final SegmentPropertyColumnsSelectionDialog selectionDialog
				= new SegmentPropertyColumnsSelectionDialog( columns.keySet() );

		coordinateToColumnName = selectionDialog.fetchUserInput();

		final LinkedHashMap< SegmentProperty, List< ? > > coordinateToColumn
				= new LinkedHashMap<>();

		for( SegmentProperty coordinate : coordinateToColumnName.keySet() )
		{
			if ( coordinateToColumnName.get( coordinate ).equals( NO_COLUMN_SELECTED ) )
				continue;

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
