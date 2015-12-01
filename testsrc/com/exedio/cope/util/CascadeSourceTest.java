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

import static com.exedio.cope.util.Properties.EMPTY_SOURCE;
import static com.exedio.cope.util.Sources.cascade;
import static com.exedio.cope.util.Sources.view;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import com.exedio.cope.util.Properties.Source;
import java.util.Properties;
import org.junit.Test;

public class CascadeSourceTest
{
	@Test public void normal()
	{
		final Properties properties1 = new Properties();
		properties1.setProperty("key1a", "value1a");
		properties1.setProperty("key1b", "value1b");

		final Properties properties2 = new Properties();
		properties2.setProperty("key2a", "value2a");
		properties2.setProperty("key2b", "value2b");

		properties1.setProperty("key12", "value12-1");
		properties2.setProperty("key12", "value12-2");

		final Source s = cascade(
				view(properties1, "description1"),
				view(properties2, "description2"));

		assertEquals("value1a", s.get("key1a"));
		assertEquals("value1b", s.get("key1b"));
		assertEquals("value2a", s.get("key2a"));
		assertEquals("value2b", s.get("key2b"));
		assertEquals("value12-1", s.get("key12"));
		assertEquals("description1 / description2", s.getDescription());
		assertEquals("description1 / description2", s.toString());

		try
		{
			s.get("");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("key must not be empty", e.getMessage());
		}
		try
		{
			s.get(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("key", e.getMessage());
		}
	}

	@Test public void nullArray()
	{
		try
		{
			cascade((Source[])null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	@Test public void nullElementSingeton()
	{
		try
		{
			cascade(new Source[]{null});
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	@Test public void nullElementMultiple()
	{
		try
		{
			cascade(new Source[]{null, null});
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	@Test public void empty()
	{
		assertSame(EMPTY_SOURCE, cascade());
	}

	@Test public void singleton()
	{
		final Source singleton = view(new Properties(), "description1");
		assertSame(singleton, cascade(singleton));
	}

	@Test public void exposeArray()
	{
		final Source[] sources = new Source[]{
				view(new Properties(), "description1"),
				view(new Properties(), "description2")};

		final Source s = cascade(sources);
		assertEquals("description1 / description2", s.getDescription());

		sources[0] = null;
		// NOTE
		// the following line throws a NullPointerException,
		// if sources are not copied.
		assertEquals("description1 / description2", s.getDescription());
	}
}
