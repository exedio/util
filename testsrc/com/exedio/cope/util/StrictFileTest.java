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
import static com.exedio.cope.util.StrictFile.createNewFile;
import static com.exedio.cope.util.StrictFile.delete;
import static com.exedio.cope.util.StrictFile.mkdir;
import static com.exedio.cope.util.StrictFile.mkdirs;
import static com.exedio.cope.util.StrictFile.renameTo;
import static com.exedio.cope.util.StrictFile.setLastModified;
import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.TemporaryFolder;

@ExtendWith(TemporaryFolder.Extension.class)
@SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
public class StrictFileTest
{
	@Test void testCreateNewFile(final TemporaryFolder folder) throws IOException
	{
		final File f = new File(folder.getRoot(), "file");
		assertEquals(false, f.exists());

		createNewFile(f);
		assertEquals(true, f.exists());
	}

	@Test void testCreateNewFileFails(final TemporaryFolder folder) throws IOException
	{
		final File f = folder.newFile("file");
		assertEquals(true, f.exists());

		assertFails(() ->
			createNewFile(f),
			IllegalStateException.class, f.getAbsolutePath());
		assertEquals(true, f.exists());
	}

	@Test void testCreateNewFileNull()
	{
		//noinspection ConstantConditions
		assertFails(
				() -> createNewFile(null),
				NullPointerException.class, null);
	}

	@Test final void testDelete(final TemporaryFolder folder) throws IOException
	{
		final File f = folder.newFile("file");
		delete(f);
		assertFails(() ->
			delete(f),
			IllegalStateException.class, f.getAbsolutePath());
	}

	@Test final void testDeleteNull()
	{
		//noinspection ConstantConditions
		assertFails(
				() -> delete(null),
				NullPointerException.class, null);
	}

	@Test final void testMkdir(final TemporaryFolder folder) throws IOException
	{
		final File f = folder.newFile("file");
		delete(f);
		mkdir(f);
		assertFails(() ->
			mkdir(f),
			IllegalStateException.class, f.getAbsolutePath());
	}

	@Test final void testMkdirNull()
	{
		//noinspection ConstantConditions
		assertFails(
				() -> mkdir(null),
				NullPointerException.class, null);
	}

	@Test final void testMkdirs(final TemporaryFolder folder) throws IOException
	{
		final File f = folder.newFile("file");
		delete(f);
		mkdirs(f);
		assertFails(() ->
			mkdirs(f),
			IllegalStateException.class, f.getAbsolutePath());
	}

	@Test final void testMkdirsNull()
	{
		//noinspection ConstantConditions
		assertFails(
				() -> mkdirs(null),
				NullPointerException.class, null);
	}

	@Test final void testRenameTo(final TemporaryFolder folder) throws IOException
	{
		final File f = folder.newFile("file");
		final File f2 = new File(folder.getRoot(), "file2");
		renameTo(f, f2);
		assertFails(() ->
			renameTo(f, f2),
			IllegalStateException.class, f.getAbsolutePath());
	}

	@Test final void testRenameToNullBoth()
	{
		//noinspection ConstantConditions
		assertFails(
				() -> renameTo(null, null),
				NullPointerException.class, null);
	}

	@Test final void testRenameToNullSource()
	{
		//noinspection ConstantConditions
		assertFails(
				() -> renameTo(null, new File(".")),
				NullPointerException.class, null);
	}

	@Test final void testRenameToNullDest()
	{
		assertFails(
				() -> renameTo(new File("."), null),
				NullPointerException.class, null);
	}

	@Test void testSetLastModified(final TemporaryFolder folder) throws IOException
	{
		final File f = folder.newFile("file");
		assertEquals(true, f.exists());

		setLastModified(f, 555000);
		assertEquals(true, f.exists());
		assertEquals(555000, f.lastModified());
	}

	@Test void testSetLastModifiedZero(final TemporaryFolder folder) throws IOException
	{
		final File f = folder.newFile("file");
		assertEquals(true, f.exists());
		setLastModified(f, 555000);
		assertEquals(555000, f.lastModified());

		setLastModified(f, 0);
		assertEquals(true, f.exists());
		assertEquals(0, f.lastModified());
	}

	@Test void testSetLastModifiedFails(final TemporaryFolder folder)
	{
		final File f = new File(folder.getRoot(), "file");
		assertEquals(false, f.exists());

		assertFails(() ->
			setLastModified(f, 555000),
			IllegalStateException.class, f.getAbsolutePath());
		assertEquals(false, f.exists());
	}

	@Test void testSetLastModifiedNull()
	{
		//noinspection ConstantConditions
		assertFails(
				() -> setLastModified(null, 555000),
				NullPointerException.class, null);
	}

	@Test void testSetLastModifiedNegative(final TemporaryFolder folder) throws IOException
	{
		final File f = folder.newFile("file");
		assertEquals(true, f.exists());
		setLastModified(f, 555000);
		assertEquals(555000, f.lastModified());

		assertFails(
				() -> setLastModified(f, -1),
				IllegalArgumentException.class,
				"Negative time");
		assertEquals(555000, f.lastModified());
	}
}
