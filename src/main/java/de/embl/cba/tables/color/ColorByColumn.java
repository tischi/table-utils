package de.embl.cba.tables.color;

import de.embl.cba.bdv.utils.lut.BlueWhiteRedARGBLut;
import de.embl.cba.bdv.utils.lut.GlasbeyARGBLut;
import de.embl.cba.tables.Logger;
import de.embl.cba.tables.Tables;
import de.embl.cba.tables.tablerow.TableRow;
import ij.gui.GenericDialog;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class ColorByColumn< T extends TableRow >
{

	public static final String LINEAR_BLUE_WHITE_RED = "Linear - Blue White Red";
	public static final String LINEAR_ZERO_BLACK_BLUE_WHITE_RED = "Linear - Black Blue White Red";
	public static final String RANDOM_GLASBEY = "Categorical - Glasbey";

	private final JTable table;
	private final SelectionColoringModel< T > selectionColoringModel;

	private String selectedColumnName;
	private String selectedColoringMode;

	private Map< String, double[] > columnNameToMinMax;
	private HashMap< String, double[] > columnNameToRangeSettings;

	public static final String[] COLORING_MODES = new String[]
			{
					LINEAR_BLUE_WHITE_RED,
					LINEAR_ZERO_BLACK_BLUE_WHITE_RED,
					RANDOM_GLASBEY
			};


	public ColorByColumn( JTable table,
						  SelectionColoringModel< T > selectionColoringModel )
	{
		this.table = table;
		this.selectionColoringModel = selectionColoringModel;

		this.columnNameToMinMax = new HashMap<>();
		this.columnNameToRangeSettings = new HashMap<>();
	}


	public void showDialog()
	{
		final String[] columnNames = Tables.getColumnNamesAsArray( table );

		final GenericDialog gd = new GenericDialog( "Color by Column" );

		if ( selectedColumnName == null ) selectedColumnName = columnNames[ 0 ];
		gd.addChoice( "Column", columnNames, selectedColumnName );

		if ( selectedColoringMode == null ) selectedColoringMode = COLORING_MODES[ 0 ];
		gd.addChoice( "Coloring Mode", COLORING_MODES, selectedColoringMode );

		gd.showDialog();
		if ( gd.wasCanceled() ) return;

		selectedColumnName = gd.getNextChoice();
		selectedColoringMode = gd.getNextChoice();

		colorByColumn( selectedColumnName, selectedColoringMode );

	}

	public ColoringModel< T > colorByColumn(
			String selectedColumnName,
			String selectedColoringMode )
	{

		switch ( selectedColoringMode )
		{
			case LINEAR_BLUE_WHITE_RED:
				return colorLinear(
						selectionColoringModel,
						selectedColumnName,
						false );
			case LINEAR_ZERO_BLACK_BLUE_WHITE_RED:
				return colorLinear(
						selectionColoringModel,
						selectedColumnName,
						true );
			case RANDOM_GLASBEY:
				return colorCategorical(
						selectionColoringModel,
						selectedColumnName );
		}
		return null;
	}

	private CategoryTableRowColumnColoringModel< T > colorCategorical(
			SelectionColoringModel< T > selectionColoringModel,
			String selectedColumnName )
	{
		final CategoryTableRowColumnColoringModel< T > coloringModel
				= new CategoryTableRowColumnColoringModel< >(
						selectedColumnName,
						new GlasbeyARGBLut( 255 ) );

		selectionColoringModel.setWrappedColoringModel( coloringModel );

		return coloringModel;
	}

	private NumericTableRowColumnColoringModel< T > colorLinear(
			SelectionColoringModel< T > selectionColoringModel,
			String selectedColumnName,
			boolean paintZeroBlack )
	{

		if ( ! Tables.isNumeric( table, selectedColumnName ) )
		{
			Logger.error( "Linear color mode is only available for numeric columns.\n" +
					"The selected " + selectedColumnName + " column however appears to be non-numeric.");
			return null;
		}

		final double[] valueRange = getValueRange( table, selectedColumnName );
		double[] valueSettings = getValueSettings( selectedColumnName, valueRange );

		final NumericTableRowColumnColoringModel< T > coloringModel
				= new NumericTableRowColumnColoringModel< >(
						selectedColumnName,
						new BlueWhiteRedARGBLut( 1000 ),
						valueSettings,
						valueRange,
						paintZeroBlack );

		selectionColoringModel.setWrappedColoringModel( coloringModel );

		SwingUtilities.invokeLater( () ->
				new NumericColoringModelDialog( selectedColumnName, coloringModel, valueRange ) );

		return coloringModel;
	}

	private double[] getValueSettings( String columnName, double[] valueRange )
	{
		double[] valueSettings;

		if ( columnNameToRangeSettings.containsKey( columnName ) )
			valueSettings = columnNameToRangeSettings.get( columnName );
		else
			valueSettings = valueRange.clone();

		columnNameToRangeSettings.put( columnName, valueSettings );

		return valueSettings;
	}

	private double[] getValueRange( JTable table, String column )
	{
		if ( ! columnNameToMinMax.containsKey( column ) )
		{
			final double[] minMaxValues = Tables.minMax( column, table );
			columnNameToMinMax.put( column, minMaxValues );
		}

		return columnNameToMinMax.get( column );
	}

}
