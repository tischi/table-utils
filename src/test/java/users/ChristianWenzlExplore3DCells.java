package users;

import de.embl.cba.tables.ExploreIntensityImageAndLabelImageAndTable;
import net.imagej.ImageJ;

import java.io.File;

public class ChristianWenzlExplore3DCells
{
	public static void main( String[] args )
	{
		new ImageJ().ui().showUI();

		new ExploreIntensityImageAndLabelImageAndTable(
				new File( "/Users/tischer/Documents/christian-wenzl--data/Archive_new/test_raw.tif" ),
				new File( "/Users/tischer/Documents/christian-wenzl--data/Archive_new/test_labels.tif" ),
				"/Users/tischer/Documents/christian-wenzl--data/Archive_new/table.csv",
				true,
				false
		);
	}
}
