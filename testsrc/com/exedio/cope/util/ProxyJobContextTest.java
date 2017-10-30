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
import static com.exedio.cope.util.JobContext.deferOrStopIfRequested;
import static com.exedio.cope.util.JobContext.sleepAndStopIfRequested;
import static com.exedio.cope.util.ProxyJobContext.max;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.Duration;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProxyJobContextTest
{
	private JC target;
	private ProxyJobContext c;
	@BeforeEach void before()
	{
		target = new JC();
		c = new ProxyJobContext(target);
	}

	@Test void testStopIfRequested()
	{
		c.stopIfRequested();
		target.assertIt("stopIfRequested");
	}
	@Test void testRequestsDeferral()
	{
		target.requestsDeferral = Duration.ofSeconds(33);
		assertEquals(Duration.ofSeconds(33), c.requestsDeferral());
		target.assertIt("requestsDeferral");
	}
	@Test void testDeferOrStopIfRequestedPositive()
	{
		target.requestsDeferral = Duration.ofNanos(1);
		deferOrStopIfRequested(c);
		target.assertIt(
				"stopIfRequested",
				"requestsDeferral",
				"sleepAndStopIfRequested(PT0.000000001S)",
				"stopIfRequested");
	}
	@Test void testDeferOrStopIfRequestedZero()
	{
		target.requestsDeferral = Duration.ZERO;
		deferOrStopIfRequested(c);
		target.assertIt(
				"stopIfRequested",
				"requestsDeferral");
	}
	@Test void testDeferOrStopIfRequestedNegative()
	{
		target.requestsDeferral = Duration.ofNanos(-1);
		deferOrStopIfRequested(c);
		target.assertIt(
				"stopIfRequested",
				"requestsDeferral");
	}
	@Test void testSleepAndStopIfRequestedDirect()
	{
		c.sleepAndStopIfRequested(Duration.ofSeconds(44));
		target.assertIt("sleepAndStopIfRequested(PT44S)");
	}
	@Test void testSleepAndStopIfRequestedPositive()
	{
		sleepAndStopIfRequested(c, Duration.ofNanos(1));
		target.assertIt(
				"stopIfRequested",
				"sleepAndStopIfRequested(PT0.000000001S)",
				"stopIfRequested");
	}
	@Test void testSleepAndStopIfRequestedZero()
	{
		sleepAndStopIfRequested(c, Duration.ZERO);
		target.assertIt(
				"stopIfRequested");
	}
	@Test void testSleepAndStopIfRequestedNegative()
	{
		sleepAndStopIfRequested(c, Duration.ofNanos(-1));
		target.assertIt(
				"stopIfRequested");
	}
	@Test void testSupportsMessage()
	{
		assertEquals(false, c.supportsMessage());
		target.assertIt("supportsMessage");

		target.supportsMessage = true;
		assertEquals(true, c.supportsMessage());
		target.assertIt("supportsMessage");
	}
	@Test void testSupportsProgress()
	{
		assertEquals(false, c.supportsProgress());
		target.assertIt("supportsProgress");

		target.supportsProgress = true;
		assertEquals(true, c.supportsProgress());
		target.assertIt("supportsProgress");
	}
	@Test void testSupportsCompleteness()
	{
		assertEquals(false, c.supportsCompleteness());
		target.assertIt("supportsCompleteness");

		target.supportsCompleteness = true;
		assertEquals(true, c.supportsCompleteness());
		target.assertIt("supportsCompleteness");
	}
	@Test void testSetMessage()
	{
		c.setMessage("");
		target.assertIt("setMessage()");

		c.setMessage("myMessage");
		target.assertIt("setMessage(myMessage)");
	}
	@Test void testIncrementProgress()
	{
		c.incrementProgress();
		target.assertIt("incrementProgress");

		c.incrementProgress(5);
		target.assertIt("incrementProgress(5)");
	}
	@Test void testSetCompleteness()
	{
		c.setCompleteness(0.5);
		target.assertIt("setCompleteness(0.5)");
	}

	private static final class JC extends AssertionErrorJobContext
	{
		private final ArrayList<String> actual = new ArrayList<>();

		void assertIt(final String... expected)
		{
			assertEquals(asList(expected), actual);
			actual.clear();
		}

		@Override
		public void stopIfRequested()
		{
			actual.add("stopIfRequested");
		}

		Duration requestsDeferral;
		@Override
		public Duration requestsDeferral()
		{
			actual.add("requestsDeferral");
			assertNotNull(requestsDeferral, "requestsDeferral");
			return requestsDeferral;
		}

		@Override
		public void sleepAndStopIfRequested(final Duration duration)
		{
			actual.add("sleepAndStopIfRequested(" + duration + ")");
		}

		boolean supportsMessage = false;
		@Override
		public boolean supportsMessage()
		{
			actual.add("supportsMessage");
			return supportsMessage;
		}

		@Override
		public void setMessage(final String message)
		{
			actual.add("setMessage(" + message + ")");
		}

		boolean supportsProgress = false;
		@Override
		public boolean supportsProgress()
		{
			actual.add("supportsProgress");
			return supportsProgress;
		}

		@Override
		public void incrementProgress()
		{
			actual.add("incrementProgress");
		}

		@Override
		public void incrementProgress(final int delta)
		{
			actual.add("incrementProgress(" + delta + ")");
		}

		boolean supportsCompleteness = false;
		@Override
		public boolean supportsCompleteness()
		{
			actual.add("supportsCompleteness");
			return supportsCompleteness;
		}

		@Override
		public void setCompleteness(final double completeness)
		{
			actual.add("setCompleteness(" + completeness + ")");
		}
	}


	@SuppressWarnings("unused")
	@Test void testNull()
	{
		assertFails(() ->
			new ProxyJobContext(null),
			NullPointerException.class, "target");
	}
	@Test void testMax()
	{
		final Duration a  = Duration.ofNanos(1);
		final Duration b  = Duration.ofNanos(2);

		assertSame(b, max(a, b));
		assertSame(b, max(b, a));
		assertSame(a, max(a, a));
	}
	@Test void testMaxEquals()
	{
		final Duration a1 = Duration.ofNanos(1);
		final Duration a2 = Duration.ofNanos(1);
		assertNotSame(a1, a2);
		assertEquals(a1, a2);

		assertSame(a1, max(a1, a1));
		assertSame(a1, max(a1, a2));
		assertSame(a2, max(a2, a1));
	}
}
