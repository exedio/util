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

import static com.exedio.cope.util.Sources.SYSTEM_PROPERTIES;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.fail;

import com.exedio.cope.junit.CopeAssert;
import org.junit.Test;

public class SystemPropertySourceTest extends CopeAssert
{
	@Test public void testIt()
	{
		try
		{
			SYSTEM_PROPERTIES.get(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("key", e.getMessage());
		}
		try
		{
			SYSTEM_PROPERTIES.get("");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("key must not be empty", e.getMessage());
		}
		assertEquals(null, SYSTEM_PROPERTIES.get("xxx"));
		assertNull(SYSTEM_PROPERTIES.keySet());
		assertEquals("java.lang.System.getProperty", SYSTEM_PROPERTIES.getDescription());
		assertEquals("SystemPropertySource", SYSTEM_PROPERTIES.toString());
	}

	@Deprecated // OK: testing deprecated api
	@Test public void testDeprecated()
	{
		assertSame(SYSTEM_PROPERTIES, Properties.getSystemPropertySource());
		assertSame(SYSTEM_PROPERTIES, Properties.getSystemPropertyContext());
		assertSame(SYSTEM_PROPERTIES, Properties.SYSTEM_PROPERTY_SOURCE);
	}
}
