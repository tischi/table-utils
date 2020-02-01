package de.embl.cba.tables.color;

import net.imglib2.type.numeric.ARGBType;

import java.awt.*;

public abstract class ColorUtils
{
	public static Color getColor( ARGBType argbType )
	{
		final int colorIndex = argbType.get();

		return new Color(
				ARGBType.red( colorIndex ),
				ARGBType.green( colorIndex ),
				ARGBType.blue( colorIndex ),
				ARGBType.alpha( colorIndex ));
	}

	public static ARGBType getARGBType( Color color )
	{
		return new ARGBType( ARGBType.rgba( color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() ) );
	}
}
