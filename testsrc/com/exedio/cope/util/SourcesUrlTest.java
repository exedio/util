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

import static com.exedio.cope.util.Sources.load;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.util.Properties.Source;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import org.junit.Test;

public class SourcesUrlTest extends CopeAssert
{
	@SuppressWarnings("static-method")
	@Test public final void testIt()
	{
		final URL url = SourcesUrlTest.class.getResource("sourcesUrlTest.properties");
		final Source s = load(url);
		try
		{
			s.get(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("key", e.getMessage());
		}
		try
		{
			s.get("");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("key must not be empty", e.getMessage());
		}
		assertEquals(null, s.get("xxx"));
		assertEquals("testValue1", s.get("testKey1"));
		assertEquals("testValue2", s.get("testKey2"));
		assertContains("testKey1", "testKey2", s.keySet());
		assertUnmodifiable(s.keySet());
		assertEquals(url.toString(), s.getDescription());
		assertEquals(url.toString(), s.toString());
	}

	@SuppressWarnings("static-method")
	@Test public final void testNotExists() throws MalformedURLException
	{
		try
		{
			load(new URL("http://sourcetest.invalid/sourcesUrlTest.properties"));
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("property url http://sourcetest.invalid/sourcesUrlTest.properties not found.", e.getMessage());
			assertTrue(e.getCause() instanceof UnknownHostException);
		}
	}

	@SuppressWarnings("static-method")
	@Test public final void testNull()
	{
		try
		{
			load((URL)null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}
}
