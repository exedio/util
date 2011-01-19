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

import static com.exedio.cope.util.Properties.EMPTY_SOURCE;

import com.exedio.cope.junit.CopeAssert;

public class EmptySourceTest extends CopeAssert
{
	public void testIt()
	{
		try
		{
			EMPTY_SOURCE.get(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("key", e.getMessage());
		}
		try
		{
			EMPTY_SOURCE.get("");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("key must not be empty", e.getMessage());
		}
		assertEquals(null, EMPTY_SOURCE.get("xxx"));
		assertUnmodifiable(EMPTY_SOURCE.keySet());
		assertEquals(list(), EMPTY_SOURCE.keySet());
		assertEquals("empty", EMPTY_SOURCE.getDescription());
		assertEquals("EmptySource", EMPTY_SOURCE.toString());
	}
}
