package commands;

import de.embl.cba.tables.ui.ExploreCellProfilerCommand;
import net.imagej.ImageJ;


public class RunExploreCellProfilerOutputCommand
{
	public static void main( String[] args )
	{
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();

		ij.command().run( ExploreCellProfilerCommand.class, true );
	}
}
