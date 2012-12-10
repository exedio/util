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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.exedio.cope.junit.CopeAssert;

public class PropertiesFileTest extends CopeAssert
{
	private File file;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		file = File.createTempFile(PropertiesFileTest.class.getName(), ".properties");
	}

	@Override
	protected void tearDown() throws Exception
	{
		if(file.exists())
			StrictFile.delete(file);
		super.tearDown();
	}

	public void testIt() throws IOException
	{
		final java.util.Properties p = new java.util.Properties();
		p.setProperty("testKey1", "testValue1");
		p.setProperty("testKey2", "testValue2");
		store(p);

		final Properties.Source s = Properties.getSource(file);
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

	public void testNotExists()
	{
		StrictFile.delete(file);
		try
		{
			Properties.getSource(file);
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("property file " + file.getAbsolutePath() + " not found.", e.getMessage());
			assertTrue(e.getCause() instanceof FileNotFoundException);
		}
	}

	private void store(final java.util.Properties p) throws IOException
	{
		final FileOutputStream stream = new FileOutputStream(file);
		try
		{
			p.store(stream, null);
		}
		finally
		{
			stream.close();
		}
	}
}
