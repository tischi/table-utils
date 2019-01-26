package de.embl.cba.tables.modelview.coloring;

import bdv.tools.brightness.SliderPanelDouble;
import bdv.util.BoundedValueDouble;
import de.embl.cba.tables.modelview.objects.AnnotatedImageSegment;

import javax.swing.*;
import java.awt.*;

public class NumericColoringModelDialog extends JFrame implements ColoringListener
{
	private static Point minMaxDialogLocation;

	public NumericColoringModelDialog(
			final String coloringFeature,
			final NumericColoringModel< ? > coloringModel  )
	{

		final JFrame frame = new JFrame( coloringFeature );

		frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );

		final BoundedValueDouble min = new BoundedValueDouble(
				coloringModel.getMin(),
				coloringModel.getMax(),
				coloringModel.getMin() );

		final BoundedValueDouble max = new BoundedValueDouble(
				coloringModel.getMin(),
				coloringModel.getMax(),
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
		if ( minMaxDialogLocation != null )
			frame.setLocation( minMaxDialogLocation );

	}

	public void close()
	{
		minMaxDialogLocation = getLocation();
		dispose();
	}


	@Override
	public void coloringChanged()
	{

	}
}
