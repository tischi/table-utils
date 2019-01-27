package de.embl.cba.tables.modelview.objects;

import net.imglib2.FinalInterval;

import java.util.LinkedHashMap;

public class DefaultAnnotatedImageSegment implements AnnotatedImageSegment
{
	private final TableRow tableRow;
	private final ImageSegment imageSegment;

	public DefaultAnnotatedImageSegment( ImageSegment imageSegment,
										 TableRow tableRow )
	{
		this.imageSegment = imageSegment;
		this.tableRow = tableRow;
	}

	@Override
	public String imageSetName()
	{
		return imageSegment.imageSetName();
	}

	@Override
	public double label()
	{
		return imageSegment.label();
	}

	@Override
	public int timePoint()
	{
		return imageSegment.timePoint();
	}

	@Override
	public FinalInterval boundingBox()
	{
		return imageSegment.boundingBox();
	}

	@Override
	public LinkedHashMap< String, Object > cells()
	{
		return tableRow.cells();
	}

	@Override
	public int rowIndex()
	{
		return tableRow.rowIndex();
	}

	@Override
	public void localize( float[] position )
	{
		imageSegment.localize( position );
	}

	@Override
	public void localize( double[] position )
	{
		imageSegment.localize( position );
	}

	@Override
	public float getFloatPosition( int d )
	{
		return imageSegment.getFloatPosition( d );
	}

	@Override
	public double getDoublePosition( int d )
	{
		return imageSegment.getDoublePosition( d );
	}

	@Override
	public int numDimensions()
	{
		return imageSegment.numDimensions();
	}
}
