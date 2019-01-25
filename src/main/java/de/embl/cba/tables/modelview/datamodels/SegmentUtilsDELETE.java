package de.embl.cba.tables.modelview.datamodels;

public class SegmentUtilsDELETE
{

	public static String getKey( Double label )
	{
		return getKey( label, 0 );
	}

	public static String getKey( Double label, Integer timePoint )
	{
		return "L"+label.toString() + "_T" + timePoint.toString();
	}
}
