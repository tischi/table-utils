package de.embl.cba.tables.color;

import de.embl.cba.bdv.utils.lut.ARGBLut;
import de.embl.cba.bdv.utils.lut.BlueWhiteRedARGBLut;
import de.embl.cba.bdv.utils.lut.GlasbeyARGBLut;
import de.embl.cba.bdv.utils.lut.SingleColorARGBLut;
import de.embl.cba.tables.Logger;
import de.embl.cba.tables.Tables;
import de.embl.cba.tables.tablerow.TableRow;
import ij.gui.GenericDialog;
import net.imglib2.type.numeric.ARGBType;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class ColumnBasedColoring< T extends TableRow >
{
	public static final String LINEAR_RED = "Linear - Red";
	public static final String LINEAR_BLUE_WHITE_RED = "Linear - Blue White Red";
	public static final String LINEAR_ZERO_BLACK_BLUE_WHITE_RED = "Linear - Transparent Blue White Red";
	public static final String RANDOM_GLASBEY = "Categorical - Glasbey";

	private final JTable table;
	private final SelectionColoringModel< T > coloringModel; // TODO

	private String selectedColumnName;
	private String selectedColoringMode;

	private Map< String, double[] > columnNameToMinMax;
	private HashMap< String, double[] > columnNameToRangeSettings;

	public static final String[] COLORING_MODES = new String[]
			{
					LINEAR_RED,
					LINEAR_BLUE_WHITE_RED,
					LINEAR_ZERO_BLACK_BLUE_WHITE_RED,
					RANDOM_GLASBEY
			};


	public ColumnBasedColoring( JTable table,
								SelectionColoringModel< T > coloringModel )
	{
		this.table = table;
		this.coloringModel = coloringModel;

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

		getColumnBasedColoringModel( selectedColumnName, selectedColoringMode );
	}

	public ColoringModel< T > getColumnBasedColoringModel(
			String selectedColumnName,
			String selectedColoringMode )
	{
		switch ( selectedColoringMode )
		{
			case LINEAR_RED:
				return getLinearColoringModel(
						coloringModel,
						selectedColumnName,
						false,
						new SingleColorARGBLut( 255, 0,0 ) );
			case LINEAR_BLUE_WHITE_RED:
				return getLinearColoringModel(
						coloringModel,
						selectedColumnName,
						false,
						new BlueWhiteRedARGBLut( 1000 ) );
			case LINEAR_ZERO_BLACK_BLUE_WHITE_RED:
				return getLinearColoringModel(
						coloringModel,
						selectedColumnName,
						true,
						new BlueWhiteRedARGBLut( 1000 ) );
			case RANDOM_GLASBEY:
				return getCategoricalColoringModel(
						coloringModel,
						selectedColumnName );
		}
		return null;
	}

	public CategoryTableRowColumnColoringModel< T > getCategoricalColoringModel( String selectedColumnName )
	{
		return getCategoricalColoringModel( coloringModel, selectedColumnName );
	}

	private CategoryTableRowColumnColoringModel< T > getCategoricalColoringModel(
			SelectionColoringModel< T > selectionColoringModel,
			String selectedColumnName )
	{
		final CategoryTableRowColumnColoringModel< T > coloringModel
				= new CategoryTableRowColumnColoringModel< >(
						selectedColumnName,
						new GlasbeyARGBLut( 255 ) );

		selectionColoringModel.setColoringModel( coloringModel );

		return coloringModel;
	}

	private NumericTableRowColumnColoringModel< T > getLinearColoringModel(
			SelectionColoringModel< T > selectionColoringModel,
			String selectedColumnName,
			boolean paintZeroTransparent,
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
						paintZeroTransparent );

		selectionColoringModel.setColoringModel( coloringModel );

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

	public String getSelectedColumnName()
	{
		return selectedColumnName;
	}
}
