import de.embl.cba.tables.commands.ExploreCellProfilerObjectsCommand;
import net.imagej.ImageJ;


public class RunCellProfilerOutputExplorerCommand
{
	public static void main( String[] args )
	{
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();


		ij.command().run( ExploreCellProfilerObjectsCommand.class, true );
	}
}

