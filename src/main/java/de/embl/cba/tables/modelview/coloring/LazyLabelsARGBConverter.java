package de.embl.cba.tables.modelview.coloring;

import de.embl.cba.bdv.utils.lut.GlasbeyARGBLut;
import net.imglib2.Volatile;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.volatiles.VolatileARGBType;

public class LazyLabelsARGBConverter implements LabelsARGBConverter
{
	private final ColoringModel< Double > coloringModel;

	private int timePointIndex; // TODO: ??

	public LazyLabelsARGBConverter()
	{
		this.coloringModel = new LazyCategoryColoringModel< >( new GlasbeyARGBLut() );
		timePointIndex = 0;
	}

	@Override
	public void convert( RealType label, VolatileARGBType color )
	{
		if ( label instanceof Volatile )
		{
			if ( ! ( ( Volatile ) label ).isValid() )
			{
				color.set( 0 );
				color.setValid( false );
				return;
			}
		}

		final double realDouble = label.getRealDouble();

		if ( realDouble == 0 )
		{
			color.setValid( true );
			color.set( 0 );
			return;
		}

		coloringModel.convert( realDouble, color.get() );
		color.setValid( true );

	}

	@Override
	public void timePointChanged( int timePointIndex )
	{
		this.timePointIndex = timePointIndex;
	}
}
