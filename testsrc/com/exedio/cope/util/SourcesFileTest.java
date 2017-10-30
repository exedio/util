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

import static com.exedio.cope.junit.Assert.assertFails;
import static com.exedio.cope.junit.CopeAssert.assertContains;
import static com.exedio.cope.junit.CopeAssert.assertUnmodifiable;
import static com.exedio.cope.util.Sources.load;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.util.Properties.Source;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.TemporaryFolder;

@ExtendWith(TemporaryFolder.Extension.class)
public class SourcesFileTest
{
	@Test final void testIt(final TemporaryFolder folder) throws IOException
	{
		final File file = folder.newFile("file");
		final Properties p = new Properties();
		p.setProperty("testKey1", "testValue1");
		p.setProperty("testKey2", "testValue2");
		store(p, file);

		final Source s = load(file);
		assertFails(() ->
			s.get(null),
			NullPointerException.class, "key");
		assertFails(() ->
			s.get(""),
			IllegalArgumentException.class,
			"key must not be empty");
		assertEquals(null, s.get("xxx"));
		assertEquals("testValue1", s.get("testKey1"));
		assertEquals("testValue2", s.get("testKey2"));
		assertContains("testKey1", "testKey2", s.keySet());
		assertUnmodifiable(s.keySet());
		assertEquals(file.getAbsolutePath(), s.getDescription());
		assertEquals(file.getAbsolutePath(), s.toString());
	}

	@Test final void testNotExists(final TemporaryFolder folder) throws IOException
	{
		final File file = folder.newFile("file");
		StrictFile.delete(file);
		assertFails(() ->
			load(file),
			RuntimeException.class,
			"property file " + file.getAbsolutePath() + " not found.",
			FileNotFoundException.class);
	}

	private static void store(final Properties p, final File file) throws IOException
	{
		try(FileOutputStream s = new FileOutputStream(file))
		{
			p.store(s, null);
		}
	}
}
