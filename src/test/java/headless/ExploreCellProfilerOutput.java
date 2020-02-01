package headless;

import de.embl.cba.tables.command.ExploreObjectsTableCommand;
import net.imagej.ImageJ;

import java.io.File;

public class ExploreCellProfilerOutput
{
	public static void main( String[] args )
	{
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();

		final ExploreObjectsTableCommand command = new ExploreObjectsTableCommand();

		command.imageRootFolder = new File( ExploreObjectsTableCommand.class.getResource( "" ).getFile() );
		command.is2D = true;
		command.isOneBasedTimePoint = false;
		command.isRelativeImagePath = true;
		command.imageRootFolder = new File( "/Users/tischer/Documents/table-utils/" +
				"src/test/resources/" +
				"cellprofiler/" );
		command.tableFile = new File("/Users/tischer/Documents/table-utils/" +
				"src/test/resources/" +
				"cellprofiler/output/final_table.csv");
		command.logService = ij.log();
		command.imagePathColumnsId = ExploreObjectsTableCommand.IMAGE_PATH_COLUMNS_ID_DEFAULT;

		command.run();

	}
}
