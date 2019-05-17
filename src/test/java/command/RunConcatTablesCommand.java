package command;

import de.embl.cba.tables.command.ConcatTablesCommand;
import net.imagej.ImageJ;

public class RunConcatTablesCommand
{
	public static void main( String[] args )
	{
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();

		ij.command().run( ConcatTablesCommand.class, true );
	}
}

