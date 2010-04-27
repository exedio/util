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

import java.util.Locale;

import com.exedio.cope.junit.CopeAssert;

public class HexTest extends CopeAssert
{
	public void testIt()
	{
		assertIt("000102030405060708090a0b0c0d0e0f", new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0xa, 0xb, 0xc, 0xd, 0xe, 0xf});
		assertIt("001020304050", new byte[]{0, 0x10, 0x20, 0x30, 0x40, 0x50});
		assertIt("000102", new byte[]{0, 1, 2});
		assertIt("0a", new byte[]{0x0a});
		assertIt("", new byte[]{});
		assertEquals(null, Hex.encodeLower(null));
	}
	
	private void assertIt(final String expected, final byte[] actual)
	{
		assertEquals(expected, Hex.encodeLower(actual));
		assertEquals(expected.toUpperCase(Locale.UK), Hex.encodeUpper(actual));
	}
}
