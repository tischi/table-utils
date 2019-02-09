import de.embl.cba.tables.ui.ExploreLabelImageCommand;
import de.embl.cba.tables.ui.ExploreSegmentsTableWithImagePathsCommand;
import net.imagej.ImageJ;

public class RunExploreSegmentsTableWithImagePathsCommand
{
	public static void main( String[] args )
	{
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();

		ij.command().run( ExploreSegmentsTableWithImagePathsCommand.class, true );
	}
}

