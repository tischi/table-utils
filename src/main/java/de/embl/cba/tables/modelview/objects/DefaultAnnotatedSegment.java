package de.embl.cba.tables.modelview.objects;

import de.embl.cba.bdv.utils.selection.Segment;
import net.imglib2.FinalInterval;

import java.util.ArrayList;

public class DefaultAnnotatedSegment implements AnnotatedSegment
{
	private final ArrayList< String > featureNames;
	private final Object[] featureValues;
	private final Segment segment;

	public DefaultAnnotatedSegment( Segment segment,
									ArrayList< String > featureNames,
									Object[] featureValues )
	{
		this.segment = segment;
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
		return featureValues[ featureNames.indexOf( featureName ) ];
	}

	@Override
	public Object[] getFeatureValues()
	{
		return featureValues;
	}

	@Override
	public String getImageId()
	{
		return segment.getImageId();
	}

	@Override
	public double getLabel()
	{
		return segment.getLabel();
	}

	@Override
	public int getTimePoint()
	{
		return segment.getTimePoint();
	}

	@Override
	public double[] getPosition()
	{
		return segment.getPosition();
	}

	@Override
	public FinalInterval getBoundingBox()
	{
		return segment.getBoundingBox();
	}



}
