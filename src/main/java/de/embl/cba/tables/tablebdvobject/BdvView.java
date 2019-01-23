package de.embl.cba.tables.tablebdvobject;

import bdv.util.BdvFunctions;
import bdv.util.BdvOptions;

import java.util.Set;

public class BdvView< T extends LabelImageRegion > implements SelectionListener< T >
{
	private final SelectionModel< T > selection;
	private final SegmentationInstancesModel model;


	public BdvView( SegmentationInstancesModel model, final SelectionModel< T > selection )
	{
		this.model = model;
		this.selection = selection;

		show( model );

	}

	public void show( SegmentationInstancesModel model )
	{
		BdvOptions options = BdvOptions.options();

		if ( model.is2D() )
		{
			options = options.is2D();
		}

		BdvFunctions.show( model.getLabelSource(), options ).getBdvHandle();
	}


	@Override
	public void selectionChanged()
	{

	}

	@Override
	public void selectionAdded( T selection )
	{

	}

	@Override
	public void selectionRemoved( T selection )
	{

	}
}
