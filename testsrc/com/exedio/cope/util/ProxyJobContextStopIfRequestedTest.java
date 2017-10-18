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

import static com.exedio.cope.util.JobContext.deferOrStopIfRequested;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Duration;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProxyJobContextStopIfRequestedTest
{
	private JC target;
	private ProxyJobContext c;
	@BeforeEach void before()
	{
		target = new JC();
		c = new ProxyJobContext(target)
		{
			@Override
			public void stopIfRequested()
			{
				target.actual.add("stopIfRequested-proxy");
				super.stopIfRequested();
				target.actual.add("stopIfRequested-/proxy");
			}

			@Override
			public void sleepAndStopIfRequested(final Duration duration)
			{
				target.actual.add("sleepAndStopIfRequested(" + duration + ")-proxy");
				super.sleepAndStopIfRequested(duration);
				target.actual.add("sleepAndStopIfRequested(" + duration + ")-/proxy");
			}
		};
	}

	@Test void testStopIfRequested()
	{
		c.stopIfRequested();
		target.assertIt(
				"stopIfRequested-proxy",
				"stopIfRequested",
				"stopIfRequested-/proxy");
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
				"stopIfRequested-proxy",
				"stopIfRequested",
				"stopIfRequested-/proxy",
				"requestsDeferral",
				"sleepAndStopIfRequested(PT0.000000001S)-proxy",
				"sleepAndStopIfRequested(PT0.000000001S)",
				"sleepAndStopIfRequested(PT0.000000001S)-/proxy",
				"stopIfRequested-proxy",
				"stopIfRequested",
				"stopIfRequested-/proxy");
	}
	@Test void testDeferOrStopIfRequestedZero()
	{
		target.requestsDeferral = Duration.ZERO;
		deferOrStopIfRequested(c);
		target.assertIt(
				"stopIfRequested-proxy",
				"stopIfRequested",
				"stopIfRequested-/proxy",
				"requestsDeferral");
	}
	@Test void testDeferOrStopIfRequestedNegative()
	{
		target.requestsDeferral = Duration.ofNanos(-1);
		deferOrStopIfRequested(c);
		target.assertIt(
				"stopIfRequested-proxy",
				"stopIfRequested",
				"stopIfRequested-/proxy",
				"requestsDeferral");
	}
	@Test void testSleepAndStopIfRequested()
	{
		c.sleepAndStopIfRequested(Duration.ofSeconds(44));
		target.assertIt(
				"sleepAndStopIfRequested(PT44S)-proxy",
				"sleepAndStopIfRequested(PT44S)",
				"sleepAndStopIfRequested(PT44S)-/proxy");
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
	}
}
