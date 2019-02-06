import java.nio.file.Path;
import java.nio.file.Paths;

public class PathMapping
{
	public static void main( String[] args )
	{
		final Path tablePath = Paths.get( "/Volumes/tables/table.txt");

		final String relativeImagePath = "../images/image.tif";

		final Path path = Paths.get( tablePath.toString(), relativeImagePath.toString() );

		final Path normalize = path.normalize();

		int a = 1;
	}
}
