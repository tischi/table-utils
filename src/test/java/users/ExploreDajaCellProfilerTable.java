package users;

import de.embl.cba.tables.command.ExploreObjectsTableCommand;
import net.imagej.ImageJ;

import java.io.File;

public class ExploreDajaCellProfilerTable
{
	public static void main( String[] args )
	{
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();

		final ExploreObjectsTableCommand command = new ExploreObjectsTableCommand();

		command.is2D = true;
		command.isOneBasedTimePoint = false;
		command.isRelativeImagePath = true;
		command.tableFile = new File("/Users/tischer/Desktop/table_small.txt");
		command.logService = ij.log();
		command.imagePathColumnsId = ExploreObjectsTableCommand.IMAGE_PATH_COLUMNS_ID_DEFAULT;
		command.imageRootFolder = new File( "/Volumes" );
		command.isPathMapping = false;

		command.run();

	}
}
