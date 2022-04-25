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

import static java.util.Objects.requireNonNull;

import java.time.Duration;

/**
 * An interface for controlling long-running jobs.
 * Instances of this interface are not required to work
 * for multiple threads accessing the methods concurrently.
 *
 * This interface has been inspired by
 * <a href="https://www.sauronsoftware.it/projects/cron4j/api/it/sauronsoftware/cron4j/TaskExecutionContext.html">TaskExecutionContext</a>
 * of <a href="https://www.sauronsoftware.it/projects/cron4j">cron4j</a>.
 */
public interface JobContext
{
	/**
	 * Checks, whether the job has been requested to stop.
	 * The job should call this method in reasonably short
	 * intervals.
	 * If throws a {@link JobStop}, the job should stop
	 * gracefully as soon as possible,
	 * but all resources held should be released.
	 * <p>
	 * The job may call {@link #deferOrStopIfRequested(JobContext)} instead.
	 */
	void stopIfRequested() throws JobStop;

	/**
	 * Returns the duration the job has been requested to defer by its context.
	 * This method is considered by {@link #deferOrStopIfRequested(JobContext) deferOrStopIfRequested}.
	 * <p>
	 * In general the job should not call this method,
	 * but {@code deferOrStopIfRequested}.
	 * <p>
	 * The default implementation returns {@link Duration#ZERO zero}.
	 */
	default Duration requestsDeferral()
	{
		return Duration.ZERO;
	}

	/**
	 * Defers the job, if the job is {@link #requestsDeferral() requested} to defer
	 * by context {@code ctx}.
	 * The job should call this method in reasonably short
	 * intervals.
	 * The job should expect {@code deferOrStopIfRequested} to block for an unspecified amount of time.
	 * The job should not call {@code deferOrStopIfRequested} while holding valuable resources,
	 * in particular locks.
	 * <p>
	 * Additionally checks, whether the job has been requested to stop or
	 * becomes requested to stop while deferring and
	 * throws a {@link JobStop} in such cases.
	 * Therefore, by calling {@code deferOrStopIfRequested} the job fulfills its obligation
	 * to call {@link #stopIfRequested()}.
	 * <p>
	 * This method calls {@link #sleepAndStopIfRequested(Duration)} for actually sleeping.
	 */
	static void deferOrStopIfRequested(final JobContext ctx) throws JobStop
	{
		requireNonNull(ctx, "ctx");
		ctx.stopIfRequested();
		final Duration deferral = ctx.requestsDeferral();
		if(!deferral.isZero() && !deferral.isNegative())
		{
			ctx.sleepAndStopIfRequested(deferral);
			ctx.stopIfRequested();
		}
	}

	/**
	 * Sleeps for the specified duration.
	 * In contrast to {@link #deferOrStopIfRequested(JobContext)} this method actually waits
	 * for the specified amount of time.
	 * In contrast to {@link Thread#sleep(long) Thread.sleep} it aborts prematurely
	 * by throwing a {@link JobStop},
	 * if the job has been requested to stop or
	 * becomes requested to stop while sleeping.
	 * <p>
	 * In general the job should not call this method directly,
	 * but only {@link #sleepAndStopIfRequested(JobContext, Duration)}.
	 * Override this method for affecting (potentially long) sleeps.
	 * For instance the job might temporarily release resources.
	 * <p>
	 * Calling this method with {@code duration} zero or negative
	 * is equivalent to a call to {@link #stopIfRequested()}.
	 * <p>
	 * The default implementation
	 * <a href="https://en.wikipedia.org/wiki/Polling_(computer_science)">polls</a>
	 * {@link #stopIfRequested()} every 100 milliseconds.
	 * Implementers are encouraged to provide a more efficient implementation.
	 * The default implementation fails for {@code duration}s
	 * too large for {@link Duration#toNanos()}, which is approximately 292 years.
	 */
	@SuppressWarnings("JavadocLinkAsPlainText") // OK: bug in idea
	default void sleepAndStopIfRequested(final Duration duration) throws JobStop
	{
		JobContexts.sleepAndStopIfRequestedPolling(this, duration);
	}

