package de.embl.cba.tables.cellprofiler;

import java.util.ArrayList;

public class CellProfilerDataset
{
	final private Object datasetIndex;
	final private ArrayList< String > imagePaths;

	public CellProfilerDataset( Object datasetIndex )
	{
		this.datasetIndex = datasetIndex;
		imagePaths = new ArrayList<String>();
	}

	public void addImagePath( String path )
	{
		imagePaths.add( path );
	}

	public ArrayList< String > getImagePaths()
	{
		return imagePaths;
	}

	public Object getDatasetIndex()
	{
		return datasetIndex;
	}


}
