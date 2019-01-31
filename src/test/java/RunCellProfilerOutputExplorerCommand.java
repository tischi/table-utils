import de.embl.cba.tables.commands.CellProfilerOutputExplorerCommand;
import net.imagej.ImageJ;

import java.util.concurrent.ExecutionException;


public class RunCellProfilerOutputExplorerCommand
{
	public static void main( String[] args )
	{
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();


		ij.command().run( CellProfilerOutputExplorerCommand.class, true );
	}
}

