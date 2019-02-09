package de.embl.cba.tables.ui;

import de.embl.cba.tables.TableColumns;
import de.embl.cba.tables.modelview.images.FileImageSourcesModel;
import de.embl.cba.tables.modelview.images.FileImageSourcesModelFactory;
import de.embl.cba.tables.modelview.segments.ColumnBasedTableRowImageSegment;
import de.embl.cba.tables.modelview.segments.ImageSegmentCoordinate;
import de.embl.cba.tables.modelview.segments.SegmentUtils;
import de.embl.cba.tables.modelview.views.DefaultBdvAndTableView;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Plugin(type = Command.class, menuPath = "Plugins>Segmentation>Explore>Segments Table with Image Paths" )
public class ExploreSegmentsTableWithImagePathsCommand< R extends RealType< R > & NativeType< R > >
		implements Command
{
	@Parameter ( label = "Segments table" )
	public File segmentsTableFile;

	private LinkedHashMap< String, List< Object > > columns;

	@Override
	public void run()
	{
		final List< ColumnBasedTableRowImageSegment > tableRowImageSegments
				= createSegments( segmentsTableFile );

		final String tablePath = segmentsTableFile.toString();

		final FileImageSourcesModel imageSourcesModel =
				new FileImageSourcesModelFactory(
						tableRowImageSegments,
						tablePath).getImageSourcesModel();

		new DefaultBdvAndTableView( tableRowImageSegments, imageSourcesModel );
	}

	private List<ColumnBasedTableRowImageSegment> createSegments(
			File tableFile )
	{
		columns = TableColumns.columnsFromTableFile( tableFile );

//		columns = TableColumns.addLabelImageIdColumn(
//				this.columns,
//				COLUMN_NAME_LABEL_IMAGE_ID,
//				"em-segmented-cells-labels" );

		final Map< ImageSegmentCoordinate, List< Object > > coordinateToColumn
				= createCoordinateToColumnMap();

		final List< ColumnBasedTableRowImageSegment > segments
				= SegmentUtils.tableRowImageSegmentsFromColumns( columns, coordinateToColumn );

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