	/**
	 * Sleeps for the specified duration.
	 * In contrast to {@link #deferOrStopIfRequested(JobContext)} this method actually waits
	 * for the specified amount of time.
	 * In contrast to {@link Thread#sleep(long) Thread.sleep} it aborts prematurely
	 * by throwing a {@link JobStop},
	 * if the job has been requested to stop or
	 * becomes requested to stop while sleeping.
	 * <p>
	 * The job may call this method, if the job itself (and not its context)
	 * requires some time to elapse.
	 * I contrast to {@link #stopIfRequested()} and {@link #deferOrStopIfRequested(JobContext)}
	 * there is no obligation to call this method in reasonably short
	 * intervals.
	 * <p>
	 * Calling this method with {@code duration} zero or negative
	 * is equivalent to a call to {@link #stopIfRequested()}.
	 * <p>
	 * This method calls {@link #sleepAndStopIfRequested(Duration)} for actually sleeping.
	 */
	static void sleepAndStopIfRequested(final JobContext ctx, final Duration duration) throws JobStop
	{
		requireNonNull(ctx, "ctx");
		requireNonNull(duration, "duration");
		ctx.stopIfRequested();
		if(!duration.isZero() && !duration.isNegative())
		{
			ctx.sleepAndStopIfRequested(duration);
			ctx.stopIfRequested();
		}
	}


	// message

	/**
	 * Returns whether this context can process information transferred by
	 * {@link #setMessage(String)}.
	 */
	boolean supportsMessage();

	/**
	 * Indicates a message describing the current status of the job.
	 * <p>
	 * Calls to this method can be safely omitted as long as
	 * {@link #supportsMessage()} returns false.
	 * This is recommended if it conserves resources on the callers side.
	 */
	void setMessage(String message);


	// progress

	/**
	 * Returns whether this context can process information transferred by
	 * {@link #incrementProgress()} and
	 * {@link #incrementProgress(int)}.
	 */
	boolean supportsProgress();

	/**
	 * Indicates, that the job has proceeded.
	 * There is no information available,
	 * when the job will return.
	 *
	 * Calling this method is equivalent to calling
	 * {@link #incrementProgress(int) incrementProgress}(1).
	 * <p>
	 * Calls to this method can be safely omitted as long as
	 * {@link #supportsProgress()} returns false.
	 * This is recommended if it conserves resources on the callers side.
	 */
	void incrementProgress();

	/**
	 * Indicates, that the job has proceeded.
	 * There is no information available,
	 * when the job will return.
	 *
	 * Calling this method is equivalent to calling
	 * {@link #incrementProgress()}
	 * for the number of <i>delta</i> times.
	 *
	 * Parameter <i>delta</i> should be greater or equal 0.
	 * Values out of range are accepted as well,
	 * thus no exception is thrown in that case.
	 *
	 * Calling this method with <i>delta</i> of 0 is equivalent
	 * to not calling this method at all.
	 * <p>
	 * Calls to this method can be safely omitted as long as
	 * {@link #supportsProgress()} returns false.
	 * This is recommended if it conserves resources on the callers side.
	 */
	void incrementProgress(int delta);


	// completeness

	/**
	 * Returns whether this context can process information transferred by
	 * {@link #setCompleteness(double)}.
	 */
	boolean supportsCompleteness();

	/**
	 * Indicates the current completeness of the job.
	 *
	 * Parameter <i>completeness</i> should be between 0 and 1.
	 * Values out of range are accepted as well,
	 * thus no exception is thrown in that case.
	 * <p>
	 * Calls to this method can be safely omitted as long as
	 * {@link #supportsCompleteness()} returns false.
	 * This is recommended if it conserves resources on the callers side.
	 */
	void setCompleteness(double completeness);
}
