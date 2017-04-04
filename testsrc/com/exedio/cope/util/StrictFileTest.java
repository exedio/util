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

import static com.exedio.cope.util.StrictFile.createNewFile;
import static com.exedio.cope.util.StrictFile.delete;
import static com.exedio.cope.util.StrictFile.mkdir;
import static com.exedio.cope.util.StrictFile.mkdirs;
import static com.exedio.cope.util.StrictFile.renameTo;
import static com.exedio.cope.util.StrictFile.setLastModified;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TemporaryFolder;

public class StrictFileTest
{
	private final TemporaryFolder folder = new TemporaryFolder();

	@SuppressFBWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
	@Rule public final RuleChain chain = RuleChain.outerRule(folder);

	@Test public void testCreateNewFile() throws IOException
	{
		final File f = new File(folder.getRoot(), "file");
		assertEquals(false, f.exists());

		createNewFile(f);
		assertEquals(true, f.exists());
	}

	@Test public void testCreateNewFileFails() throws IOException
	{
		final File f = folder.newFile("file");
		assertEquals(true, f.exists());

		try
		{
			createNewFile(f);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(f.getAbsolutePath(), e.getMessage());
		}
		assertEquals(true, f.exists());
	}

	@Test public final void testDelete() throws IOException
	{
		final File f = folder.newFile("file");
		delete(f);
		try
		{
			delete(f);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(f.getAbsolutePath(), e.getMessage());
		}
	}

	@Test public final void testMkdir() throws IOException
	{
		final File f = folder.newFile("file");
		delete(f);
		mkdir(f);
		try
		{
			mkdir(f);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(f.getAbsolutePath(), e.getMessage());
		}
	}

	@Test public final void testMkdirs() throws IOException
	{
		final File f = folder.newFile("file");
		delete(f);
		mkdirs(f);
		try
		{
			mkdirs(f);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(f.getAbsolutePath(), e.getMessage());
		}
	}

	@Test public final void testRenameTo() throws IOException
	{
		final File f = folder.newFile("file");
		final File f2 = new File(folder.getRoot(), "file2");
		renameTo(f, f2);
		try
		{
			renameTo(f, f2);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(f.getAbsolutePath(), e.getMessage());
		}
	}

	@Test public void testSetLastModified() throws IOException
	{
		final File f = folder.newFile("file");
		assertEquals(true, f.exists());

		setLastModified(f, 555000);
		assertEquals(true, f.exists());
		assertEquals(555000, f.lastModified());
	}

	@Test public void testSetLastModifiedFails()
	{
		final File f = new File(folder.getRoot(), "file");
		assertEquals(false, f.exists());

		try
		{
			setLastModified(f, 555000);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(f.getAbsolutePath(), e.getMessage());
		}
		assertEquals(false, f.exists());
	}
}
