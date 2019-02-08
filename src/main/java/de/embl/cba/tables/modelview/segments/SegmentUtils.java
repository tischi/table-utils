package de.embl.cba.tables.modelview.segments;

import net.imglib2.util.ValuePair;

import java.util.*;

public class SegmentUtils
{

	@Deprecated
	public static String getKey( Double label )
	{
		return getKey( label, 0 );
	}

	@Deprecated
	public static String getKey( Double label, Integer timePoint )
	{
		return "L"+label.toString() + "_T" + timePoint.toString();
	}

	public static DefaultImageSegment segmentFromFeatures(
			Map< ImageSegmentCoordinate, String > coordinateColumnMap,
			HashMap< String, Object > columnValueMap,
			DefaultImageSegmentBuilder segmentBuilder )
	{

		for( ImageSegmentCoordinate coordinate : coordinateColumnMap.keySet() )
		{
			final String colName = coordinateColumnMap.get( coordinate );

			columnValueMap.get( colName );

			switch ( coordinate )
			{
				case X:
					segmentBuilder.setX( ( double ) columnValueMap.get( colName ) );
					break;
				case Y:
					segmentBuilder.setY( ( double ) columnValueMap.get( colName ) );
					break;
				case Z:
					segmentBuilder.setZ( ( double ) columnValueMap.get( colName ) );
					break;
				case T:
					segmentBuilder.setTimePoint( ( int ) columnValueMap.get( colName ) );
					break;
				case LabelId:
					segmentBuilder.setLabel( ( double ) columnValueMap.get( colName ) );
					break;
				case ImageId:
					segmentBuilder.setImageId( columnValueMap.get( colName ).toString() );
					break;

			}
		}

		return segmentBuilder.build();
	}


	public static DefaultImageSegment segmentFromTableRowMap(
			final Map< ImageSegmentCoordinate, String > coordinateColumnMap,
			final TableRowMap tableRowMap,
			final DefaultImageSegmentBuilder segmentBuilder )
	{

		for( ImageSegmentCoordinate coordinate : coordinateColumnMap.keySet() )
		{
			final String colName = coordinateColumnMap.get( coordinate );

			tableRowMap.get( colName );

			switch ( coordinate )
			{
				case X:
					segmentBuilder.setX( Double.parseDouble( (String) tableRowMap.get( colName ) ) );
					break;
				case Y:
					segmentBuilder.setY( Double.parseDouble(  (String) tableRowMap.get( colName ) ));
					break;
				case Z:
					segmentBuilder.setZ( Double.parseDouble( (String) tableRowMap.get( colName ) ) );
					break;
				case T:
					segmentBuilder.setTimePoint( Integer.parseInt( (String) tableRowMap.get( colName ) ));
					break;
				case LabelId:
					segmentBuilder.setLabel(  Double.parseDouble((String)  tableRowMap.get( colName ) ));
					break;
				case ImageId:
					segmentBuilder.setImageId( tableRowMap.get( colName ).toString() );

					break;

			}
		}

		return segmentBuilder.build();
	}


	public static DefaultImageSegment segmentFromFeaturesIndexBased(
			Map< ImageSegmentCoordinate, ValuePair< String, Integer > > coordinateColumnMap,
			String[] rowEntries )
	{
		final DefaultImageSegmentBuilder segmentBuilder = new DefaultImageSegmentBuilder();

		for( ImageSegmentCoordinate coordinate : coordinateColumnMap.keySet() )
		{
			final Integer col = coordinateColumnMap.get( coordinate ).getB();

			switch ( coordinate )
			{
				case X:
					segmentBuilder.setX(
							Double.parseDouble(
									rowEntries[ col ] ) );
					break;
				case Y:
					segmentBuilder.setY(
							Double.parseDouble(
									rowEntries[ col ] ) );
					break;
				case Z:
					segmentBuilder.setZ(
							Double.parseDouble(
									rowEntries[ col ] ) );
					break;
				case T:
					segmentBuilder.setTimePoint(
							Integer.parseInt(
									rowEntries[ col ] ) );
					break;
				case LabelId:
					segmentBuilder.setLabel(
							Double.parseDouble(
									rowEntries[ col ] ) );
					break;
				case ImageId:
					segmentBuilder.setImageId( rowEntries[ col ] );
					break;

			}
		}

		return segmentBuilder.build();
	}


	public static List< ColumnBasedTableRowImageSegment > tableRowImageSegmentsFromColumns(
			final LinkedHashMap< String, List< Object > > columns,
			final Map< ImageSegmentCoordinate, List< Object > > imageSegmentCoordinateToColumn )
	{

		final List< ColumnBasedTableRowImageSegment > columnBasedTableRowImageSegments
				= new ArrayList<>();

		final int numRows = columns.values().iterator().next().size();

		for ( int row = 0; row < numRows; row++ )
		{
			final ColumnBasedTableRowImageSegment segment
					= new ColumnBasedTableRowImageSegment( row, columns, imageSegmentCoordinateToColumn );
			columnBasedTableRowImageSegments.add( segment );
		}

		return columnBasedTableRowImageSegments;

	}

}
