package de.embl.cba.tables.modelview.images;

import bdv.viewer.Source;

public class SourceAndMetadata< T >
{
	private final Source< T > source;
	private final Metadata metadata;

	public SourceAndMetadata( Source< T > source, Metadata metadata )
	{
		this.source = source;
		this.metadata = metadata;
	}

	public Source< T > source()
	{
		return source;
	}

	public Metadata metadata()
	{
		return metadata;
	}
}
