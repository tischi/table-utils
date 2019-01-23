import de.embl.cba.tables.TableUtils;
import de.embl.cba.tables.cellprofiler.CellProfilerDataset;
import de.embl.cba.tables.cellprofiler.CellProfilerOutputExplorer;
import de.embl.cba.tables.cellprofiler.CellProfilerTableParser;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ExploreCellProfilerOutputDevelop
{

	public static final String DATASET_INDEX = "ImageNumber";
	public static final String FOLDER = "PathName_";
	public static final String FILE = "FileName_";
	private static ArrayList< String > columns;

	public static void main( String[] args ) throws IOException
	{
		//final File file = new File( TestTableLoading.class.getResource( "cellprofiler-table.txt" ).getFileColumn() );

//		final File file = new File("/Users/tischer/Documents/daja-schichler-nucleoli-segmentation--data/20190116_for_classification_interphase_versus_mitotic/concatenated_tables/merged_images_nuclei.txt");

		final File file = new File("/Volumes/cba/exchange/Daja-Christian/20190116_for_classification_interphase_versus_mitotic/concatenated_tables/merged_images_nuclei.txt" );
		final JTable table = TableUtils.loadTable( file, "\t" );

		final CellProfilerTableParser cellProfilerTableParser = new CellProfilerTableParser( table );

		final HashMap< Object, CellProfilerDataset > datasets = cellProfilerTableParser.getDatasets();

		new CellProfilerOutputExplorer( table, datasets );

	}

}
