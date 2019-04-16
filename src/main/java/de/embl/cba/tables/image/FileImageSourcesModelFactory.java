package de.embl.cba.tables.image;

import de.embl.cba.tables.FileUtils;
import de.embl.cba.tables.Tables;
import de.embl.cba.tables.tablerow.TableRowImageSegment;
import de.embl.cba.tables.tablerow.TableRow;

import java.nio.file.Path;
import java.util.*;


public class FileImageSourcesModelFactory< T extends TableRowImageSegment >
{
	public static final String PATH_COLUMN_ID = "Path_";
	public ArrayList< String > labelMaskColumnIds;

	private final List< T > tableRowImageSegments;
	private Set< String > columns;
	private final Map< String, String > imageNameToPathColumnName;
	private FileImageSourcesModel imageSourcesModel;
	private final String imageRootFolder;
	private final boolean is2D;

	public FileImageSourcesModelFactory(
			final List< T > tableRowImageSegments,
			final String imageRootFolder,
			boolean is2D )
	{
		this.tableRowImageSegments = tableRowImageSegments;
		this.imageRootFolder = imageRootFolder;
		this.is2D = is2D;

		columns = tableRowImageSegments.get( 0 ).cells().keySet();

		createLabelMaskIds(); // TODO: how to handle this? could be anything...

		imageNameToPathColumnName = getImageNameToPathColumnName();
		imageSourcesModel = createImageSourcesModel();
	}

	public void createLabelMaskIds()
	{
		labelMaskColumnIds = new ArrayList< >();
		labelMaskColumnIds.add( "Objects_" );
		labelMaskColumnIds.add( "labelMasks" );
		labelMaskColumnIds.add( "LabelMask" );
		labelMaskColumnIds.add( "LabelImage" );
	}

	public FileImageSourcesModel getImageSourcesModel()
	{
		return imageSourcesModel;
	}

	private FileImageSourcesModel createImageSourcesModel( )
	{
		imageSourcesModel = new FileImageSourcesModel( is2D );

		for ( TableRowImageSegment tableRowImageSegment : tableRowImageSegments )
		{
			final Set< String > imageNames = imageNameToPathColumnName.keySet();

			final List< String > imageSetIds = getImageSetIds( tableRowImageSegment, imageNames );

			for ( String imageName : imageNames )
			{
				final String imagePath = getImagePath( tableRowImageSegment, imageName );

				final String imageId = imagePath;

				if ( ! imageSourcesModel.sources().containsKey( imageId ) )
				{
					final Path absoluteImagePath =
							Tables.getAbsolutePath( imageRootFolder, imagePath );

					final String imageDisplayName = absoluteImagePath.getFileName().toString();

					imageSourcesModel.addSourceAndMetadata(
							imageId,
							imageDisplayName,
							absoluteImagePath.toFile(),
							imageSetIds,
							getImageFlavour( imageName ) );
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

	private List< String > getImageSetIds( TableRow tableRow, Set< String > imageNames )
	{
		ArrayList< String > imageSetIds = new ArrayList<>(  );

		for ( String imageName : imageNames )
		{
			imageSetIds.add( getImagePath( tableRow, imageName ) );
		}

		return imageSetIds;
	}

	private SourceMetadata.Flavour getImageFlavour( String imageName )
	{
		final SourceMetadata.Flavour flavour;

		if ( FileUtils.stringContainsItemFromList( imageName, labelMaskColumnIds ) )
		{
			flavour = SourceMetadata.Flavour.LabelSource;
		}
		else
		{
			flavour = SourceMetadata.Flavour.IntensitySource;
		}

		return flavour;
	}

	private Map< String, String > getImageNameToPathColumnName( )
	{
		final HashMap< String, String > imageNameToPathColumnName = new HashMap<>();
		for ( String column : columns )
		{
			if ( column.contains( PATH_COLUMN_ID ) )
			{
				final String image = column.split( PATH_COLUMN_ID )[ 1 ];
				imageNameToPathColumnName.put( image, column );
			}
		}
		return imageNameToPathColumnName;
	}


}
