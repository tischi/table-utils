package de.embl.cba.tables.commands;

import de.embl.cba.bdv.utils.lut.GlasbeyARGBLut;
import de.embl.cba.tables.TableUtils;
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
import java.util.ArrayList;
import java.util.HashMap;


@Plugin(type = Command.class, menuPath = "Plugins>Segmentation>Explore>CellProfiler Objects" )
public class ExploreCellProfilerObjectsCommand< R extends RealType< R > & NativeType< R > >
		implements Command
{
	@Parameter ( label = "CellProfiler Table" )
	public File inputTableFile;

	@Parameter ( label = "Label Image Path Name Column" )
	public String labelImageFileNameColumn = "FileName_Objects_Nuclei_Labels";

	@Parameter ( label = "Label Image File Name Column" )
	public String labelImagePathNameColumn = "PathName_Objects_Nuclei_Labels";

	@Parameter ( label = "Apply Path Mapping" )
	public boolean applyPathMapping = false;

	@Parameter ( label = "Image Path Mapping (Table)" )
	public String imageRootPathInTable = "/Volumes/cba/exchange/Daja-Christian/20190116_for_classification_interphase_versus_mitotic";

	@Parameter ( label = "Image Path Mapping (This Computer)" )
	public String imageRootPathOnThisComputer = "/Users/tischer/Documents/daja-schichler-nucleoli-segmentation--data/2019-01-31";

	@Override
	public void run()
	{
		
		final ArrayList< AnnotatedImageSegment > annotatedImageSegments
				= createCellProfilerImageSegments( inputTableFile );

		final ImageSourcesModelFromAnnotatedSegmentsFactory< AnnotatedImageSegment > factory
				= new ImageSourcesModelFromAnnotatedSegmentsFactory<>(
					annotatedImageSegments,
					imageRootPathInTable, // TODO: make PathMapper class
					imageRootPathOnThisComputer,
					2
			);

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
		if ( ! applyPathMapping )
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

	public ArrayList< AnnotatedImageSegment > createCellProfilerImageSegments( File tableFile )
	{
		final HashMap< ImageSegmentCoordinate, String > coordinateToColumnNameAndIndexMap = new HashMap<>();

		coordinateToColumnNameAndIndexMap.put(
				ImageSegmentCoordinate.ImageId,
				labelImageFileNameColumn + SegmentUtils.FOLDER_AND_FILE_COLUMNS + labelImagePathNameColumn  );

		coordinateToColumnNameAndIndexMap.put( ImageSegmentCoordinate.Label, "Number_Object_Number");
		coordinateToColumnNameAndIndexMap.put( ImageSegmentCoordinate.X, "Location_Center_X" );
		coordinateToColumnNameAndIndexMap.put( ImageSegmentCoordinate.Y, "Location_Center_Y" );

		// TODO: merge FileAndPath columns at this point
		// TODO: also fix the pathmapping here.

		final ArrayList< AnnotatedImageSegment > annotatedImageSegments
				= TableUtils.segmentsFromTableFile(
						tableFile,
						null,
						coordinateToColumnNameAndIndexMap,
						new DefaultImageSegmentBuilder() );

		return annotatedImageSegments;
	}

}
