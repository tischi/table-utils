package users;

import de.embl.cba.tables.ExploreIntensityImageAndLabelImageAndTable;

import java.io.File;

public class ChristianWenzlExplore3DCells
{
	public static void main( String[] args )
	{

		new ExploreIntensityImageAndLabelImageAndTable(
				new File( "/Users/tischer/Documents/christian-wenzl--data/Archive/test_raw.tif" ),
				new File( "/Users/tischer/Documents/christian-wenzl--data/Archive/test_labels.tif" ),
				new File( "/Users/tischer/Documents/christian-wenzl--data/Archive/table.csv")
				);
	}
}
