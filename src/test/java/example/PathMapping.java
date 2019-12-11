package example;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathMapping
{
	public static void main( String[] args )
	{
		test01();
		test02();
	}

	private static void test01()
	{
		final Path rootPath = Paths.get( "/Volumes/model/model.txt");

		final String relativeImagePath = "../image/image.tif";

		final Path path = Paths.get( rootPath.toString(), relativeImagePath.toString() );

		final Path normalize = path.normalize();

		System.out.println( normalize );
	}

	private static void test02()
	{
		final Path rootPath = Paths.get( "" );

		final String relativeImagePath = "/g/image/image.tif";

		final Path path = Paths.get( rootPath.toString(), relativeImagePath.toString() );

		final Path normalize = path.normalize();

		System.out.println( normalize );
	}
}
