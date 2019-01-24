package de.embl.cba.tables.modelview.objects;

import de.embl.cba.tables.modelview.datamodels.SegmentModel;

import java.util.ArrayList;

public class Segment // implements FeatureList, ImageSegment
{
	private final ArrayList< String > featureNames; // Does it sense to keep it here? (one could also take it from segmentModel)
	private final String[] featureValues;
	private final SegmentModel< ? extends Segment > segmentModel;


	public Segment( SegmentModel< ? extends Segment > segmentModel,
					ArrayList< String > featureNames,
					String[] featureValues )
	{
		this.segmentModel = segmentModel;
		this.featureNames = featureNames;
		this.featureValues = featureValues;
	}

	//@Override
	public ArrayList< String > featureNames()
	{
		return featureNames;
	}

	//@Override
	public Object featureValue( String featureName )
	{
		return featureValues[ featureNames.indexOf( featureName ) ];
	}


	//@Override
	public double getLabel()
	{
		return Double.parseDouble(
				featureValues[
						featureNames.indexOf(
								segmentModel.getLabelFeatureName() ) ] );
	}

	//@Override
	public int getTimePoint()
	{
		return Integer.parseInt(
				featureValues[
						featureNames.indexOf(
								segmentModel.getTimePointFeatureName() ) ] );
	}

	public String[] getFeatureValues()
	{
		return featureValues;
	}


}
