package playground;

import de.embl.cba.tables.Utils;

public class TestParseDouble
{
	public static void main( String[] args )
	{
		String s = "\"1\"";
		final double v = Utils.parseDouble( s );
		System.out.println( v );
	}
}
