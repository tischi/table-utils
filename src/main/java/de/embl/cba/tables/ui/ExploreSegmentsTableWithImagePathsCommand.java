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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Plugin(type = Command.class, menuPath = "Plugins>Segmentation>Explore>Segments Table with Image Paths" )
public class ExploreSegmentsTableWithImagePathsCommand< R extends RealType< R > & NativeType< R > >
		implements Command
{
	private static final String COLUMN_NAME_LABEL_IMAGE_ID = "LabelImageId";

	@Parameter ( label = "Segments table" )
	public File segmentsTableFile;

	private LinkedHashMap< String, List< Object > > columns;

	@Override
	public void run()
	{
		final List< ColumnBasedTableRowImageSegment > tableRowImageSegments
				= createAnnotatedImageSegments( segmentsTableFile );

		final String tablePath = segmentsTableFile.toString();

		final FileImageSourcesModel imageSourcesModel =
				new FileImageSourcesModelFactory(
						tableRowImageSegments,
						tablePath,
						2 ).getImageSourcesModel();

		new DefaultBdvAndTableView( tableRowImageSegments, imageSourcesModel );

	}

	private List<ColumnBasedTableRowImageSegment> createAnnotatedImageSegments(
			File tableFile )
	{
		columns = TableColumns.columnsFromTableFile( tableFile );

//		columns = TableColumns.addLabelImageIdColumn(
//				this.columns,
//				COLUMN_NAME_LABEL_IMAGE_ID,
//				"em-segmented-cells-labels" );

		final HashMap< ImageSegmentCoordinate, List< Object > > coordinateToColumn
				= createCoordinateToColumn( );

		final List< ColumnBasedTableRowImageSegment > segments
				= SegmentUtils.tableRowImageSegmentsFromColumns( columns, coordinateToColumn );

		return segments;
	}

	private Map< ImageSegmentCoordinate, List< Object > > createCoordinateToColumn( )
	{
		final ImageSegmentCoordinateColumnsSelectionDialog selectionDialog
				= new ImageSegmentCoordinateColumnsSelectionDialog( columns.keySet() );

		return selectionDialog.fetchUserInput();
	}


}
