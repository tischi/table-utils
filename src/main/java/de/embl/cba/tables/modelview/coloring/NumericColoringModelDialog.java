package de.embl.cba.tables.modelview.coloring;

import bdv.tools.brightness.SliderPanelDouble;
import bdv.util.BoundedValueDouble;

import javax.swing.*;
import java.awt.*;

public class NumericColoringModelDialog extends JFrame implements ColoringListener
{
	private static Point dialogLocation;

	public NumericColoringModelDialog(
			final String coloringFeature,
			final NumericColoringModel< ? > coloringModel,
			double[] valueRange )
	{

		final JFrame frame = new JFrame( coloringFeature );

		frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );

		final BoundedValueDouble min = new BoundedValueDouble(
				valueRange[ 0 ],
				valueRange[ 1 ],
				coloringModel.getMin() );

		final BoundedValueDouble max = new BoundedValueDouble(
				valueRange[ 0 ],
				valueRange[ 1 ],
				coloringModel.getMax() );

		JPanel panel = new JPanel();
		panel.setLayout( new BoxLayout( panel, BoxLayout.PAGE_AXIS ) );
		final SliderPanelDouble minSlider = new SliderPanelDouble( "Min", min, 1 );
		final SliderPanelDouble maxSlider = new SliderPanelDouble( "Max", max, 1 );

		class UpdateListener implements BoundedValueDouble.UpdateListener
		{
			@Override
			public void update()
			{
				coloringModel.setMin( min.getCurrentValue() );
				coloringModel.setMax( max.getCurrentValue() );
				minSlider.update();
				maxSlider.update();
			}
		}

		final UpdateListener updateListener = new UpdateListener();

		min.setUpdateListener( updateListener );
		max.setUpdateListener( updateListener );

		panel.add( minSlider );
		panel.add( maxSlider );

		frame.setContentPane( panel );
		frame.setBounds( MouseInfo.getPointerInfo().getLocation().x,
				MouseInfo.getPointerInfo().getLocation().y,
				120, 10);
		frame.pack();
		frame.setVisible( true );
		if ( dialogLocation != null )
			frame.setLocation( dialogLocation );

	}

	public void close()
	{
		dialogLocation = getLocation();
		dispose();
	}


	@Override
	public void coloringChanged()
	{

	}
}
