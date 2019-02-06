package de.embl.cba.tables.commands;

import de.embl.cba.tables.TableUtils;
import de.embl.cba.tables.modelview.images.FileImageSourcesModel;
import de.embl.cba.tables.modelview.images.ImageSourcesModelFactory;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;


@Plugin(type = Command.class, menuPath = "Plugins>Segmentation>Explore>Label Image and Segments Table" )
public class ExploreLabelImageAndSegmentsTableCommand< R extends RealType< R > & NativeType< R > >
		implements Command
{
	@Parameter ( label = "Label mask image (single channel, 2D+t or 3D+t)" )
	public File labelMasksFile;

	@Parameter ( label = "Segments table" )
	public File tableFile;

	@Parameter ( label = "Intensities image (optional)", required = false )
	public File intensitiesFile;


	private LinkedHashMap< String, ArrayList< Object > > columns;

	@Override
	public void run()
	{
		final ArrayList< ColumnBasedTableRowImageSegment > tableRowImageSegments
				= createAnnotatedImageSegments( tableFile );

		final String tablePath = tableFile.toString();

		final FileImageSourcesModel imageSourcesModel =
				new ImageSourcesModelFactory(
						tableRowImageSegments,
						tablePath,
						2 ).getImageSourcesModel();

		DefaultBdvAndTableView.show( tableRowImageSegments, imageSourcesModel );
	}

	private ArrayList<ColumnBasedTableRowImageSegment> createAnnotatedImageSegments(
			File inputTableFile )
	{
		columns = TableUtils.columnsFromTableFile( tableFile, null );

		final HashMap< ImageSegmentCoordinate, ArrayList< Object > > imageSegmentCoordinateToColumn
				= getImageSegmentCoordinateToColumn( pathColumnNames );

		final ArrayList< ColumnBasedTableRowImageSegment > segments
				= SegmentUtils.tableRowImageSegmentsFromColumns( columns, imageSegmentCoordinateToColumn );

		return segments;
	}

	private HashMap< ImageSegmentCoordinate, ArrayList< Object > > getImageSegmentCoordinateToColumn( ArrayList< String > pathColumnNames )
	{
		final HashMap< ImageSegmentCoordinate, ArrayList< Object > > imageSegmentCoordinateToColumn
				= new HashMap<>();

		String labelImagePathColumnName = getLabelImagePathColumnName( pathColumnNames );

		imageSegmentCoordinateToColumn.put(
				ImageSegmentCoordinate.ImageId,
				columns.get( labelImagePathColumnName ));

		// TODO: UI?
//		imageSegmentCoordinateToColumn.put(
//				ImageSegmentCoordinate.LabelId,
//				columns.get( COLUMN_NAME_OBJECT_LABEL ) );
//
//		imageSegmentCoordinateToColumn.put(
//				ImageSegmentCoordinate.X,
//				columns.get( COLUMN_NAME_OBJECT_LOCATION_CENTER_X ) );
//
//		imageSegmentCoordinateToColumn.put(
//				ImageSegmentCoordinate.Y,
//				columns.get( COLUMN_NAME_OBJECT_LOCATION_CENTER_Y ) );

		return imageSegmentCoordinateToColumn;
	}


}
