package de.embl.cba.tables.modelview.datamodels;

import de.embl.cba.tables.modelview.objects.DefaultImageSegment;
import de.embl.cba.tables.modelview.objects.DefaultImageSegmentBuilder;
import de.embl.cba.tables.modelview.objects.ImageSegmentCoordinate;
import net.imglib2.util.ValuePair;

import java.util.HashMap;
import java.util.Map;

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
			Map< ImageSegmentCoordinate, ValuePair< String, Integer > > coordinateColumnMap,
			HashMap< String, Object > columnValueMap )
	{
		final DefaultImageSegmentBuilder segmentBuilder = new DefaultImageSegmentBuilder();

		for( ImageSegmentCoordinate coordinate : coordinateColumnMap.keySet() )
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
				case ImageSetId:
					segmentBuilder.setImageId( ( String ) columnValueMap.get( colName )  );
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
				case Label:
					segmentBuilder.setLabel(
							Double.parseDouble(
									rowEntries[ col ] ) );
					break;
				case ImageSetId:
					segmentBuilder.setImageId( rowEntries[ col ] );
					break;

			}
		}

		return segmentBuilder.build();
	}
}
