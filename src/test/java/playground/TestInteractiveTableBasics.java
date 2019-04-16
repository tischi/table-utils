package playground;

import javax.swing.*;

public class TestInteractiveTableBasics
{
	public static void main( String[] args )
	{

		int cols = 300;
		int rows = 10;

		String[] colNames = new String[ cols ];
		Object[][] data = new Object[ rows ][ cols ];

		for ( int i = 0; i < cols ; i++ )
		{
			colNames[ i ] = "AAAAA";
		}

		for ( int i = 0; i < rows; i++ )
		{
			for ( int j = 0; j < cols; j++ )
			{
				data[ i ][ j ] = i * j;
			}
		}

		JTable table = new JTable( data, colNames );

//		final ObjectTablePanel panel = new ObjectTablePanel( table );
//
//		final Object[] newStringRow = new Object[ tablerow ];
//		for ( int i = 0; i < tablerow; i++ )
//		{
//			newStringRow[ i ] = "aaa";
//		}
//
//		((DefaultTableModel)panel.getTableModel()).addRow( newStringRow );
//
//		final Double[] newDoubleRow = new Double[ tablerow ];
//		for ( int i = 0; i < tablerow; i++ )
//		{
//			newDoubleRow[ i ] = 1.0;
//		}
//
//		((DefaultTableModel)panel.getTableModel()).addRow( newDoubleRow );
//
//		panel.createTableUIAndShow();

	}
}
