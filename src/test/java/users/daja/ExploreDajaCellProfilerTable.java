package users.daja;

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
		command.tableFile = new File("/Volumes/cuylen/01_Share/Filemaker/01_Experiments/Experiments_0200/0274/03_analysis/e-0274_MitSol2-01-07-Date15-07-2019/02_Concatenating-and-merging/02_output/R_output/e-0274-LayoutMitSol2-Batch01-Repl07_merged_images_nuclei_for_explore_objects_table_relative_paths_first-half.txt");
		command.logService = ij.log();
		command.imagePathColumnsId = ExploreObjectsTableCommand.IMAGE_PATH_COLUMNS_ID_DEFAULT;
		command.imageRootFolder = new File( "/Volumes" );

		command.run();
	}
}
