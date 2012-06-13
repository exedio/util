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

import java.util.Arrays;
import java.util.Locale;

import com.exedio.cope.junit.CopeAssert;

public class HexTest extends CopeAssert
{
	public void testDecodeHelper()
	{
		assertEquals(0x00, Hex.decodeLower('0'));
		assertEquals(0x09, Hex.decodeLower('9'));
		assertEquals(0x0a, Hex.decodeLower('a'));
		assertEquals(0x0f, Hex.decodeLower('f'));
		assertDecodeHelperFails((char)('0'-1));
		assertDecodeHelperFails((char)('9'+1));
		assertDecodeHelperFails((char)('a'-1));
		assertDecodeHelperFails((char)('g'+1));
		assertDecodeHelperFails('A');
		assertDecodeHelperFails('F');
	}

	private static void assertDecodeHelperFails(final char c)
	{
		try
		{
			Hex.decodeLower(c);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(String.valueOf(c), e.getMessage());
		}
	}

	public void testIt()
	{
		assertIt("000ff0aa", new byte[]{0x00, 0x0f, (byte)0xf0, (byte)0xaa});
		assertIt("0123456789abcdef", new byte[]{0x01, 0x23, 0x45, 0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef});
		assertIt("000102030405060708090a0b0c0d0e0f", new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0xa, 0xb, 0xc, 0xd, 0xe, 0xf});
		assertIt("001020304050", new byte[]{0, 0x10, 0x20, 0x30, 0x40, 0x50});
		assertIt("000102", new byte[]{0, 1, 2});
		assertIt("0a", new byte[]{0x0a});
		assertIt("", new byte[]{});

		assertEquals("02030a", Hex.encodeLower(new byte[]{0, 1, 2, 3, 0xa, 0xb, 0xc, 0xd, 0xe, 0xf}, 2, 3));
		assertEquals("02030A", Hex.encodeUpper(new byte[]{0, 1, 2, 3, 0xa, 0xb, 0xc, 0xd, 0xe, 0xf}, 2, 3));

		assertEquals(null, Hex.encodeLower(null));
		assertEquals(null, Hex.decodeLower(null));
		try
		{
			Hex.decodeLower("a");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("odd length: a", e.getMessage());
		}
	}

	private static void assertIt(final String expected, final byte[] actual)
	{
		assertEquals(expected, Hex.encodeLower(actual));
		assertEquals(expected.toUpperCase(Locale.UK), Hex.encodeUpper(actual));
		final StringBuilder bf = new StringBuilder();
		Hex.append(bf, actual, actual.length);
		assertEquals(expected, bf.toString());

		assertTrue(Arrays.equals(actual, Hex.decodeLower(expected)));
	}
}
