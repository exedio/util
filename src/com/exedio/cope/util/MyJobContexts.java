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

package com.exedio.project.feature.util;

import static java.util.Objects.requireNonNull;

import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.ProxyJobContext;
import java.time.Duration;
import java.util.function.LongSupplier;

public final class MyJobContexts
{
	public static JobContext deferToProgress(
			final JobContext target,
			final Duration durationPerProgress)
	{
		return deferToProgress(target, durationPerProgress, System::nanoTime);
	}

	private static final Duration DEFERRAL_LIMIT = Duration.ofMinutes(1);

	static JobContext deferToProgress(
			final JobContext target,
			final Duration durationPerProgress,
			final LongSupplier now)
	{
		requireNonNull(durationPerProgress, "durationPerProgress");
		if(durationPerProgress.isZero())
			return target;
		if(durationPerProgress.isNegative())
			throw new IllegalArgumentException("durationPerProgress must be greater or equal zero, but was " + durationPerProgress);
		// plausibility check
		if(durationPerProgress.compareTo(DEFERRAL_LIMIT)>0)
			throw new IllegalArgumentException("durationPerProgress must be less or equal " + DEFERRAL_LIMIT + ", but was " + durationPerProgress);

		return new DeferToProgress(
				target, now,
				durationPerProgress.toNanos()); // checks whether fits into nanos
	}

	private static final class DeferToProgress extends ProxyJobContext
	{
		private final LongSupplier now;
		private final long nanosPerProgress;

		private boolean noProgressYet = true;
		private long firstProgressNanos = Long.MAX_VALUE;
		private long progress = 0;

		private DeferToProgress(
				final JobContext target,
				final LongSupplier now,
				final long nanosPerProgress)
		{
			super(target);
			this.now = now;
			this.nanosPerProgress = nanosPerProgress;
		}

		@Override
		public Duration requestsDeferral()
		{
			return max(
					super.requestsDeferral(),
					requestsDeferralMine());
		}

		private Duration requestsDeferralMine()
		{
			if(noProgressYet)
				return Duration.ZERO;

			// TODO will fail for long durations
			final long passedNanos = Math.subtractExact(now.getAsLong(), firstProgressNanos);
			final long requiredNanos = Math.multiplyExact(progress, nanosPerProgress);
			final long nanosToWait = Math.subtractExact(requiredNanos, passedNanos);
			if(nanosToWait<=0)
				return Duration.ZERO;

			return Duration.ofNanos(nanosToWait);
		}

		@Override
		public void incrementProgress()
		{
			super.incrementProgress();
			handleFirstProgress();
			progress++;
		}

		@Override
		public void incrementProgress(final int delta)
		{
			super.incrementProgress(delta);
			handleFirstProgress();
			progress += delta;
		}

		private void handleFirstProgress()
		{
			if(noProgressYet)
			{
				firstProgressNanos = now.getAsLong();
				noProgressYet = false;
			}
		}
	}


	private MyJobContexts()
	{
		// prevent instantiation
	}
}
