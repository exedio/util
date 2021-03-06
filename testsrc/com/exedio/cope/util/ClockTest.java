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
import static com.exedio.cope.junit.CopeAssert.assertWithin;
import static com.exedio.cope.util.Clock.currentTimeMillis;
import static com.exedio.cope.util.Clock.newDate;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.util.junit.ClockRule;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ClockRule.Extension.class)
public class ClockTest
{
	@Test void overrideNull(final ClockRule clock)
	{
		assertClear();

		assertFails(() ->
			clock.override(null),
			NullPointerException.class, "strategy");
		assertClear();
	}

	@Test void testIt(final ClockRule clock)
	{
		final MockStrategy ms = new MockStrategy();
		clock.override(ms);
		assertEquals(444, currentTimeMillis());
		assertEquals(1, ms.currentTimeMillisCount);

		assertEquals(444, currentTimeMillis());
		assertEquals(2, ms.currentTimeMillisCount);

		assertEquals(new Date(444), newDate());
		assertEquals(3, ms.currentTimeMillisCount);

		clock.clear();
		assertClear();
	}

	@Test void clearNotNeeded(final ClockRule clock)
	{
		assertClear();

		clock.clear();
		assertClear();
	}

	private static final class MockStrategy implements Clock.Strategy
	{
		int currentTimeMillisCount = 0;

		MockStrategy()
		{
			// just make package private
		}

		@Override
		public long currentTimeMillis()
		{
			currentTimeMillisCount++;
			return 444;
		}
	}

	private static void assertClear()
	{
		{
			final Date before = new Date();
			final long date = currentTimeMillis();
			final Date after = new Date();
			assertWithin(before, after, new Date(date));
		}
		{
			final Date before = new Date();
			final Date date = newDate();
			final Date after = new Date();
			assertWithin(before, after, date);
		}
	}
}
