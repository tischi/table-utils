package de.embl.cba.tables.modelview.coloring;

import de.embl.cba.tables.modelview.selection.Listeners;
import de.embl.cba.tables.modelview.selection.SelectionModel;
import net.imglib2.type.numeric.ARGBType;

public class SelectionColoringModel < T > implements ColoringModel< T >
{
	ColoringModel< T > coloringModel;
	SelectionModel< T > selectionModel;

	private SelectionMode selectionMode;
	private ARGBType selectionColor;
	private double brightnessNotSelected;

	public static final ARGBType YELLOW = new ARGBType( ARGBType.rgba( 255, 255, 0, 255 ) );

	public enum SelectionMode
	{
		DimNotSelected,
		OnlyShowSelected,
		SelectionColor,
		SelectionColorAndDimNotSelected;
	}

	public SelectionColoringModel(
			ColoringModel< T > coloringModel,
			SelectionModel< T > selectionModel )
	{
		this.coloringModel = coloringModel;
		this.selectionModel = selectionModel;

		this.selectionColor = YELLOW;
		this.brightnessNotSelected = 0.1;
	}

	@Override
	public Listeners< ColoringListener > listeners()
	{
		return null;
	}

	@Override
	public void convert( T input, ARGBType output )
	{
		coloringModel.convert( input, output );

		final boolean isSelected = selectionModel.isSelected( input );

		switch ( selectionMode )
		{
			case DimNotSelected:

				if ( !isSelected )
				{
					output.mul( brightnessNotSelected );
				}
				break;

			case OnlyShowSelected:

				if ( !isSelected )
				{
					output.mul( 0.0 );
				}
				break;

			case SelectionColor:

				if ( isSelected )
				{
					output.set( selectionColor );
				}
				break;

			case SelectionColorAndDimNotSelected:

				if ( isSelected )
				{
					output.set( selectionColor );
				}
				else
				{
					output.mul( brightnessNotSelected );
				}
				break;

			default:
				break;
		}

	}

	public void setSelectionMode( SelectionMode selectionMode )
	{
		this.selectionMode = selectionMode;

		switch ( selectionMode )
		{
			case DimNotSelected:
				brightnessNotSelected = 0.2;
				selectionColor = null;
				break;
			case OnlyShowSelected:
				brightnessNotSelected = 0.0;
				selectionColor = null;
				break;
			case SelectionColor:
				brightnessNotSelected = 1.0;
				break;
			case SelectionColorAndDimNotSelected:
				brightnessNotSelected = 0.2;
				break;
		}

	}

	public void setSelectionColor( ARGBType selectionColor )
	{
		this.selectionColor = selectionColor;
	}

	public void setWrappedColoringModel( ColoringModel< T > coloringModel )
	{
		this.coloringModel = coloringModel;
	}

	public ColoringModel< T > getWrappedColoringModel()
	{
		return coloringModel;
	}
}
