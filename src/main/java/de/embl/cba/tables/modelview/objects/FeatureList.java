package de.embl.cba.tables.modelview.objects;

import java.util.ArrayList;

public interface FeatureList
{
	ArrayList< String > featureNames();

	Object featureValue( String featureName );

	Object[] getFeatureValues();

}
