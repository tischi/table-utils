package de.embl.cba.tables.imagesegment;

import de.embl.cba.tables.tablerow.TableRowImageSegment;
import net.imglib2.FinalRealInterval;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * All values are dynamically fetched from the columns.
 * This might be slow, but allows changes in columns to be reflected.
 * // TODO: make interface for ColumnBasedTableRow
 */
public class ColumnBasedTableRowImageSegment implements TableRowImageSegment
{
	private final int row;
	private final LinkedHashMap< String, List< ? > > columns;
	private final Map< SegmentProperty, List< ? > > segmentPropertyToColumn;
	private double[] position;
	FinalRealInterval boundingBox;
	private LinkedHashMap< String, Object > cells;
	private boolean isOneBasedTimePoint;
	private float[] mesh;

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

	public LinkedHashMap< String, List< ? > > getColumns()
	{
		return columns;
	}

	private synchronized void setPosition()
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
	public FinalRealInterval boundingBox()
	{
		setBoundingBox();
		return boundingBox;
	}

	@Override
	public float[] getMesh()
	{
		return mesh;
	}

	@Override
	public void setMesh( float[] mesh )
	{
		this.mesh = mesh;
	}

	private void setBoundingBox()
	{
		// TODO: this checking needs improvement...
		if ( ! segmentPropertyToColumn.containsKey( SegmentProperty.BoundingBoxXMin ) )
		{
			boundingBox = null;
			return;
		}

		final double[] min = getBoundingBoxMin();
		final double[] max = getBoundingBoxMax();

		boundingBox = new FinalRealInterval( min, max );
	}

	private double[] getBoundingBoxMax()
	{
		final double[] max = new double[ numDimensions() ];

		if ( segmentPropertyToColumn.containsKey( SegmentProperty.BoundingBoxXMax ) )
			max[ 0 ] = Double.parseDouble(
					segmentPropertyToColumn
							.get( SegmentProperty.BoundingBoxXMax )
							.get( row ).toString() );

		if ( segmentPropertyToColumn.containsKey( SegmentProperty.BoundingBoxYMax ) )
			max[ 1 ] = Double.parseDouble(
					segmentPropertyToColumn
							.get( SegmentProperty.BoundingBoxYMax )
							.get( row ).toString() );

		if ( segmentPropertyToColumn.containsKey( SegmentProperty.BoundingBoxZMax ) )
			max[ 2 ] = Double.parseDouble(
					segmentPropertyToColumn
							.get( SegmentProperty.BoundingBoxZMax )
							.get( row ).toString() );
		return max;
	}

	private double[] getBoundingBoxMin()
	{
		final double[] min = new double[ numDimensions() ];

		if ( segmentPropertyToColumn.containsKey( SegmentProperty.BoundingBoxXMin ) )
			min[ 0 ] = Double.parseDouble(
							segmentPropertyToColumn
								.get( SegmentProperty.BoundingBoxXMin )
								.get( row ).toString() );

		if ( segmentPropertyToColumn.containsKey( SegmentProperty.BoundingBoxYMin ) )
			min[ 1 ] = Double.parseDouble(
					segmentPropertyToColumn
							.get( SegmentProperty.BoundingBoxYMin )
							.get( row ).toString() );

		if ( segmentPropertyToColumn.containsKey( SegmentProperty.BoundingBoxZMin ) )
			min[ 2 ] = Double.parseDouble(
					segmentPropertyToColumn
							.get( SegmentProperty.BoundingBoxZMin )
							.get( row ).toString() );
		return min;
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
		setPosition();

		for ( int d = 0; d < 3; d++ )
			position[ d ] = (float) this.position[ d ];
	}

	@Override
	public synchronized void localize( double[] position )
	{
		setPosition();

		for ( int d = 0; d < 3; d++ )
			position[ d ] = this.position[ d ];
	}

	@Override
	public synchronized float getFloatPosition( int d )
	{
		setPosition();
		return (float) position[ d ];
	}

	@Override
	public synchronized double getDoublePosition( int d )
	{
		setPosition();
		return position[ d ];
	}

	@Override
	public int numDimensions()
	{
		setPosition();
		return position.length;
	}
}
