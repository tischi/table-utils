import de.embl.cba.tables.ui.ExploreLabelImageCommand;
import net.imagej.ImageJ;

public class RunExploreLabelImageCommand
{
	public static void main( String[] args )
	{
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();

		ij.command().run( ExploreLabelImageCommand.class, true );
	}
}

