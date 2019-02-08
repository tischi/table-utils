package de.embl.cba.tables.modelview.segments;

import net.imglib2.FinalInterval;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * All values are dynamically fetched from the columns.
 * This might be slow, but allows changes in columns to be reflected.
 *
 */
public class ColumnBasedTableRowImageSegment implements TableRowImageSegment
{
	private final int row;
	private final LinkedHashMap< String, List< Object > > columns;
	private final Map< ImageSegmentCoordinate, List< Object > > imageSegmentCoordinateToColumn;
	private double[] position;
	private LinkedHashMap< String, Object > cells;


	public ColumnBasedTableRowImageSegment(
			int row,
			LinkedHashMap< String, List< Object > > columns,
			Map< ImageSegmentCoordinate, List< Object > > imageSegmentCoordinateToColumn )
	{
		this.row = row;
		this.columns = columns;
		this.imageSegmentCoordinateToColumn = imageSegmentCoordinateToColumn;
	}

	private synchronized void setPositionFromColumns()
	{
		position = new double[ 3 ];

		if ( imageSegmentCoordinateToColumn.containsKey( ImageSegmentCoordinate.X ) )
			position[ 0 ] = Double.parseDouble( imageSegmentCoordinateToColumn.get( ImageSegmentCoordinate.X ).get( row ).toString() );

		if ( imageSegmentCoordinateToColumn.containsKey( ImageSegmentCoordinate.Y ) )
			position[ 1 ] = Double.parseDouble( imageSegmentCoordinateToColumn.get( ImageSegmentCoordinate.Y ).get( row ).toString() );

		if ( imageSegmentCoordinateToColumn.containsKey( ImageSegmentCoordinate.Z ) )
			position[ 2 ] = Double.parseDouble( imageSegmentCoordinateToColumn.get( ImageSegmentCoordinate.Z ).get( row ).toString() );
	}

	@Override
	public String imageId()
	{
		return imageSegmentCoordinateToColumn.get( ImageSegmentCoordinate.ImageId ).get( row ).toString();
	}

	@Override
	public double labelId()
	{
		return Double.parseDouble( imageSegmentCoordinateToColumn.get( ImageSegmentCoordinate.LabelId ).get( row ).toString() );
	}

	@Override
	public int timePoint()
	{
		if ( imageSegmentCoordinateToColumn.containsKey( ImageSegmentCoordinate.T ) )
		{
			return Integer.parseInt( imageSegmentCoordinateToColumn.get( ImageSegmentCoordinate.T ).get( row ).toString() );
		}
		else
		{
			return 0;
		}
	}

	@Override
	public FinalInterval boundingBox()
	{
		return null;
	}

	@Override
	public LinkedHashMap< String, Object > cells()
	{
		setCellsFromColumns();

		return cells;
	}

	private synchronized void setCellsFromColumns()
	{
		cells = new LinkedHashMap<>();

		for ( String columnName : columns.keySet() )
		{
			final Object columnValue = columns.get( columnName ).get( row );
			cells.put( columnName, columnValue );
		}
	}

	@Override
	public int rowIndex()
	{
		return row;
	}

	@Override
	public void localize( float[] position )
	{
		setPositionFromColumns();

		for ( int d = 0; d < 3; d++ )
		{
			position[ d ] = (float) this.position[ d ];
		}
	}

	@Override
	public void localize( double[] position )
	{
		setPositionFromColumns();

		for ( int d = 0; d < 3; d++ )
		{
			position[ d ] = this.position[ d ];
		}
	}

	@Override
	public float getFloatPosition( int d )
	{
		setPositionFromColumns();
		return (float) position[ d ];
	}

	@Override
	public double getDoublePosition( int d )
	{
		setPositionFromColumns();
		return position[ d ];
	}

	@Override
	public int numDimensions()
	{
		setPositionFromColumns();
		return position.length;
	}
}
