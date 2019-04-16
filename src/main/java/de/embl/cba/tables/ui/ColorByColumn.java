package de.embl.cba.tables.ui;

import de.embl.cba.bdv.utils.lut.BlueWhiteRedARGBLut;
import de.embl.cba.bdv.utils.lut.GlasbeyARGBLut;
import de.embl.cba.tables.Logger;
import de.embl.cba.tables.Tables;
import de.embl.cba.tables.modelview.coloring.CategoryTableRowColumnColoringModel;
import de.embl.cba.tables.modelview.coloring.NumericColoringModelDialog;
import de.embl.cba.tables.modelview.coloring.NumericTableRowColumnColoringModel;
import de.embl.cba.tables.modelview.coloring.SelectionColoringModel;
import de.embl.cba.tables.modelview.segments.TableRow;
import ij.gui.GenericDialog;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class ColorByColumn< T extends TableRow >
{

	public static final String LINEAR_BLUE_WHITE_RED = "Linear - Blue White Red";
	public static final String RANDOM_GLASBEY = "Categorical - Glasbey";

	private final JTable table;
	private final SelectionColoringModel< T > selectionColoringModel;

	private String selectedColumnName;
	private String selectedColoringMode;

	private Map< String, double[] > columnNameToMinMax;
	private HashMap< String, double[] > columnNameToRangeSettings;


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
		final String[] coloringModes = new String[]
				{
						LINEAR_BLUE_WHITE_RED,
						RANDOM_GLASBEY
				};

		final GenericDialog gd = new GenericDialog( "Color by Column" );

		if ( selectedColumnName == null ) selectedColumnName = columnNames[ 0 ];
		gd.addChoice( "Column", columnNames, selectedColumnName );

		if ( selectedColoringMode == null ) selectedColoringMode = coloringModes[ 0 ];
		gd.addChoice( "Coloring Mode", coloringModes, selectedColoringMode );

		gd.showDialog();
		if ( gd.wasCanceled() ) return;

		selectedColumnName = gd.getNextChoice();
		selectedColoringMode = gd.getNextChoice();

		colorByColumn( selectedColumnName, selectedColoringMode );

	}

	public void colorByColumn( String selectedColumnName,
							   String selectedColoringMode )
	{


		switch ( selectedColoringMode )
		{
			case LINEAR_BLUE_WHITE_RED:
				colorLinear( selectionColoringModel,
						selectedColumnName );
				break;
			case RANDOM_GLASBEY:
				colorCategorical( selectionColoringModel,
						selectedColumnName );
				break;
		}
	}

	private void colorCategorical(
			SelectionColoringModel< T > selectionColoringModel,
			String selectedColumnName )
	{
		final CategoryTableRowColumnColoringModel< T > coloringModel
				= new CategoryTableRowColumnColoringModel< >(
				selectedColumnName,
				new GlasbeyARGBLut( 255 ) );

		selectionColoringModel.setWrappedColoringModel( coloringModel );
	}

	private void colorLinear(
			SelectionColoringModel< T > selectionColoringModel,
			String selectedColumnName )
	{

		if ( ! Tables.isNumeric( table, selectedColumnName ) )
		{
			Logger.error( "Linear coloring mode is only available for numeric columns.\n" +
					"The selected " + selectedColumnName + " column however appears to be non-numeric.");
			return;
		}

		final double[] valueRange = getValueRange( table, selectedColumnName );
		double[] valueSettings = getValueSettings( selectedColumnName, valueRange );


		final NumericTableRowColumnColoringModel< T > coloringModel
				= new NumericTableRowColumnColoringModel< >(
						selectedColumnName,
						new BlueWhiteRedARGBLut( 1000 ),
						valueSettings,
						valueRange
		);

		selectionColoringModel.setWrappedColoringModel( coloringModel );

		SwingUtilities.invokeLater( () ->
				new NumericColoringModelDialog( selectedColumnName, coloringModel, valueRange ) );
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
