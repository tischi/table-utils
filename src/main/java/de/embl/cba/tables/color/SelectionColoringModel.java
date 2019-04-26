package de.embl.cba.tables.color;

import de.embl.cba.tables.select.SelectionModel;
import net.imglib2.type.numeric.ARGBType;

import java.util.Arrays;
import java.util.List;

import static de.embl.cba.tables.color.SelectionColoringModel.SelectionMode.OnlyShowSelected;
import static de.embl.cba.tables.color.SelectionColoringModel.SelectionMode.SelectionColor;

public class SelectionColoringModel < T > extends AbstractColoringModel< T >
{
	ColoringModel< T > wrappedColoringModel;
	SelectionModel< T > selectionModel;

	private SelectionMode selectionMode;
	private ARGBType selectionColor;
	private double brightnessNotSelected;

	public static final ARGBType YELLOW =
			new ARGBType( ARGBType.rgba( 255, 255, 0, 255 ) );
	private final List< SelectionMode > selectionModes;

	public enum SelectionMode
	{
		OnlyShowSelected,
		SelectionColor,
		SelectionColorAndDimNotSelected,
		DimNotSelected
	}

	public SelectionColoringModel(
			ColoringModel< T > wrappedColoringModel,
			SelectionModel< T > selectionModel )
	{
		setWrappedColoringModel( wrappedColoringModel );
		this.selectionModel = selectionModel;
		this.selectionModes = Arrays.asList( SelectionColoringModel.SelectionMode.values() );

		this.selectionColor = YELLOW;
		this.brightnessNotSelected = 0.1;
		this.selectionMode = SelectionMode.DimNotSelected;
	}


	@Override
	public void convert( T input, ARGBType output )
	{
		wrappedColoringModel.convert( input, output );

		if ( selectionModel.isEmpty() ) return;

		final boolean isSelected = selectionModel.isSelected( input );

		switch ( selectionMode )
		{
			case DimNotSelected:

				if ( ! isSelected )
					output.mul( brightnessNotSelected );
				break;

			case OnlyShowSelected:

				if ( ! isSelected )
					output.mul( 0.0 );
				break;

			case SelectionColor:

				if ( isSelected )
					output.set( selectionColor );
				break;

			case SelectionColorAndDimNotSelected:

				if ( isSelected )
					output.set( selectionColor );
				else
					output.mul( brightnessNotSelected );
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
				selectionColor = YELLOW;
				break;
			case SelectionColorAndDimNotSelected:
				brightnessNotSelected = 0.2;
				selectionColor = YELLOW;
				break;
		}
		notifyColoringListeners();
	}

	public SelectionMode getSelectionMode()
	{
		return selectionMode;
	}

	public void setSelectionColor( ARGBType selectionColor )
	{
		this.selectionColor = selectionColor;
		notifyColoringListeners();
	}

	public void setWrappedColoringModel( ColoringModel< T > wrappedColoringModel )
	{
		this.wrappedColoringModel = wrappedColoringModel;
		notifyColoringListeners();

		// chain event notification
		wrappedColoringModel.listeners().add( () -> SelectionColoringModel.this.notifyColoringListeners() );

	}

	public ColoringModel< T > getWrappedColoringModel()
	{
		return wrappedColoringModel;
	}

	public void iterateSelectionMode()
	{
		final int selectionModeIndex = selectionModes.indexOf( selectionMode );

		if ( selectionModeIndex < selectionModes.size() - 1 )
		{
			setSelectionMode( selectionModes.get( selectionModeIndex + 1 ) );
		}
		else
		{
			setSelectionMode( selectionModes.get( 0 ) );
		}
	}
}
