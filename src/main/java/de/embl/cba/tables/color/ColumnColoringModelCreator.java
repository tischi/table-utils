package de.embl.cba.tables.color;

import de.embl.cba.bdv.utils.lut.*;
import de.embl.cba.tables.Logger;
import de.embl.cba.tables.Tables;
import de.embl.cba.tables.tablerow.TableRow;
import ij.gui.GenericDialog;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class ColumnColoringModelCreator< T extends TableRow >
{
	public static final String LINEAR_RED = "Linear - Red";
	public static final String LINEAR_BLUE_WHITE_RED = "Linear - Blue White Red";
	public static final String LINEAR_BLUE_WHITE_RED_ZERO_TRANSPARENT = "Linear - Blue White Red (0 transparent)";
	public static final String LINEAR_VIRIDIS = "Linear - Viridis";
	public static final String CATEGORICAL_GLASBEY = "Categorical - Glasbey";
	public static final String CATEGORICAL_GLASBEY_ZERO_TRANSPARENT = "Categorical - Glasbey (0 transparent)";

	private final JTable table;

	private String selectedColumnName;
	private String selectedColoringMode;

	private Map< String, double[] > columnNameToMinMax;
	private HashMap< String, double[] > columnNameToRangeSettings;

	public static final String[] COLORING_MODES = new String[]
			{
					LINEAR_BLUE_WHITE_RED,
					LINEAR_BLUE_WHITE_RED_ZERO_TRANSPARENT,
					LINEAR_VIRIDIS,
					CATEGORICAL_GLASBEY,
					CATEGORICAL_GLASBEY_ZERO_TRANSPARENT
			};


	public ColumnColoringModelCreator( JTable table )
	{
		this.table = table;

		this.columnNameToMinMax = new HashMap<>();
		this.columnNameToRangeSettings = new HashMap<>();
	}

	public ColoringModel< T > showDialog()
	{
		final String[] columnNames = Tables.getColumnNamesAsArray( table );

		final GenericDialog gd = new GenericDialog( "Color by Column" );

		if ( selectedColumnName == null ) selectedColumnName = columnNames[ 0 ];
		gd.addChoice( "Column", columnNames, selectedColumnName );

		if ( selectedColoringMode == null ) selectedColoringMode = COLORING_MODES[ 0 ];
		gd.addChoice( "Coloring Mode", COLORING_MODES, selectedColoringMode );

		gd.showDialog();
		if ( gd.wasCanceled() ) return null;

		selectedColumnName = gd.getNextChoice();
		selectedColoringMode = gd.getNextChoice();

		return createColoringModel( selectedColumnName, selectedColoringMode );
	}

	public ColoringModel< T > createColoringModel(
			String selectedColumnName,
			String selectedColoringMode )
	{
		switch ( selectedColoringMode )
		{
			case LINEAR_RED:
				return createLinearColoringModel(
						selectedColumnName,
						false,
						new SingleColorARGBLut( 255, 0,0 ) );
			case LINEAR_BLUE_WHITE_RED:
				return createLinearColoringModel(
						selectedColumnName,
						false,
						new BlueWhiteRedARGBLut( 1000 ) );
			case LINEAR_BLUE_WHITE_RED_ZERO_TRANSPARENT:
				return createLinearColoringModel(
						selectedColumnName,
						true,
						new BlueWhiteRedARGBLut( 1000 ) );
			case LINEAR_VIRIDIS:
				return createLinearColoringModel(
						selectedColumnName,
						true,
						new ViridisARGBLut( ) );
			case CATEGORICAL_GLASBEY:
				return createCategoricalColoringModel(
						selectedColumnName, false );
			case CATEGORICAL_GLASBEY_ZERO_TRANSPARENT:
				return createCategoricalColoringModel(
						selectedColumnName, true );



		}
		return null;
	}

	public CategoryTableRowColumnColoringModel< T > createCategoricalColoringModel( String selectedColumnName, boolean isZeroTransparent )
	{
		final CategoryTableRowColumnColoringModel< T > coloringModel
				= new CategoryTableRowColumnColoringModel< >(
						selectedColumnName,
						new GlasbeyARGBLut( 255 ) );

		if ( isZeroTransparent )
		{
			coloringModel.putInputToFixedColor( "0", CategoryTableRowColumnColoringModel.TRANSPARENT );
			coloringModel.putInputToFixedColor( "0.0", CategoryTableRowColumnColoringModel.TRANSPARENT );
		}

		return coloringModel;
	}

	private NumericTableRowColumnColoringModel< T > createLinearColoringModel(
			String selectedColumnName,
			boolean isZeroTransparent,
			ARGBLut argbLut )
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
				= new NumericTableRowColumnColoringModel(
						selectedColumnName,
						argbLut,
						valueSettings,
						valueRange,
						isZeroTransparent );

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
