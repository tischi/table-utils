import de.embl.cba.tables.commands.ExploreLabelImageCommand;
import de.embl.cba.tables.commands.ExploreMorphoLibJSegmentationCommand;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.ResultsTable;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import net.imagej.ImageJ;

public class RunExploreMorphoLibJSegmentationCommand
{
	public static void main( String[] args )
	{
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();

		final ResultsTable resultsTable = new ResultsTable( 10 );
		resultsTable.show( "Table" );
		resultsTable.addValue( "Column01", 10 );

		final ResultsTable resultsTable2 = new ResultsTable( 10 );
		resultsTable2.show( "Table2" );
		resultsTable2.addValue( "Column01", 10 );


//		final net.imagej.table.ResultsTable resultsTable1 = new net.imagej.table.ResultsTable();

		final ImagePlus imagePlus = new ImagePlus(
				"Image",
				new ShortProcessor( 100, 100 ) );

		imagePlus.show();

		ij.command().run( ExploreMorphoLibJSegmentationCommand.class, true );
	}
}

