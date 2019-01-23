import de.embl.cba.tables.commands.TableBdvConnectionCommand;
import net.imagej.ImageJ;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class RunTableBdvConnectionCommand2DTime
{
	public static void main( String[] args ) throws ExecutionException, InterruptedException
	{
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();

		final HashMap< String, Object > parameters = new HashMap<>();

		parameters.put( "inputTableFile",
				new File( RunTableBdvConnectionCommand.class.getResource(
						"2d+t-8bit-labelMasks-table.txt" ).getFile() ) );

		parameters.put( "inputLabelMasksFile",
				new File( RunTableBdvConnectionCommand.class.getResource(
						"2d+t-8bit-labelMasks.zip" ).getFile() ) );

		parameters.put( "inputIntensitiesFile", null );

		ij.command().run( TableBdvConnectionCommand.class, true, parameters );
	}
}
