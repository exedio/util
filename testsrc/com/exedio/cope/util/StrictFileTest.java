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

import static com.exedio.cope.util.StrictFile.delete;
import static com.exedio.cope.util.StrictFile.mkdir;
import static com.exedio.cope.util.StrictFile.mkdirs;
import static com.exedio.cope.util.StrictFile.renameTo;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.exedio.cope.junit.CopeAssert;

public class StrictFileTest extends CopeAssert
{
	File f;

	@Before public final void setUp() throws IOException
	{
		f = File.createTempFile(StrictFileTest.class.getName(), "tmp");
	}

	@After public final void tearDown()
	{
		if(f.exists())
			delete(f);
	}

	@Test public final void testDelete()
	{
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

	@Test public final void testMkdir()
	{
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

	@Test public final void testMkdirs()
	{
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

	@Test public final void testRenameTo()
	{
		final File f2 = new File(f.getAbsolutePath()+"-rename");
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
		delete(f2);
	}
}
