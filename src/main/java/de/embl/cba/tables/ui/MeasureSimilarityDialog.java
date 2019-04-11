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
	private static final String Z_SCORE = "Column-wise Z-Score";


	JTable table;
	private Map< String, SummaryStatistics > columnNameToSummaryStatistics;
	private String selectedMetric;
	private String selectedNorm;
	private double[] selectedColumnMeans;
	private double[] selectedColumnSigmas;
	private int[] selectedColumnIndices;
	private ArrayList< String > selectedColumnNames;
	private String newColumnName;
	private String columnSelectionRegExp;
	private Double[] distances;

	public MeasureSimilarityDialog( )
	{
		this.columnNameToSummaryStatistics = new LinkedHashMap<>();
	}


	public void showDialog( JTable table, Set< T > selectedRows )
	{
		this.table = table;

		if ( ! initChoicesFromDialog() ) return;

		configSelectedColumns( );

		if ( selectedNorm.equals( Z_SCORE ))
			computeSelectedColumnMeansAndSigmas( );

		final double[] referenceVector = computeReferenceVector( selectedRows );

		if ( selectedMetric.equals( L2_NORM ) )
			distances = distances( referenceVector );

		( ( DefaultTableModel ) table.getModel() ).addColumn( newColumnName, distances );
	}

	private boolean initChoicesFromDialog()
	{
		final String[] norms = new String[]
				{
						Z_SCORE
				};


		final String[] metrics = new String[]
				{
						L2_NORM
				};


		final GenericDialog gd = new GenericDialog( "Measure Similarity" );
		gd.addStringField( "Column RegExp", ".*", 20 );

		if ( selectedMetric == null ) selectedMetric = metrics[ 0 ];
		gd.addChoice( "Metric", metrics, selectedMetric );

		if ( selectedNorm == null ) selectedNorm = norms[ 0 ];
		gd.addChoice( "Normalisation", norms, selectedMetric );

		gd.addStringField( "New Column Name", "Similarity", 20 );

		gd.showDialog();
		if ( gd.wasCanceled() ) return false;

		columnSelectionRegExp = gd.getNextString();
		selectedMetric = gd.getNextChoice();
		newColumnName = gd.getNextString();

		return true;
	}

	private void computeSelectedColumnMeansAndSigmas( )
	{
		final int n = selectedColumnIndices.length;
		selectedColumnMeans = new double[ n ];
		selectedColumnSigmas = new double[ n ];

		for ( int i = 0; i < n; ++i )
		{
			final SummaryStatistics summaryStatistics =
					getSummaryStatistics( table, selectedColumnNames.get( i ) );

			selectedColumnMeans[ i ] = summaryStatistics.mean;
			selectedColumnSigmas[ i ] = summaryStatistics.sigma;
		}
	}

	private void configSelectedColumns( )
	{
		selectedColumnNames = getSelectedColumnNames( table, columnSelectionRegExp );

		final int n = selectedColumnNames.size();
		selectedColumnIndices = new int[ n ];

		for ( int i = 0; i < n; ++i )
		{
			selectedColumnIndices[ i ] =
					table.getColumnModel().getColumnIndex( selectedColumnNames.get( i ) );
		}

	}

	private double[] computeReferenceVector( Set< T > selectedRows )
	{
		final ArrayList< double[] > normVectors = new ArrayList<>();
		for ( T tableRow : selectedRows )
		{
			final double[] normVector = getZScoreNormalisedRowVector(
					table, tableRow.rowIndex(), selectedColumnIndices, selectedColumnMeans, selectedColumnSigmas );

			normVectors.add( normVector );
		}

		return computeAverageVector( normVectors );
	}

	private double[] computeAverageVector( ArrayList< double[] > normVectors )
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

	private Double[] distances( double[] referenceVector )
	{
		final int rowCount = table.getRowCount();

		final Double[] distances = new Double[ rowCount ];

		for ( int rowIndex = 0; rowIndex < rowCount; ++rowIndex )
		{
			final double[] rowVector = getZScoreNormalisedRowVector(
					table, rowIndex, selectedColumnIndices, selectedColumnMeans, selectedColumnSigmas );

			double distance = l2Distance( rowVector, referenceVector );

			distances[ rowIndex ] = distance;
		}

		return distances;
	}

	private double l2Distance( double[] rowVector, double[] avgNormVector )
	{
		int n = rowVector.length ;
		double distance = 0.0;
		for ( int i = 0; i < n; ++i )
			distance += Math.pow( rowVector[ i ] - avgNormVector[ i ], 2 );
		distance = Math.sqrt( distance );
		return distance;
	}

	private double[] getZScoreNormalisedRowVector(
			final JTable table,
			final int rowIndex,
			final int[] selectedColumnIndices,
			final double[] means,
			final double[] sigmas )
	{
		final double[] rawVector = getRowVector( table, rowIndex, selectedColumnIndices );

		final double[] normVector = zScoreNormalisation( means, sigmas, rawVector );

		return normVector;
	}

	private double[] zScoreNormalisation( double[] means, double[] sigmas, double[] rawVector )
	{
		int n = rawVector.length;
		final double[] normVector = new double[ n ];
		for ( int i = 0; i < n; ++i )
			normVector[ i ] =  ( rawVector[ i ] - means[ i ] ) / sigmas[ i ];
		return normVector;
	}

	private double[] getRowVector( JTable table, int rowIndex, int[] selectedColumnIndices )
	{
		int n = selectedColumnIndices.length;
		final double[] rawVector = new double[ n ];
		for ( int i = 0; i < n; ++i )
			rawVector[ i ] = ( Double ) table.getValueAt( rowIndex, selectedColumnIndices[ i ] );
		return rawVector;
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

	private ArrayList< String > getSelectedColumnNames( JTable table, String columnNameRegExp )
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
