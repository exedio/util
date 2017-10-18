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

import static com.exedio.cope.util.JobContexts.sleepAndStopIfRequestedPolling;
import static java.lang.Long.MIN_VALUE;
import static java.time.Duration.ZERO;
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofNanos;
import static java.util.Arrays.fill;
import static java.util.Arrays.stream;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class JobContextSleepPollingNondeterministicTest
{
	@BeforeAll static void warmup()
	{
		final JC ctx = new JC();
		final Duration duration = ofMillis(1);
		for(int i = 0; i<500; i++)
			sleepAndStopIfRequestedPolling(ctx, duration);
	}

	@Test void testZero()
	{
		final JC ctx = new JC();
		sleepAndStopIfRequestedPolling(ctx, ZERO);
		ctx.assertIt(0);
	}

	@Test void testNegative()
	{
		final JC ctx = new JC();
		sleepAndStopIfRequestedPolling(ctx, ofMillis(-100));
		ctx.assertIt(0);
	}

	@Test void test50Micros()
	{
		final JC ctx = new JC();
		sleepAndStopIfRequestedPolling(ctx, ofNanos(50_000));
		ctx.assertIt(0);
	}

	@Test void testAlmost1Milli()
	{
		final JC ctx = new JC();
		sleepAndStopIfRequestedPolling(ctx, ofNanos(999_999));
		ctx.assertIt(0);
	}

	@Test void test1Milli()
	{
		final JC ctx = new JC();
		sleepAndStopIfRequestedPolling(ctx, ofMillis(1));
		ctx.assertIt(0, 1);
	}

	@Test void test2Millis()
	{
		final JC ctx = new JC();
		sleepAndStopIfRequestedPolling(ctx, ofMillis(2));
		ctx.assertIt(0, 2);
	}

	@Test void test50Millis()
	{
		final JC ctx = new JC();
		sleepAndStopIfRequestedPolling(ctx, ofMillis(50));
		ctx.assertIt(0, 50);
	}

	@Test void test150Millis()
	{
		final JC ctx = new JC();
		sleepAndStopIfRequestedPolling(ctx, ofMillis(150));
		ctx.assertIt(0, 100, 149);
	}

	@Test void test250Millis()
	{
		final JC ctx = new JC();
		sleepAndStopIfRequestedPolling(ctx, ofMillis(250));
		ctx.assertIt(0, 100, 200, 249);
	}

	@Test void test350Millis()
	{
		final JC ctx = new JC();
		sleepAndStopIfRequestedPolling(ctx, ofMillis(350));
		ctx.assertIt(0, 100, 200, 300, 349);
	}

	private static final class JC extends AssertionErrorJobContext
	{
		private final long[] actual = new long[5000];
		{
			fill(actual, MIN_VALUE);
		}
		private int actualLimit = 0;

		private final long created = System.nanoTime();

		void assertIt(final long... expected)
		{
			assertEquals(
					stream(expected).
							boxed().collect(Collectors.toList()),
					stream(actual).limit(actualLimit).map(l -> (l - created)/1_000_000).
							boxed().collect(Collectors.toList()),
					"may fail rarely due to race conditions");

			fill(actual, MIN_VALUE);
			actualLimit = 0;
		}

		@Override
		public void stopIfRequested()
		{
			actual[actualLimit++] = System.nanoTime();
		}
	}
}
