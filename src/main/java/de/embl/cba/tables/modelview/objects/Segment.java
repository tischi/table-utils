package de.embl.cba.tables.modelview.objects;

import de.embl.cba.tables.modelview.datamodels.SegmentModel;

import java.util.ArrayList;

public class Segment implements FeatureList, ImageSegment
{
	private final ArrayList< String > featureNames; // Does it sense to keep it here? (one could also take it from segmentModel)
	private final ArrayList< String > featureValues;
	private final SegmentModel< Segment > segmentModel;

	public Segment( SegmentModel< Segment > segmentModel,
					ArrayList< String > featureNames,
					ArrayList< String > featureValues )
	{
		this.segmentModel = segmentModel;
		this.featureNames = featureNames;
		this.featureValues = featureValues;
	}

	@Override
	public ArrayList< String > featureNames()
	{
		return featureNames;
	}

	@Override
	public Object featureValue( String featureName )
	{
		return featureValues.get( featureNames.indexOf( featureName ) );
	}


	@Override
	public double getLabel()
	{
		return Double.parseDouble(
				featureValues.get(
						featureNames.indexOf(
								segmentModel.getLabelFeatureName() ) ) );
	}

	@Override
	public int getTimePoint()
	{
		return Integer.parseInt(
				featureValues.get(
						featureNames.indexOf(
								segmentModel.getTimePointFeatureName() ) ) );
	}

	public ArrayList< String > getFeatureValues()
	{
		return featureValues;
	}
}
