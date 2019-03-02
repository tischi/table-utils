package de.embl.cba.tables.modelview.images;

import bdv.viewer.Source;
import net.imglib2.type.numeric.RealType;

public class SourceAndMetadata< R extends RealType< R > >
{
	private final Source< R > source;
	private final SourceMetadata metadata;

	public SourceAndMetadata( Source< R > source, SourceMetadata metadata )
	{
		this.source = source;
		this.metadata = metadata;
	}

	public Source< R > source()
	{
		return source;
	}

	public SourceMetadata metadata()
	{
		return metadata;
	}


}
