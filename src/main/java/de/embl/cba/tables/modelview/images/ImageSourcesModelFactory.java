package de.embl.cba.tables.modelview.images;

import de.embl.cba.tables.TableUtils;
import de.embl.cba.tables.modelview.segments.TableRowImageSegment;
import de.embl.cba.tables.modelview.segments.TableRow;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class ImageSourcesModelFactory< T extends TableRowImageSegment >
{
	public static final String PATH = "Path_";
	public static final String OBJECTS = "Objects_";

	private final ArrayList< T > tableRowImageSegments;
	private Set< String > columns;
	private final HashMap< String, String > imageNameToPathColumnName;
	private final int numSpatialDimensions;

	private FileImageSourcesModel imageSourcesModel;
	private String tablePath;

	public ImageSourcesModelFactory(
			ArrayList< T > tableRowImageSegments,
			String tablePath,
			int numSpatialDimensions )
	{
		this.tableRowImageSegments = tableRowImageSegments;
		this.numSpatialDimensions = numSpatialDimensions;
		this.tablePath = tablePath;

		columns = tableRowImageSegments.get( 0 ).cells().keySet();

		imageNameToPathColumnName = getImageNameToPathColumnName();

		imageSourcesModel = createImageSourcesModel();
	}

	public FileImageSourcesModel getImageSourcesModel()
	{
		return imageSourcesModel;
	}

	private FileImageSourcesModel createImageSourcesModel( )
	{
		imageSourcesModel = new FileImageSourcesModel();

		for ( TableRowImageSegment tableRowImageSegment : tableRowImageSegments )
		{
			final Set< String > imageNames = imageNameToPathColumnName.keySet();

			ArrayList< String > imageSetIds = getImageSetIds( tableRowImageSegment, imageNames );

			for ( String imageName : imageNames )
			{
				final String imagePath = getImagePath( tableRowImageSegment, imageName );

				final String imageId = imagePath;

				if ( ! imageSourcesModel.sources().containsKey( imageId ) )
				{
					final Metadata.Flavour imageFlavour = getImageFlavour( imageName );

					final Path absoluteImagePath = TableUtils.getAbsolutePath( tablePath, imagePath );

					final String imageDisplayName = absoluteImagePath.getFileName().toString();

					imageSourcesModel.addSource(
							imageId,
							imageDisplayName,
							absoluteImagePath.toFile(),
							imageSetIds,
							imageFlavour,
							numSpatialDimensions );
				}
			}
		}

		return imageSourcesModel;
	}

	private String getImagePath( TableRow tableRow, String imageName )
	{
		final String imagePathColumn = imageNameToPathColumnName.get( imageName );
		return tableRow.cells().get( imagePathColumn ).toString();
	}

	private ArrayList< String > getImageSetIds( TableRow tableRow, Set< String > imageNames )
	{
		ArrayList< String > imageSetIds = new ArrayList<>(  );

		for ( String imageName : imageNames )
		{
			imageSetIds.add( getImagePath( tableRow, imageName ) );
		}

		return imageSetIds;
	}

	private Metadata.Flavour getImageFlavour( String imageName )
	{
		final Metadata.Flavour flavour;

		if ( imageName.contains( OBJECTS ) )
		{
			flavour = Metadata.Flavour.LabelSource;
		}
		else
		{
			flavour = Metadata.Flavour.IntensitySource;
		}

		return flavour;
	}

	public HashMap< String, String > getImageNameToPathColumnName( )
	{
		final HashMap< String, String > imageNameToPathColumnName = new HashMap<>();
		for ( String column : columns )
		{
			if ( column.contains( PATH ) )
			{
				final String image = column.split( PATH )[ 1 ];
				imageNameToPathColumnName.put( image, column );
			}
		}
		return imageNameToPathColumnName;
	}

}
