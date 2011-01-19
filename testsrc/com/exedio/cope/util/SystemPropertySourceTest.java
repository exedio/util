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

import static com.exedio.cope.util.Properties.SYSTEM_PROPERTY_SOURCE;

import com.exedio.cope.junit.CopeAssert;

public class SystemPropertySourceTest extends CopeAssert
{
	public void testIt()
	{
		try
		{
			SYSTEM_PROPERTY_SOURCE.get(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("key can't be null", e.getMessage());
		}
		try
		{
			SYSTEM_PROPERTY_SOURCE.get("");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("key can't be empty", e.getMessage());
		}
		assertEquals(null, SYSTEM_PROPERTY_SOURCE.get("xxx"));
		assertNull(SYSTEM_PROPERTY_SOURCE.keySet());
		assertEquals("java.lang.System.getProperty", SYSTEM_PROPERTY_SOURCE.getDescription());
		assertEquals("SystemPropertySource", SYSTEM_PROPERTY_SOURCE.toString());
	}

	@Deprecated // OK: testing deprecated api
	public void testDeprecated()
	{
		assertSame(SYSTEM_PROPERTY_SOURCE, Properties.getSystemPropertySource());
	}
}
