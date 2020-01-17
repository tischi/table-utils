package playground;

import de.embl.cba.table.ui.TableUIs;
import net.imagej.ImageJ;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class GetStringChoiceSciJavaUIDemo
{
	public static void main( String[] args ) throws ExecutionException, InterruptedException
	{
		final ImageJ ij = new ImageJ();
		ij.launch(  );

		final ArrayList< String > choices = new ArrayList<>();
		choices.add( "Hello" );
		choices.add( "World" );
		final String selectedString = TableUIs.getStringChoiceSciJavaUI( "columnName", choices, ij.context() );
		System.out.println( selectedString );
	}
}
