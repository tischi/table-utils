package users.valerie;

import de.embl.cba.tables.command.ExploreObjectsTableCommand;
import de.embl.cba.tables.imagesegment.SegmentProperty;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CORBELMeeting2019
{
	public static void main( String[] args )
	{
		Map< SegmentProperty, String > propertyToColumnName = new HashMap<>(  );

		propertyToColumnName.put( SegmentProperty.ObjectLabel, "Object_Label" );
		propertyToColumnName.put( SegmentProperty.LabelImage, "Path_LabelMasks" );
		propertyToColumnName.put( SegmentProperty.X, "Centroid_X_Pixel" );
		propertyToColumnName.put( SegmentProperty.Y, "Centroid_Y_Pixel" );
		propertyToColumnName.put( SegmentProperty.Z, "Centroid_Z_Pixel" );
		propertyToColumnName.put( SegmentProperty.T, "Centroid_Time_Frames" );

		final ExploreObjectsTableCommand command = new ExploreObjectsTableCommand();
		command.tableFile = new File("/Users/tischer/Documents/valerie-blanche-petegnief-CORBEL-microglia-quantification--data/2019-October-CORBEL-Meeting/morphology_data_with_annotated_cells_and_predictions.txt");
		command.isRelativeImagePath = true;
		command.imageRootFolder = new File("/Users/tischer/Documents/valerie-blanche-petegnief-CORBEL-microglia-quantification--data/2019-October-CORBEL-Meeting/images");
		command.propertyToColumnName = propertyToColumnName;
		command.run();

	}
}
