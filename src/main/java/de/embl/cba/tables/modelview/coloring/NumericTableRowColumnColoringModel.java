package de.embl.cba.tables.modelview.coloring;

import de.embl.cba.bdv.utils.lut.ARGBLut;
import de.embl.cba.tables.Tables;
import de.embl.cba.tables.modelview.segments.TableRow;
import net.imglib2.type.numeric.ARGBType;


// TODO: extract abstract class NumericFeatureColoringModel
public class NumericTableRowColumnColoringModel< T extends TableRow >
		extends AbstractColoringModel< T > implements NumericColoringModel< T >
{
	private String columnName;
	private ARGBLut lut;
	private double[] lutMinMax;
	private double[] lutRange;

	public NumericTableRowColumnColoringModel(
			String columnName,
			ARGBLut lut,
			double[] lutMinMax,
			double[] lutRange )
	{
		this.columnName = columnName;
		this.lut = lut;
		this.lutMinMax = lutMinMax;
		this.lutRange = lutRange;
	}

	@Override
	public void convert( T tableRow, ARGBType output )
	{
		final Object featureValue = tableRow.cells().get( columnName );
		setColorLinearly( featureValue, output );
	}


	@Override
	public double getMin()
	{
		return lutMinMax[ 0 ];
	}


	@Override
	public double getMax()
	{
		return lutMinMax[ 1 ];
	}


	@Override
	public void setMin( double min )
	{
		this.lutMinMax[ 0 ] = min;
		notifyColoringListeners();
	}

	@Override
	public void setMax( double max )
	{
		this.lutMinMax[ 1 ] = max;
		notifyColoringListeners();
	}

	public void setColumnName( String columnName )
	{
		this.columnName = columnName;
	}

	public String getColumnName()
	{
		return columnName;
	}


	public void setColorLinearly( Object featureValue, ARGBType output )
	{
		final double value = Tables.asDouble( featureValue );
		double normalisedValue = computeLinearNormalisedValue( value );
		final int colorIndex = lut.getARGB( normalisedValue );
		output.set( colorIndex );
	}

	public double computeLinearNormalisedValue( double value )
	{
		double normalisedValue = 0;
		if ( lutMinMax[ 1 ] == lutMinMax[ 0 ] )
		{
			if ( lutMinMax[ 1 ] == lutRange[ 0 ] )
				normalisedValue = 1.0;
			else if ( lutMinMax[ 1 ] == lutRange[ 1 ] )
				normalisedValue = 0.0;
		}
		else
		{
			normalisedValue =
					Math.max(
							Math.min(
									( value - lutMinMax[ 0 ] )
											/ ( lutMinMax[ 1 ] - lutMinMax[ 0 ] ), 1.0 ), 0.0 );
		}
		return normalisedValue;
	}

}
