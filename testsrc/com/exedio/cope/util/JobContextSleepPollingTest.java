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
import static com.exedio.cope.util.JobContexts.sleepAndStopIfRequestedPolling;
import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.Duration;
import org.junit.jupiter.api.Test;

public class JobContextSleepPollingTest
{
	@Test void testContextNull()
	{
		assertFails(() ->
			sleepAndStopIfRequestedPolling(null, null),
			NullPointerException.class, "ctx");
	}

	@Test void testDurationNull()
	{
		assertFails(() ->
			sleepAndStopIfRequestedPolling(new AssertionErrorJobContext(), null),
			NullPointerException.class, "duration");
	}

	@SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON")
	@Test void testDurationOverflow()
	{
		final ArithmeticException e = assertFails(() ->
			sleepAndStopIfRequestedPolling(
					new AssertionErrorJobContext() { @Override public void stopIfRequested() {} },
					Duration.ofDays(300*365)),
			ArithmeticException.class, "long overflow");
		final StackTraceElement ste = e.getStackTrace()[1];
		assertEquals("java.time.Duration", ste.getClassName());
		assertEquals("toNanos", ste.getMethodName());
	}

	@Test void testJobContextDefault()
	{
		final NullPointerException e = assertFails(() ->
			JobContexts.EMPTY.sleepAndStopIfRequested(null),
			NullPointerException.class, "duration");
		final StackTraceElement ste = e.getStackTrace()[1];
		assertEquals("com.exedio.cope.util.JobContexts", ste.getClassName());
		assertEquals("sleepAndStopIfRequestedPolling", ste.getMethodName());
	}
}
