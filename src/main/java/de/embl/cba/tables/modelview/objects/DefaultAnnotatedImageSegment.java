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
	public String imageSetId()
	{
		return imageSegment.imageSetId();
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
	public double[] position()
	{
		return imageSegment.position();
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
}
