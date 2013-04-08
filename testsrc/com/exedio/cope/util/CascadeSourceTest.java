/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package com.exedio.copedemo.feature.util;

import static com.exedio.cope.util.Properties.EMPTY_SOURCE;
import static com.exedio.cope.util.Properties.getSource;
import static com.exedio.copedemo.feature.util.CascadeSource.cascade;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.Properties;

import org.junit.Test;

import com.exedio.cope.util.Properties.Source;

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
				getSource(properties1, "description1"),
				getSource(properties2, "description2"));

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
		final Source singleton = getSource(new Properties(), "description1");
		assertSame(singleton, cascade(singleton));
	}
}
