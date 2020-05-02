package headless;

import de.embl.cba.tables.command.ExploreObjectsTableCommand;
import net.imagej.ImageJ;

import java.io.File;

public class RunExploreObjectsTableCommand
{
	public static void main( String[] args )
	{
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();

		final ExploreObjectsTableCommand command = new ExploreObjectsTableCommand();

		command.imageRootFolder = new File( "/Users/tischer/Documents/table-utils/src/test/resources" );
		command.is2D = false;
		command.isOneBasedTimePoint = false;
		command.isRelativeImagePath = true;
		command.tableFile = new File("/Users/tischer/Documents/table-utils/src/test/resources/3d-image-with-paths.csv");
		command.logService = ij.log();
		command.imagePathColumnsId = ExploreObjectsTableCommand.IMAGE_PATH_COLUMNS_ID_DEFAULT;

		command.run();
	}
}
