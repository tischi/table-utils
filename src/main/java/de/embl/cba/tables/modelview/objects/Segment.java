package de.embl.cba.tables.modelview.objects;

import java.util.ArrayList;

public class Segment implements FeatureList, ImageSegment
{

	private final double label;
	private final int timePoint;
	private final ArrayList< String > featureNames;
	private final ArrayList< String > featureValues;

	public Segment( double label,
					int timePoint,
					ArrayList< String > featureNames,
					ArrayList< String > featureValues )
	{
		this.label = label;
		this.timePoint = timePoint;
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
		return label;
	};

	@Override
	public int getTimePoint()
	{
		return timePoint;
	};



}
