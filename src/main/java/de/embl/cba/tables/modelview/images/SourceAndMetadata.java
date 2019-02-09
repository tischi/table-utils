package de.embl.cba.tables.modelview.images;

import bdv.viewer.Source;

public class SourceAndMetadata< T >
{
	private final Source< T > source;
	private final SourceMetadata metadata;

	public SourceAndMetadata( Source< T > source, SourceMetadata metadata )
	{
		this.source = source;
		this.metadata = metadata;
	}

	public Source< T > source()
	{
		return source;
	}

	public SourceMetadata metadata()
	{
		return metadata;
	}
}
