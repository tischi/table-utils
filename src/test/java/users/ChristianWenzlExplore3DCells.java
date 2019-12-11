package users;

import net.imagej.ImageJ;

public class ChristianWenzlExplore3DCells
{
	public static void main( String[] args )
	{
		new ImageJ().ui().showUI();

		new ExploreIntensityImageAndLabelImageAndTable(
				"/Users/tischer/Documents/christian-wenzl--data/Archive_new/test_raw.tif",
				 "/Users/tischer/Documents/christian-wenzl--data/Archive_new/test_labels.tif",
				"/Users/tischer/Documents/christian-wenzl--data/Archive_new/model.csv",
				true,
				false
		);
	}
}
