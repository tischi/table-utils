import de.embl.cba.tables.ui.ExploreMorphoLibJSegmentationCommand;
import ij.IJ;
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

//		IJ.open( RunExploreMorphoLibJSegmentationCommand.class.getResource(
//				"3d-image-lbl-morpho.csv" ).getFile() );
//
//		IJ.open( RunExploreMorphoLibJSegmentationCommand.class.getResource(
//				"3d-image-lbl.zip" ).getFile() );

		IJ.open( RunExploreMorphoLibJSegmentationCommand.class.getResource(
				"blobs.zip" ).getFile() );

		IJ.open( RunExploreMorphoLibJSegmentationCommand.class.getResource(
				"mask-lbl.zip" ).getFile() );

		IJ.open( RunExploreMorphoLibJSegmentationCommand.class.getResource(
				"blobs-lbl-Morphometry.csv" ).getFile() );

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

