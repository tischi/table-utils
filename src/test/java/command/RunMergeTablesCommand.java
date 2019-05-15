package command;

import de.embl.cba.tables.command.ExploreLabelImageCommand;
import de.embl.cba.tables.command.MergeTablesCommand;
import ij.IJ;
import net.imagej.ImageJ;

public class RunMergeTablesCommand
{
	public static void main( String[] args )
	{
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();

		ij.command().run( MergeTablesCommand.class, true );
	}
}

