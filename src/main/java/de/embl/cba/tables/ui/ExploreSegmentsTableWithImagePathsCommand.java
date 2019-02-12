package de.embl.cba.tables.ui;

import de.embl.cba.tables.TableColumns;
import de.embl.cba.tables.modelview.images.FileImageSourcesModel;
import de.embl.cba.tables.modelview.images.FileImageSourcesModelFactory;
import de.embl.cba.tables.modelview.segments.ImageSegmentCoordinate;
import de.embl.cba.tables.modelview.segments.SegmentUtils;
import de.embl.cba.tables.modelview.segments.TableRowImageSegment;
import de.embl.cba.tables.modelview.views.DefaultTableAndBdvViews;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Plugin(type = Command.class, menuPath = "Plugins>Segmentation>Explore>Segments Table with Image Paths" )
public class ExploreSegmentsTableWithImagePathsCommand
		implements Command
{
	@Parameter ( label = "Image Segments Table" )
	public File segmentsTableFile;

	@Parameter ( label = "2D Images" )
	boolean is2D;

	@Parameter ( label = "One-based Time-points" )
	boolean isOneBasedTimePoint;

	@Parameter ( label = "Relative Image Paths" )
	boolean isRelativeImagePath;

	@Parameter ( label = "Image Root Folder", style = "directory")
	File imageRootFolder;

	private LinkedHashMap< String, List< ? > > columns;

	@Override
	public void run()
	{
		if ( ! isRelativeImagePath ) imageRootFolder = new File("" );

		final List< TableRowImageSegment > tableRowImageSegments
				= createSegments( segmentsTableFile );

		final FileImageSourcesModel imageSourcesModel =
				new FileImageSourcesModelFactory(
						tableRowImageSegments,
						imageRootFolder.toString(),
						is2D ).getImageSourcesModel();

		new DefaultTableAndBdvViews( tableRowImageSegments, imageSourcesModel );
	}

	private List< TableRowImageSegment > createSegments(
			File tableFile )
	{
		columns = TableColumns.asTypedColumns(
				       TableColumns.stringColumnsFromTableFile( tableFile ) );

		final Map< ImageSegmentCoordinate, List< Object > > coordinateToColumn
				= createCoordinateToColumnMap();

		final List< TableRowImageSegment > segments
				= SegmentUtils.tableRowImageSegmentsFromColumns( columns, coordinateToColumn, isOneBasedTimePoint );

		return segments;
	}

	private LinkedHashMap< ImageSegmentCoordinate, List< Object > > createCoordinateToColumnMap( )
	{
		final ImageSegmentCoordinateColumnsSelectionDialog selectionDialog
				= new ImageSegmentCoordinateColumnsSelectionDialog( columns.keySet() );

		final Map< ImageSegmentCoordinate, String > coordinateToColumnName = selectionDialog.fetchUserInput();

		final LinkedHashMap< ImageSegmentCoordinate, List< Object > > coordinateToColumn = new LinkedHashMap<>();

		for( ImageSegmentCoordinate coordinate : coordinateToColumnName.keySet() )
		{
			coordinateToColumn.put(
					coordinate,
					columns.get( coordinateToColumnName.get( coordinate ) ) );
		}

		return coordinateToColumn;

	}

}
