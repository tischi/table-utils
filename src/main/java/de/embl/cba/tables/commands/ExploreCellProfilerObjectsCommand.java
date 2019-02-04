package de.embl.cba.tables.commands;

import de.embl.cba.bdv.utils.lut.GlasbeyARGBLut;
import de.embl.cba.tables.TableUtils;
import de.embl.cba.tables.modelview.coloring.DynamicCategoryColoringModel;
import de.embl.cba.tables.modelview.coloring.SelectionColoringModel;
import de.embl.cba.tables.modelview.combined.DefaultImageSegmentsModel;
import de.embl.cba.tables.modelview.combined.DefaultTableRowsModel;
import de.embl.cba.tables.modelview.combined.ImageAndTableModels;
import de.embl.cba.tables.modelview.images.CellProfilerImageSourcesModel;
import de.embl.cba.tables.modelview.images.CellProfilerImageSourcesModelCreator;
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
import java.util.ArrayList;
import java.util.HashMap;


@Plugin(type = Command.class, menuPath = "Plugins>Segmentation>Explore>CellProfiler Objects" )
public class ExploreCellProfilerObjectsCommand< R extends RealType< R > & NativeType< R > >
		implements Command
{
	@Parameter ( label = "CellProfiler Table" )
	public File inputTableFile;

	@Parameter ( label = "Label Image Column Name" )
	public String lableImageColumnName = "FileName_Objects_Nuclei_Labels";

	@Parameter ( label = "Apply Path Mapping" )
	public boolean applyPathMapping = false;

	@Parameter ( label = "Image Path Mapping (Table)" )
	public String imageRootPathInTable = "/Volumes/cba/exchange/Daja-Christian/20190116_for_classification_interphase_versus_mitotic";

	@Parameter ( label = "Image Path Mapping (This Computer)" )
	public String imageRootPathOnThisComputer = "/Users/tischer/Documents/daja-schichler-nucleoli-segmentation--data/2019-01-31";

	@Override
	public void run()
	{
		final CellProfilerImageSourcesModel imageSourcesModel
				= createCellProfilerImageSourcesModel();

		final ArrayList< AnnotatedImageSegment > annotatedImageSegments
				= createCellProfilerImageSegments( inputTableFile );

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

	public CellProfilerImageSourcesModel createCellProfilerImageSourcesModel()
	{
		if ( ! applyPathMapping )
		{
			imageRootPathInTable = "";
			imageRootPathOnThisComputer = "";
		}

		final CellProfilerImageSourcesModelCreator modelCreator = new CellProfilerImageSourcesModelCreator(
				inputTableFile,
				imageRootPathInTable,
				imageRootPathOnThisComputer,
				"\t"
		);

		return modelCreator.getModel();
	}

	public ArrayList< AnnotatedImageSegment > createCellProfilerImageSegments( File tableFile )
	{
		final HashMap< ImageSegmentCoordinate, String > coordinateToColumnNameAndIndexMap = new HashMap<>();
		coordinateToColumnNameAndIndexMap.put( ImageSegmentCoordinate.ImageId, "ImageNumber" + SegmentUtils.MULTIPLE_COLUMN_SEPARATOR + lableImageColumnName );
		coordinateToColumnNameAndIndexMap.put( ImageSegmentCoordinate.Label, "Number_Object_Number");
		coordinateToColumnNameAndIndexMap.put( ImageSegmentCoordinate.X, "Location_Center_X" );
		coordinateToColumnNameAndIndexMap.put( ImageSegmentCoordinate.Y, "Location_Center_Y" );

		final ArrayList< AnnotatedImageSegment > annotatedImageSegments
				= TableUtils.segmentsFromTableFile(
						tableFile,
						null,
						coordinateToColumnNameAndIndexMap, new DefaultImageSegmentBuilder() );

		return annotatedImageSegments;
	}

}
