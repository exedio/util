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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SourcesFileTest extends CopeAssert
{
	private File file;

	@Before public final void setUp() throws IOException
	{
		file = File.createTempFile(SourcesFileTest.class.getName(), ".properties");
	}

	@After public final void tearDown()
	{
		if(file.exists())
			StrictFile.delete(file);
	}

	@Test public final void testIt() throws IOException
	{
		final Properties p = new Properties();
		p.setProperty("testKey1", "testValue1");
		p.setProperty("testKey2", "testValue2");
		store(p);

		final Source s = load(file);
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
		assertEquals(file.getAbsolutePath(), s.getDescription());
		assertEquals(file.getAbsolutePath(), s.toString());
	}

	@Test public final void testNotExists()
	{
		StrictFile.delete(file);
		try
		{
			load(file);
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("property file " + file.getAbsolutePath() + " not found.", e.getMessage());
			assertTrue(e.getCause() instanceof FileNotFoundException);
		}
	}

	private void store(final Properties p) throws IOException
	{
		try(FileOutputStream s = new FileOutputStream(file))
		{
			p.store(s, null);
		}
	}
}
