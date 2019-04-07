package de.embl.cba.tables.modelview.segments;

import de.embl.cba.tables.ui.CoordinateColumnsSelectionDialog;
import net.imglib2.FinalInterval;

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
	private final LinkedHashMap< String, List< ? > > columns;
	private final Map< ImageSegmentCoordinate, List< ? > > imageSegmentCoordinateToColumn;
	private double[] position;
	private LinkedHashMap< String, Object > cells;
	private boolean isOneBasedTimePoint;


	public ColumnBasedTableRowImageSegment(
			int row,
			LinkedHashMap< String, List< ? > > columns,
			Map< ImageSegmentCoordinate, List< ? > > imageSegmentCoordinateToColumn,
			boolean isOneBasedTimePoint )
	{
		this.row = row;
		this.columns = columns;
		this.imageSegmentCoordinateToColumn = imageSegmentCoordinateToColumn;
		this.isOneBasedTimePoint = isOneBasedTimePoint;
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
		return imageSegmentCoordinateToColumn.get( ImageSegmentCoordinate.LabelImage ).get( row ).toString();
	}

	@Override
	public double labelId()
	{
		return Double.parseDouble( imageSegmentCoordinateToColumn.get( ImageSegmentCoordinate.ObjectLabel ).get( row ).toString() );
	}

	@Override
	public int timePoint()
	{
		if ( imageSegmentCoordinateToColumn.get( ImageSegmentCoordinate.T ) == null )
			return 0;

		int timePoint = ( ( Double ) imageSegmentCoordinateToColumn.get( ImageSegmentCoordinate.T ).get( row )).intValue();
		if ( isOneBasedTimePoint ) timePoint -= 1;
		return timePoint;
	}

	@Override
	public FinalInterval boundingBox()
	{
		return null;
	}

	@Override
	public synchronized LinkedHashMap< String, Object > cells()
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
	public synchronized void localize( float[] position )
	{
		setPositionFromColumns();

		for ( int d = 0; d < 3; d++ )
		{
			position[ d ] = (float) this.position[ d ];
		}
	}

	@Override
	public synchronized void localize( double[] position )
	{
		setPositionFromColumns();

		for ( int d = 0; d < 3; d++ )
		{
			position[ d ] = this.position[ d ];
		}
	}

	@Override
	public synchronized float getFloatPosition( int d )
	{
		setPositionFromColumns();
		return (float) position[ d ];
	}

	@Override
	public synchronized double getDoublePosition( int d )
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
