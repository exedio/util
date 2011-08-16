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
	public static String encodeUpper(final byte[] bytes)
	{
		if(bytes==null)
			return null;

		return encode(bytes, 0, bytes.length, DICTIONARY_UPPER);
	}

	public static String encodeLower(final byte[] bytes)
	{
		if(bytes==null)
			return null;

		return encode(bytes, 0, bytes.length, DICTIONARY_LOWER);
	}

	public static String encodeUpper(final byte[] bytes, final int offset, final int length)
	{
		return encode(bytes, offset, length, DICTIONARY_UPPER);
	}

	public static String encodeLower(final byte[] bytes, final int offset, final int length)
	{
		return encode(bytes, offset, length, DICTIONARY_LOWER);
	}

	private static String encode(final byte bytes[], final int offset, final int length, final char[] dictionary)
	{
		final char[] result = new char[length*2];

		final int toIndex = length + offset;
		int i2 = 0;
		for(int i = offset; i<toIndex; i++)
		{
			final byte b = bytes[i];
			result[i2++] = dictionary[(b & 0xf0)>>4];
			result[i2++] = dictionary[ b & 0x0f    ];
		}
		return new String(result);
	}

	public static void append(final StringBuilder out, final byte[] bytes, final int len)
	{
		if(bytes==null)
			return;

		for(int i = 0; i<len; i++)
		{
			final byte b = bytes[i];
			out.append(DICTIONARY_LOWER[(b & 0xF0)>>4]);
			out.append(DICTIONARY_LOWER[b & 0x0F]);
		}
	}

	private static final char[] DICTIONARY_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	private static final char[] DICTIONARY_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};


	@edu.umd.cs.findbugs.annotations.SuppressWarnings("PZLA_PREFER_ZERO_LENGTH_ARRAYS")
	public static byte[] decodeLower(final String string)
	{
		if(string==null)
			return null;
		if((string.length()&1)!=0)
			throw new IllegalArgumentException("odd length: " + string);

		final int length = string.length()>>1;
		final byte[] result = new byte[length];

		int i2 = 0;
		for(int i = 0; i<length; i++)
		{
			result[i] = (byte)( // don't know, why I need this cast
				(decodeLower(string.charAt(i2++))<<4) |
				(decodeLower(string.charAt(i2++))   )   );
		}
		return result;
	}

	static byte decodeLower(final char c)
	{
		if('0'<=c&&c<='9')
			return ((byte)(c-'0'));
		else if('a'<=c&&c<='f')
			return ((byte)(c-('a'-10)));
		else
			throw new IllegalArgumentException(String.valueOf(c));
	}

	private Hex()
	{
		// prevent instantiation
	}
}
