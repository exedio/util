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
import static java.util.Locale.ENGLISH;

import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.util.Clock;
import com.exedio.cope.util.Properties.Source;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

public class ServletSourceFileTest extends CopeAssert
{
	private File file;

	private String reloadDate;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z (Z)", ENGLISH);
		final long reloadMillis = 5555555;
		reloadDate = df.format(new Date(reloadMillis));
		Clock.override(() -> reloadMillis);
	}

	@Override
	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
	protected void tearDown() throws Exception
	{
		Clock.clearOverride();

		if(file!=null)
		{
			file.delete();
			file = null;
		}

		super.tearDown();
	}

	public void testNormal()
	{
		final Source s = create(new TestContext("/testContextPath", "testContextPath.", file()));
		assertKey(s);
		assertEquals("v1", s.get("p1"));
		assertEquals("v2", s.get("p2"));
		assertEquals(null, s.get("p3"));
		assertEquals(null, s.get("top"));
		assertEquals("/testContextPath", s.get("contextPath"));
		assertContainsUnmodifiable("p1", "p2", "contextPath", s.keySet());
		final Source r = s.reload();
		assertNotSame(s, r);
		assertEquals(file.getAbsolutePath() + "(" + reloadDate + ")", s.getDescription());
		assertEquals(file.getAbsolutePath() + "(" + reloadDate + " reload 1)", r.getDescription());
		assertEquals(file.getAbsolutePath() + "(reloadable)", s.toString());
		assertEquals(file.getAbsolutePath() + "(reloadable)", r.toString());
	}

	public void testRoot()
	{
		final Source s = create(new TestContext("", "root.", file()));
		assertKey(s);
		assertEquals("v1", s.get("p1"));
		assertEquals("v2", s.get("p2"));
		assertEquals(null, s.get("p3"));
		assertEquals(null, s.get("top"));
		assertEquals("", s.get("contextPath"));
		assertContainsUnmodifiable("p1", "p2", "contextPath", s.keySet());
		final Source r = s.reload();
		assertNotSame(s, r);
		assertEquals(file.getAbsolutePath() + "(" + reloadDate + ")", s.getDescription());
		assertEquals(file.getAbsolutePath() + "(" + reloadDate + " reload 1)", r.getDescription());
		assertEquals(file.getAbsolutePath() + "(reloadable)", s.toString());
		assertEquals(file.getAbsolutePath() + "(reloadable)", r.toString());
	}

	public void testWithoutSlash()
	{
		final Source s = create(new TestContext("ding", "ding.", file()));
		assertKey(s);
		assertEquals("v1", s.get("p1"));
		assertEquals("v2", s.get("p2"));
		assertEquals(null, s.get("p3"));
		assertEquals(null, s.get("top"));
		assertEquals("ding", s.get("contextPath"));
		assertContainsUnmodifiable("p1", "p2", "contextPath", s.keySet());
		final Source r = s.reload();
		assertNotSame(s, r);
		assertEquals(file.getAbsolutePath() + "(" + reloadDate + ")", s.getDescription());
		assertEquals(file.getAbsolutePath() + "(" + reloadDate + " reload 1)", r.getDescription());
		assertEquals(file.getAbsolutePath() + "(reloadable)", s.toString());
		assertEquals(file.getAbsolutePath() + "(reloadable)", r.toString());
	}

	public void testNull()
	{
		final Source s = create(new TestContext(null, "", file()));
		assertKey(s);
		assertEquals("v1", s.get("p1"));
		assertEquals("v2", s.get("p2"));
		assertEquals(null, s.get("p3"));
		assertEquals(null, s.get("top"));
		assertEquals(null, s.get("contextPath"));
		assertContainsUnmodifiable("p1", "p2", "contextPath", s.keySet());
		final Source r = s.reload();
		assertNotSame(s, r);
		assertEquals(file.getAbsolutePath() + "(" + reloadDate + ")", s.getDescription());
		assertEquals(file.getAbsolutePath() + "(" + reloadDate + " reload 1)", r.getDescription());
		assertEquals(file.getAbsolutePath() + "(reloadable)", s.toString());
		assertEquals(file.getAbsolutePath() + "(reloadable)", r.toString());
	}

	private File file()
	{
		assertNull(file);
		final Properties props = new Properties();
		props.setProperty("p1", "v1");
		props.setProperty("p2", "v2");
		try
		{
			file = File.createTempFile(ServletSourceFileTest.class.getName(), ".properties");
			try(FileOutputStream out = new FileOutputStream(file))
			{
				props.store(out, null);
			}
			return file;
		}
		catch(final IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	private static class TestContext extends AssertionFailedServletContext
	{
		private final String contextPath;
		private final String prefix;
		private final File file;

		TestContext(final String contextPath, final String prefix, final File file)
		{
			this.contextPath = contextPath;
			this.prefix = prefix;
			this.file = file;
		}

		@Override
		public String getInitParameter(final String name)
		{
			if((prefix + "com.exedio.cope.servletutil.ServletSource.propertiesFile").equals(name))
				return file.getAbsolutePath();
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
