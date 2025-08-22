/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package com.exedio.cope.util;

import static com.exedio.cope.junit.Assert.assertFails;
import static com.exedio.cope.util.JobContext.deferOrStopIfRequested;
import static com.exedio.cope.util.JobContexts.deferToProgress;
import static java.time.Duration.ZERO;
import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofNanos;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.Duration;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JobContextsDeferToProgressTest
{
	@Test void testLimit1()
	{
		final JC target = new JC();
		final JobContext ctx = deferToProgress(target, ofNanos(1), () -> now);
		target.assertIt();
		assertDeferral(target, ctx, ZERO);
		assertDeferral(target, ctx, ZERO);

		ctx.incrementProgress();
		target.assertIt("progress");
		assertDeferral(target, ctx, ofNanos(1));
		assertDeferral(target, ctx, ofNanos(1));

		ctx.incrementProgress();
		target.assertIt("progress");
		assertDeferral(target, ctx, ofNanos(2));
		assertDeferral(target, ctx, ofNanos(2));
	}

	@Test void testLimit1Delta()
	{
		final JC target = new JC();
		final JobContext ctx = deferToProgress(target, ofNanos(1), () -> now);
		target.assertIt();
		assertDeferral(target, ctx, ZERO);
		assertDeferral(target, ctx, ZERO);

		ctx.incrementProgress(33);
		target.assertIt("progress(33)");
		assertDeferral(target, ctx, ofNanos(33));
		assertDeferral(target, ctx, ofNanos(33));

		ctx.incrementProgress(44);
		target.assertIt("progress(44)");
		assertDeferral(target, ctx, ofNanos(77));
		assertDeferral(target, ctx, ofNanos(77));

		now = 22;
		assertDeferral(target, ctx, ofNanos(55));

		now = 75;
		assertDeferral(target, ctx, ofNanos(2));

		now = 76;
		assertDeferral(target, ctx, ofNanos(1));

		now = 77;
		assertDeferral(target, ctx, ZERO);

		now = 78;
		assertDeferral(target, ctx, ZERO);

		now = 79;
		assertDeferral(target, ctx, ZERO);

		now = 1000;
		assertDeferral(target, ctx, ZERO);
	}

	@Test void testLimit44()
	{
		final JC target = new JC();
		final JobContext ctx = deferToProgress(target, ofNanos(44), () -> now);
		target.assertIt();
		assertDeferral(target, ctx, ZERO);

		ctx.incrementProgress();
		target.assertIt("progress");
		assertDeferral(target, ctx, ofNanos(44));

		ctx.incrementProgress();
		target.assertIt("progress");
		assertDeferral(target, ctx, ofNanos(88));


		now = 43;
		assertDeferral(target, ctx, ofNanos(45));

		now = 44;
		assertDeferral(target, ctx, ofNanos(44));

		now = 45;
		assertDeferral(target, ctx, ofNanos(43));


		now = 87;
		assertDeferral(target, ctx, ofNanos(1));

		now = 88;
		assertDeferral(target, ctx, ZERO);

		now = 89;
		assertDeferral(target, ctx, ZERO);
	}

	@Test void testLimit44Delta()
	{
		final JC target = new JC();
		final JobContext ctx = deferToProgress(target, ofNanos(44), () -> now);
		target.assertIt();
		assertDeferral(target, ctx, ZERO);

		ctx.incrementProgress(10);
		target.assertIt("progress(10)");
		assertDeferral(target, ctx, ofNanos(440));

		now = 1;
		assertDeferral(target, ctx, ofNanos(439));


		now = 109;
		assertDeferral(target, ctx, ofNanos(331));

		now = 110;
		assertDeferral(target, ctx, ofNanos(330));

		now = 111;
		assertDeferral(target, ctx, ofNanos(329));


		now = 219;
		assertDeferral(target, ctx, ofNanos(221));

		now = 220;
		assertDeferral(target, ctx, ofNanos(220));

		now = 221;
		assertDeferral(target, ctx, ofNanos(219));


		now = 329;
		assertDeferral(target, ctx, ofNanos(111));

		now = 330;
		assertDeferral(target, ctx, ofNanos(110));

		now = 331;
		assertDeferral(target, ctx, ofNanos(109));


		now = 439;
		assertDeferral(target, ctx, ofNanos(1));

		now = 440;
		assertDeferral(target, ctx, ZERO);

		now = 441;
		assertDeferral(target, ctx, ZERO);
	}

	@Test void testTargetDefers()
	{
		final JC target = new JC();
		final JobContext ctx = deferToProgress(target, ofNanos(44), () -> now);
		target.assertIt();
		assertDeferral(target, ctx, ZERO);

		target.requestsDeferral = ofNanos(22);
		target.assertIt();
		assertDeferral(target, ctx, ofNanos(22));

		ctx.incrementProgress();
		target.assertIt("progress");
		assertDeferral(target, ctx, ofNanos(44));

		target.requestsDeferral = ofNanos(55);
		target.assertIt();
		assertDeferral(target, ctx, ofNanos(55));
	}

	private static void assertDeferral(
			final JC target,
			final JobContext ctx,
			final Duration duration)
	{
		deferOrStopIfRequested(ctx);
		if(duration.isZero())
			target.assertIt(
					"stop",
					"requestsDeferral");
		else
			target.assertIt(
					"stop",
					"requestsDeferral",
					"sleep(" + duration + ")",
					"stop");
	}

	long now = 0;
	@BeforeEach void resetNow()
	{
		now = 0;
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
			actual.add("stop");
		}

		private Duration requestsDeferral = ZERO;
		@Override
		public Duration requestsDeferral()
		{
			actual.add("requestsDeferral");
			return requestsDeferral;
		}

		@Override
		public void sleepAndStopIfRequested(final Duration duration)
		{
			actual.add("sleep(" + duration + ")");
		}


		@Override
		public void incrementProgress()
		{
			actual.add("progress");
		}

		@Override
		public void incrementProgress(final int delta)
		{
			actual.add("progress(" + delta + ")");
		}
	}

	@Test void testTargetNull()
	{
		assertFails(
				() -> deferToProgress(null, ofNanos(1)),
				NullPointerException.class, "target");
	}
	@Test void testDurationNull()
	{
		assertFails(
				() -> deferToProgress(null, null),
				NullPointerException.class, "durationPerProgress");
	}
	@Test void testDurationZero()
	{
		final AssertionErrorJobContext target = new AssertionErrorJobContext();
		assertSame(target, deferToProgress(target, ZERO));
	}
	@Test void testDurationNegative()
	{
		assertFails(
				() -> deferToProgress(null, ofNanos(-1)),
				IllegalArgumentException.class,
				"durationPerProgress must be greater or equal zero, but was PT-0.000000001S");
	}
	@Test void testDurationLimitOk()
	{
		deferToProgress(new AssertionErrorJobContext(), ofMinutes(1));
	}
	@Test void testDurationLimitWrong()
	{
		assertFails(
				() -> deferToProgress(null, ofMinutes(1).plus(ofNanos(1))),
				IllegalArgumentException.class,
				"durationPerProgress must be less or equal PT1M, but was PT1M0.000000001S");
	}
}
