package de.embl.cba.tables.modelview.datamodels;

public class SegmentUtils
{

	public static Object getKey( Double label )
	{
		return getKey( label, 0 );
	}

	public static Object getKey( Double label, Integer timePoint )
	{
		return "L"+label.toString() + "_T" + timePoint.toString();
	}
}
