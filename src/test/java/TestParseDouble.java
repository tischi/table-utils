public class TestParseDouble
{
	public static void main( String[] args )
	{
		String s = "\"1\"";
		final double v = Double.parseDouble( s );
		System.out.println( v );
	}
}
