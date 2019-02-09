import de.embl.cba.tables.ui.ExploreMorphoLibJSegmentationCommand;
import ij.ImagePlus;
import ij.measure.ResultsTable;
import ij.process.ShortProcessor;
import net.imagej.ImageJ;

public class RunExploreMorphoLibJSegmentationCommand
{
	public static void main( String[] args )
	{
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();

		createResultsTable( "Table01" );
		createResultsTable( "Table02" );

		final ImagePlus imagePlus = new ImagePlus(
				"Image",
				new ShortProcessor( 100, 100 ) );

		imagePlus.show();

		ij.command().run( ExploreMorphoLibJSegmentationCommand.class, true );
	}

	private static ResultsTable createResultsTable( String title )
	{
		final ResultsTable resultsTable = new ResultsTable();
		resultsTable.addValue( "Column01", 10.0 );
		resultsTable.addValue( "Column01", 10.0 );
		resultsTable.addValue( "Column01", 20.0 );
		resultsTable.show( title );
		return resultsTable;
	}
}

