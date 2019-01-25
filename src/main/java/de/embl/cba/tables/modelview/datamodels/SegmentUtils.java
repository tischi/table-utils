package de.embl.cba.tables.modelview.datamodels;

import de.embl.cba.tables.modelview.objects.DefaultSegment;
import de.embl.cba.tables.modelview.objects.DefaultSegmentBuilder;
import de.embl.cba.tables.objects.SegmentCoordinate;
import net.imglib2.util.ValuePair;

import java.util.HashMap;
import java.util.Map;

public class SegmentUtils
{

	public static String getKey( Double label )
	{
		return getKey( label, 0 );
	}

	public static String getKey( Double label, Integer timePoint )
	{
		return "L"+label.toString() + "_T" + timePoint.toString();
	}

	public static DefaultSegment segmentFromFeatures(
			Map< SegmentCoordinate, ValuePair< String, Integer > > coordinateColumnMap,
			HashMap< String, Object > columnValueMap )
	{
		final DefaultSegmentBuilder segmentBuilder = new DefaultSegmentBuilder();

		for( SegmentCoordinate coordinate : coordinateColumnMap.keySet() )
		{
			final String colName = coordinateColumnMap.get( coordinate ).getA();

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
				case Label:
					segmentBuilder.setLabel( ( double ) columnValueMap.get( colName ) );
					break;
				case ImageId:
					segmentBuilder.setImageId( ( String ) columnValueMap.get( colName )  );
					break;

			}
		}

		return segmentBuilder.build();
	}

	public static DefaultSegment segmentFromFeaturesIndexBased(
			Map< SegmentCoordinate, ValuePair< String, Integer > > coordinateColumnMap,
			String[] rowEntries )
	{
		final DefaultSegmentBuilder segmentBuilder = new DefaultSegmentBuilder();

		for( SegmentCoordinate coordinate : coordinateColumnMap.keySet() )
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
				case Label:
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
}
