package de.embl.cba.tables.modelview.datamodels;

import bdv.viewer.Source;
import net.imglib2.type.numeric.RealType;

public class LabelImageSourceModel // implements imageSource??
{
	private final Source< ? extends RealType< ? > > labelSource;
	private final boolean is2D;

	public LabelImageSourceModel( Source< ? extends RealType< ? > > labelSource, boolean is2D )
	{
		this.labelSource = labelSource;
		this.is2D = is2D;
	}

	public Source< ? extends RealType< ? > > getSource()
	{
		return labelSource;
	}

	public boolean is2D()
	{
		return is2D;
	}
}
