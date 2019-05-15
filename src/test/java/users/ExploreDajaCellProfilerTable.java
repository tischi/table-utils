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
		command.tableFile = new File("/Volumes/cuylen/01_Share/Filemaker/01_Experiments/0243/03_analysis/02_Concatenating-and-merging/CP_v3/02_output/R_output/e-0234-CP-v3_merged_images_nuclei_for_explore_objects_table_relative_paths_2000_rows.txt");
		command.logService = ij.log();
		command.imagePathColumnsId = ExploreObjectsTableCommand.IMAGE_PATH_COLUMNS_ID_DEFAULT;
		command.imageRootFolder = new File( "/Volumes" );
		command.isPathMapping = false;

		command.run();

	}
}
