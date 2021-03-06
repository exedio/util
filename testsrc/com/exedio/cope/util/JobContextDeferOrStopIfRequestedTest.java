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
import static java.time.Duration.ZERO;
import static java.time.Duration.ofNanos;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Duration;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class JobContextDeferOrStopIfRequestedTest
{
	@Test void testPositive()
	{
		final JC ctx = new JC();
		ctx.requestsDeferral = ofNanos(1);
		deferOrStopIfRequested(ctx);
		ctx.assertIt(
				"stopIfRequested",
				"requestsDeferral",
				"sleepAndStopIfRequested(PT0.000000001S)",
				"stopIfRequested");
	}

	@Test void testZero()
	{
		final JC ctx = new JC();
		ctx.requestsDeferral = ZERO;
		deferOrStopIfRequested(ctx);
		ctx.assertIt(
				"stopIfRequested",
				"requestsDeferral");
	}

	@Test void testNegative()
	{
		final JC ctx = new JC();
		ctx.requestsDeferral = ofNanos(-1);
		deferOrStopIfRequested(ctx);
		ctx.assertIt(
				"stopIfRequested",
				"requestsDeferral");
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
			assertNotNull(requestsDeferral);
			final Duration result = requestsDeferral;
			requestsDeferral = null;
			return result;
		}

		@Override
		public void sleepAndStopIfRequested(final Duration duration)
		{
			actual.add("sleepAndStopIfRequested(" + duration + ")");
		}
	}


	@Test void testContextNull()
	{
		assertFails(() ->
			deferOrStopIfRequested(null),
			NullPointerException.class, "ctx");
	}
}
