import de.embl.cba.tables.TableUtils;
import de.embl.cba.tables.cellprofiler.CellProfilerDataset;
import de.embl.cba.tables.cellprofiler.CellProfilerOutputExplorer;
import de.embl.cba.tables.cellprofiler.FolderAndFileColumn;
import de.embl.cba.tables.cellprofiler.ParseCellProfilerTable;
import de.embl.cba.tables.objects.ObjectTablePanel;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class DevelopParseCellProfilerTable
{

	public static final String DATASET_INDEX = "ImageNumber";
	public static final String FOLDER = "PathName_";
	public static final String FILE = "FileName_";
	private static ArrayList< String > columns;

	public static void main( String[] args ) throws IOException
	{
		//final File file = new File( TestTableLoading.class.getResource( "cellprofiler-table.txt" ).getFileColumn() );

		final File file = new File("/Users/tischer/Documents/daja-schichler-nucleoli-segmentation--data/20190116_for_classification_interphase_versus_mitotic/concatenated_tables/merged_images_nuclei.txt");
		final JTable table = TableUtils.loadTable( file, "\t" );

		final HashMap< Object, CellProfilerDataset > datasets = new ParseCellProfilerTable( table ).getDatasets();

		new CellProfilerOutputExplorer( table, datasets );

	}

}
