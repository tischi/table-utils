import de.embl.cba.tables.ui.ExploreLabelImageCommand;
import ij.IJ;
import net.imagej.ImageJ;

public class RunExploreLabelImageCommand
{
	public static void main( String[] args )
	{
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();

		IJ.open( RunExploreMorphoLibJSegmentationCommand.class.getResource(
				"blobs.zip" ).getFile() );

		IJ.open( RunExploreMorphoLibJSegmentationCommand.class.getResource(
				"mask-lbl.zip" ).getFile() );

		ij.command().run( ExploreLabelImageCommand.class, true );
	}
}

