import de.embl.cba.tables.ui.ExploreObjectsTableCommand;
import net.imagej.ImageJ;

public class RunExploreObjectsTableCommand
{
	public static void main( String[] args )
	{
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();

		ij.command().run( ExploreObjectsTableCommand.class, true );
	}
}

