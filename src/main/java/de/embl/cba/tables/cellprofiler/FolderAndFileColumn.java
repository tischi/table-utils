package de.embl.cba.tables.cellprofiler;

public class FolderAndFileColumn
{
	final String folderColumn;
	final String fileColumn;

	public FolderAndFileColumn( String folderColumn, String fileColumn )
	{
		this.folderColumn = folderColumn;
		this.fileColumn = fileColumn;
	}


	public String getFileColumn()
	{
		return fileColumn;
	}

	public String getFolderColumn()
	{
		return folderColumn;
	}

}
