/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cope.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Color;
import java.text.DecimalFormat;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * This test evaluates, whether it is ok to use {@link Color}
 * for storing rgba values suitable for Cascading Style Sheets (CSS).
 * Especially regarding rounding issues between the 0.00 - 1.00 syntax
 * of CSS and the bytes stores in {@link java.awt.Color Color}.
 */
public class ColorDecimalAlphaTest
{
	@Disabled
	@Test void testIt()
	{
		for(int alphaPercent = 0; alphaPercent<=100; alphaPercent++)
		{
			final String expectedDecimal = (alphaPercent/100) + "," + ((alphaPercent/10)%10) + ((alphaPercent)%10);
			final int alphaByte = (int)(alphaPercent * 2.55f);
			@SuppressWarnings("cast")
			final float alphaFloat = ((float)alphaByte)/255f;
			final int alphaByteFromFloat = (((int)(alphaFloat*255)) & 0xFF);
			assertEquals(alphaByte, alphaByteFromFloat);
			final String actualDecimal = new DecimalFormat("#########0.00").format(alphaFloat);
			assertEquals(expectedDecimal, actualDecimal);
			System.out.println(alphaPercent + " " + alphaByte + "  " + alphaByteFromFloat + "  " + expectedDecimal + "  " + actualDecimal + "  " + alphaFloat);
			final Color color = new Color(0.22f, 0.33f, 0.44f, alphaFloat);
			assertEquals(alphaByte, color.getAlpha());
			assertEquals(alphaFloat, color.getRGBComponents(null)[3]);
		}
	}
}
