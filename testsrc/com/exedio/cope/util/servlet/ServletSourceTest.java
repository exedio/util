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

package com.exedio.cope.servletutil;

import static com.exedio.cope.servletutil.ServletSource.create;

import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.util.Properties.Source;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

public class ServletSourceTest extends CopeAssert
{
	public void testNormal()
	{
		final Source s = create(new TestContext("/testContextPath", "testContextPath."));
		assertKey(s);
		assertEquals("v1", s.get("p1"));
		assertEquals("v2", s.get("p2"));
		assertFails(s, "p3", "testContextPath.p3");
		assertFails(s, "top", "testContextPath.top");
		assertEquals("/testContextPath", s.get("contextPath"));
		assertEqualsUnmodifiable(list("contextPath", "p1", "p2"), s.keySet());
		assertSame(s, s.reload());
		assertEquals("ServletContext '/testContextPath' (prefix testContextPath.)", s.getDescription());
		assertEquals("ServletContext '/testContextPath' (prefix testContextPath.)", s.toString());
	}

	public void testRoot()
	{
		final Source s = create(new TestContext("", "root."));
		assertKey(s);
		assertEquals("v1", s.get("p1"));
		assertEquals("v2", s.get("p2"));
		assertFails(s, "p3", "root.p3");
		assertFails(s, "top", "root.top");
		assertEquals("", s.get("contextPath"));
		assertEqualsUnmodifiable(list("contextPath", "p1", "p2"), s.keySet());
		assertSame(s, s.reload());
		assertEquals("ServletContext '' (prefix root.)", s.getDescription());
		assertEquals("ServletContext '' (prefix root.)", s.toString());
	}

	public void testWithoutSlash()
	{
		final Source s = create(new TestContext("ding", "ding."));
		assertKey(s);
		assertEquals("v1", s.get("p1"));
		assertEquals("v2", s.get("p2"));
		assertFails(s, "p3", "ding.p3");
		assertFails(s, "top", "ding.top");
		assertEquals("ding", s.get("contextPath"));
		assertEqualsUnmodifiable(list("contextPath", "p1", "p2"), s.keySet());
		assertSame(s, s.reload());
		assertEquals("ServletContext 'ding' (prefix ding.)", s.getDescription());
		assertEquals("ServletContext 'ding' (prefix ding.)", s.toString());
	}

	public void testNull()
	{
		final Source s = create(new TestContext(null, ""));
		assertKey(s);
		assertEquals("v1", s.get("p1"));
		assertEquals("v2", s.get("p2"));
		assertFails(s, "p3", "p3");
		assertEquals("vtop", s.get("top"));
		assertEquals(null, s.get("contextPath"));
		assertEqualsUnmodifiable(list("contextPath", "p1", "p2", "top"), s.keySet());
		assertSame(s, s.reload());
		assertEquals("ServletContext 'null'", s.getDescription());
		assertEquals("ServletContext 'null'", s.toString());
	}

	private static void assertFails(final Source source, final String key, final String failureKey)
	{
		try
		{
			source.get(key);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(failureKey, e.getMessage());
		}
	}

	private static class TestContext extends AssertionFailedServletContext
	{
		private final String contextPath;
		private final String prefix;

		TestContext(final String contextPath, final String prefix)
		{
			this.contextPath = contextPath;
			this.prefix = prefix;
		}

		@Override
		public String getInitParameter(final String name)
		{
			if((prefix + "p1").equals(name))
				return "v1";
			else if((prefix + "p2").equals(name))
				return "v2";
			else if((prefix + "com.exedio.cope.servletutil.ServletSource.propertiesFile").equals(name))
				return null;
			else if("top".equals(name))
				return "vtop";
			else
				throw new IllegalArgumentException(name);
		}

		@Override
		public Enumeration<String> getInitParameterNames()
		{
			//noinspection UseOfObsoleteCollectionType
			return new Vector<>(Arrays.asList(prefix+"p1", prefix+"p2", "top")).elements();
		}

		@Override
		public String getContextPath()
		{
			return contextPath;
		}
	}
}
