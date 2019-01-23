package de.embl.cba.tables.cellprofiler;

import java.util.ArrayList;

public class CellProfilerDataset
{
	// TODO: make enum
	public static final String OBJECT_LABEL_MASK = "Object Label Mask";
	public static final String INTENSITY_IMAGE = "Intensity Image";

	final private Object datasetIndex;
	final private ArrayList< ImageTypeAndPath > images;

	public CellProfilerDataset( Object datasetIndex )
	{
		this.datasetIndex = datasetIndex;
		images = new ArrayList<>();
	}

	public void addImagePath( String type, String path )
	{
		images.add( new ImageTypeAndPath( type, path ) );
	}

	public ArrayList< ImageTypeAndPath > getImages()
	{
		return images;
	}

	public Object getDatasetIndex()
	{
		return datasetIndex;
	}

	class ImageTypeAndPath
	{
		String type;
		String path;

		public ImageTypeAndPath( String type, String path )
		{
			this.type = type;
			this.path = path;
		}
	}
}
