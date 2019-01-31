package de.embl.cba.tables.commands;

import de.embl.cba.tables.TableUtils;
import de.embl.cba.tables.modelview.combined.DataModelUtils;
import de.embl.cba.tables.modelview.images.CellProfilerImageSourcesModel;
import de.embl.cba.tables.modelview.images.CellProfilerImageSourcesModelCreator;
import de.embl.cba.tables.modelview.segments.DefaultAnnotatedImageSegment;
import de.embl.cba.tables.modelview.segments.DefaultImageSegmentBuilder;
import de.embl.cba.tables.modelview.segments.ImageSegmentCoordinate;
import de.embl.cba.tables.modelview.segments.SegmentUtils;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


@Plugin(type = Command.class, menuPath = "Plugins>Segmentation>Explore>CellProfiler Output" )
public class CellProfilerOutputExplorerCommand< R extends RealType< R > & NativeType< R > >
		implements Command
{
	@Parameter ( label = "CellProfiler Table" )
	public File inputTableFile;

	@Parameter ( label = "Label Image Column Name" )
	public String lableImageColumnName = "FileName_Objects_Nuclei_Labels";

	@Parameter ( label = "Image Path Mapping (Table)" )
	public String imageRootPathInTable = "/Volumes/cba/exchange/Daja-Christian/20190116_for_classification_interphase_versus_mitotic";

	@Parameter ( label = "Image Path Mapping (This Computer)" )
	public String imageRootPathOnThisComputer = "/Users/tischer/Documents/daja-schichler-nucleoli-segmentation--data/2019-01-31";

	@Override
	public void run()
	{

		final CellProfilerImageSourcesModelCreator modelCreator = new CellProfilerImageSourcesModelCreator(
				inputTableFile,
				imageRootPathInTable,
				imageRootPathOnThisComputer,
				"\t"
		);

		final CellProfilerImageSourcesModel imageSourcesModel = modelCreator.getModel();

		final ArrayList< DefaultAnnotatedImageSegment > annotatedImageSegments
				= createCellProfilerImageSegments( inputTableFile );

		final ArrayList< String > categoricalColumns = new ArrayList<>();
		categoricalColumns.add( "Label" );


		ArrayList< String > initialSources = new ArrayList< String >();
		initialSources.add( imageSourcesModel.sources().keySet().iterator().next() );

		DataModelUtils.buildModelsAndViews(
				imageSourcesModel,
				annotatedImageSegments,
				categoricalColumns,
				true,
				initialSources );

	}

	public ArrayList< DefaultAnnotatedImageSegment > createCellProfilerImageSegments( File tableFile )
	{
		final HashMap< ImageSegmentCoordinate, String > coordinateToColumnNameAndIndexMap = new HashMap<>();
		coordinateToColumnNameAndIndexMap.put( ImageSegmentCoordinate.ImageId, "ImageNumber" + SegmentUtils.MULTIPLE_COLUMN_SEPARATOR + lableImageColumnName );
		coordinateToColumnNameAndIndexMap.put( ImageSegmentCoordinate.Label, "Number_Object_Number");
		coordinateToColumnNameAndIndexMap.put( ImageSegmentCoordinate.X, "Location_Center_X" );
		coordinateToColumnNameAndIndexMap.put( ImageSegmentCoordinate.Y, "Location_Center_Y" );

		final ArrayList< DefaultAnnotatedImageSegment > annotatedImageSegments
				= TableUtils.segmentsFromTableFile(
						tableFile,
						null,
						coordinateToColumnNameAndIndexMap, new DefaultImageSegmentBuilder() );

		return annotatedImageSegments;
	}

}
