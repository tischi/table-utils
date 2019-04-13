package de.embl.cba.tables.modelview.segments;

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
	private final Map< SegmentProperty, List< ? > > segmentPropertyToColumn;
	private double[] position;
	private LinkedHashMap< String, Object > cells;
	private boolean isOneBasedTimePoint;

	public ColumnBasedTableRowImageSegment(
			int row,
			LinkedHashMap< String, List< ? > > columns,
			Map< SegmentProperty, List< ? > > segmentPropertyToColumn,
			boolean isOneBasedTimePoint )
	{
		this.row = row;
		this.columns = columns;
		this.segmentPropertyToColumn = segmentPropertyToColumn;
		this.isOneBasedTimePoint = isOneBasedTimePoint;
	}

	private synchronized void setPositionFromColumns()
	{
		if ( position != null ) return;

		position = new double[ 3 ];

		if ( segmentPropertyToColumn.containsKey( SegmentProperty.X ) )
			position[ 0 ] = Double.parseDouble(
					segmentPropertyToColumn
							.get( SegmentProperty.X )
							.get( row ).toString() );

		if ( segmentPropertyToColumn.containsKey( SegmentProperty.Y ) )
			position[ 1 ] = Double.parseDouble(
					segmentPropertyToColumn
							.get( SegmentProperty.Y )
							.get( row ).toString() );

		if ( segmentPropertyToColumn.containsKey( SegmentProperty.Z ) )
			position[ 2 ] = Double.parseDouble(
					segmentPropertyToColumn
							.get( SegmentProperty.Z )
							.get( row ).toString() );
	}

	@Override
	public String imageId()
	{
		return segmentPropertyToColumn
				.get( SegmentProperty.LabelImage )
				.get( row ).toString();
	}

	@Override
	public double labelId()
	{
		return Double.parseDouble( segmentPropertyToColumn
				.get( SegmentProperty.ObjectLabel )
				.get( row ).toString() );
	}

	@Override
	public int timePoint()
	{
		if ( segmentPropertyToColumn.get( SegmentProperty.T ) == null )
			return 0;

		int timePoint = ( ( Double ) segmentPropertyToColumn
				.get( SegmentProperty.T )
				.get( row )).intValue();

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
			position[ d ] = (float) this.position[ d ];
	}

	@Override
	public synchronized void localize( double[] position )
	{
		setPositionFromColumns();

		for ( int d = 0; d < 3; d++ )
			position[ d ] = this.position[ d ];
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
