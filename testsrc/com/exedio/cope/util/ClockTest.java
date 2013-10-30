/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.junit.CopeAssert.assertWithin;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

import org.junit.Test;

import java.util.Date;

public class ClockTest
{
	@Test public void testIt()
	{
		final Clock c = new Clock();

		{
			final Date before = new Date();
			final long date = c.currentTimeMillis();
			final Date after = new Date();
			assertWithin(before, after, new Date(date));
		}

		try
		{
			c.setSource(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("source", e.getMessage());

			final Date before = new Date();
			final long date = c.currentTimeMillis();
			final Date after = new Date();
			assertWithin(before, after, new Date(date));
		}

		final MockSource ms = new MockSource();
		c.setSource(ms);
		assertEquals(444, c.currentTimeMillis());
		assertEquals(1, ms.currentTimeMillisCount);

		try
		{
			c.setSource(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("source", e.getMessage());
		}
		assertEquals(444, c.currentTimeMillis());
		assertEquals(2, ms.currentTimeMillisCount);

		c.removeSource();
		{
			final Date before = new Date();
			final long date = c.currentTimeMillis();
			final Date after = new Date();
			assertWithin(before, after, new Date(date));
		}

		c.removeSource();
		{
			final Date before = new Date();
			final long date = c.currentTimeMillis();
			final Date after = new Date();
			assertWithin(before, after, new Date(date));
		}
	}

	private static final class MockSource implements Clock.Source
	{
		int currentTimeMillisCount = 0;

		MockSource()
		{
			// just make package private
		}

		public long currentTimeMillis()
		{
			currentTimeMillisCount++;
			return 444;
		}
	}
}
