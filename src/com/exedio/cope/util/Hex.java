/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

public final class Hex
{
	public static final String encodeUpper(final byte[] bytes)
	{
		return encode(bytes, DICTIONARY_UPPER);
	}
	
	public static final String encodeLower(final byte[] bytes)
	{
		return encode(bytes, DICTIONARY_LOWER);
	}
	
	private static final String encode(final byte[] bytes, final char[] dictionary)
	{
		if(bytes==null)
			return null;
		
		final int length = bytes.length;
		final char[] result = new char[length*2];

		int i2 = 0;
		for(int i = 0; i<length; i++)
		{
			final byte b = bytes[i];
			result[i2++] = dictionary[(b & 0xf0)>>4];
			result[i2++] = dictionary[ b & 0x0f    ];
		}
		return new String(result);
	}
	
	private static final char[] DICTIONARY_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	private static final char[] DICTIONARY_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	
	private Hex()
	{
		// prevent instantiation
	}
}
