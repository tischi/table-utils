package de.embl.cba.tables.modelview.coloring;

import de.embl.cba.tables.modelview.objects.DefaultAnnotatedSegment;
import de.embl.cba.tables.modelview.selection.Listeners;
import net.imglib2.type.numeric.ARGBType;

public interface AnnotatedSegmentsColoringModel extends ColoringModel< DefaultAnnotatedSegment >
{
	void setColoringFeature( String coloringFeature );

	@Override
	Listeners< ColoringListener > listeners();

	@Override
	void convert( DefaultAnnotatedSegment segment, ARGBType color );
}
