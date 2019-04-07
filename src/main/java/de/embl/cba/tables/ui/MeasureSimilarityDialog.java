package de.embl.cba.tables.ui;

import de.embl.cba.tables.TableUtils;
import de.embl.cba.tables.measure.SummaryStatistics;
import de.embl.cba.tables.modelview.segments.TableRow;
import ij.IJ;
import ij.gui.GenericDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MeasureSimilarityDialog< T extends TableRow >
{

	public static final String L1_NORM = "L1 Norm";
	public static final String L2_NORM = "L2 Norm";


	private Map< String, SummaryStatistics > columnNameToSummaryStatistics;
	private String selectedMetric;

	public MeasureSimilarityDialog( )
	{
		this.columnNameToSummaryStatistics = new LinkedHashMap<>();
	}


	public void showDialog( JTable table, Set< T > selectedRows )
	{

		final String[] metrics = new String[]
				{
						L2_NORM
				};

		final GenericDialog gd = new GenericDialog( "Measure Similarity" );
		gd.addStringField( "Column RegExp", ".*", 20 );
		if ( selectedMetric == null ) selectedMetric = metrics[ 0 ];
		gd.addChoice( "Metric", metrics, selectedMetric );
		gd.addStringField( "New Column Name", "Similarity", 20 );

		gd.showDialog();
		if ( gd.wasCanceled() ) return;

		final String columnNameRegExp = gd.getNextString();
		selectedMetric = gd.getNextChoice();
		final String newColumnName = gd.getNextString();

		final ArrayList< String > selectedColumnNames =
				getSelectedColumnNames( table, columnNameRegExp );

		final int n = selectedColumnNames.size();
		final int[] selectedColumnIndices = new int[ n ];

		double[] means = new double[ n ];
		double[] sigmas = new double[ n ];

		for ( int i = 0; i < n; ++i )
		{
			selectedColumnIndices[ i ] =
					table.getColumnModel().getColumnIndex( selectedColumnNames.get( i ) );

			final SummaryStatistics summaryStatistics =
					getSummaryStatistics( table, selectedColumnNames.get( i ) );

			means[ i ] = summaryStatistics.mean;
			sigmas[ i ] = summaryStatistics.sigma;
		}


		final ArrayList< double[] > normVectors = new ArrayList<>();
		for ( T tableRow : selectedRows )
		{
			final double[] normVector = getNormalisedRowVector(
					table, tableRow.rowIndex(), selectedColumnIndices, means, sigmas );

			normVectors.add( normVector );
		}

		final double[] avgNormVector = computeAverageVector( normVectors );


		final Double[] distances =
				computDistances( table, selectedColumnIndices, means, sigmas, avgNormVector );

		( ( DefaultTableModel ) table.getModel() ).addColumn( newColumnName , distances );



	}

	public double[] computeAverageVector( ArrayList< double[] > normVectors )
	{
		int n = normVectors.size();
		final double[] avgNormVector = new double[ n ];
		for( double[] v : normVectors )
			for ( int i = 0; i < n; ++i )
				avgNormVector[ i ] += v[ i ];

		for ( int i = 0; i < n; ++i )
			avgNormVector[ i ] /= n;
		return avgNormVector;
	}

	public Double[] computDistances( JTable table, int[] selectedColumnIndices, double[] means, double[] sigmas, double[] avgNormVector )
	{
		final Double[] distances = new Double[ table.getRowCount() ];

		for ( int rowIndex = 0; rowIndex < table.getRowCount(); ++rowIndex )
		{
			final double[] rowVector = getNormalisedRowVector(
					table, rowIndex, selectedColumnIndices, means, sigmas );

			double distance = getDistance( rowVector, avgNormVector );

			distances[ rowIndex ] = distance;
		}
		return distances;
	}

	public double getDistance( double[] rowVector, double[] avgNormVector )
	{
		int n = rowVector.length ;
		double distance = 0.0;
		for ( int i = 0; i < n; ++i )
			distance += Math.pow( rowVector[ i ] - avgNormVector[ i ], 2 );
		distance = Math.sqrt( distance );
		return distance;
	}

	public double[] getNormalisedRowVector(
			final JTable table,
			final int rowIndex,
			final int[] selectedColumnIndices,
			final double[] means,
			final double[] sigmas )
	{
		int n = selectedColumnIndices.length;

		final double[] rawVector = new double[ n ];
		for ( int i = 0; i < n; ++i )
			rawVector[ i ] = ( Double ) table
					.getValueAt( rowIndex, selectedColumnIndices[ i ] );

		final double[] normVector = new double[ n ];
		for ( int i = 0; i < n; ++i )
			normVector[ i ] =  ( rawVector[ i ] - means[ i ] ) / sigmas[ i ];

		return normVector;
	}

	private SummaryStatistics getSummaryStatistics( JTable table, String columnName )
	{
		if ( ! columnNameToSummaryStatistics.containsKey( columnName ) )
		{
			final double[] meanSigma = TableUtils.meanSigma( columnName, table );
			final SummaryStatistics summaryStatistics = new SummaryStatistics(
					meanSigma[ 0 ], meanSigma[ 1 ]
			);

			columnNameToSummaryStatistics.put( columnName, summaryStatistics );
		}

		IJ.log( columnName + ": " + columnNameToSummaryStatistics.get( columnName ) );

		return columnNameToSummaryStatistics.get( columnName );
	}

	public ArrayList< String > getSelectedColumnNames( JTable table, String columnNameRegExp )
	{
		final List< String > columnNames = TableUtils.getColumnNames( table );

		final ArrayList< String > selectedColumnNames = new ArrayList<>();
		for ( String columnName : columnNames )
		{
			if ( TableUtils.isNumeric( table, columnName ) )
			{
				final Matcher matcher = Pattern.compile( columnNameRegExp ).matcher( columnName );

				if ( matcher.matches() )
					selectedColumnNames.add( columnName );
			}
		}

		return selectedColumnNames;
	}


}
