/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

/**
 * An interface for controlling long-running jobs.
 * Instances of this interface are not required to work
 * for multiple threads accessing the methods concurrently.
 *
 * This interface has been inspired by
 * <a href="http://www.sauronsoftware.it/projects/cron4j/api/it/sauronsoftware/cron4j/TaskExecutionContext.html">TaskExecutionContext</a>
 * of <a href="http://www.sauronsoftware.it/projects/cron4j">cron4j</a>.
 */
public interface JobContext
{
	/**
	 * Checks, whether the job has been requested to stop.
	 * The developer should call this method in reasonably short
	 * intervals.
	 * If throws a {@link JobStop}, the job should stop
	 * gracefully as soon as possible,
	 * but all resources held should be closed/disposed carefully.
	 * You may use {@link #requestedToStop()} instead, but it's not recommended.
	 */
	void stopIfRequested() throws JobStop;

	/**
	 * Checks, whether the job has been requested to stop.
	 * The developer should call this method in reasonably short
	 * intervals.
	 * If <i>true</i> is returned, the job should stop
	 * gracefully as soon as possible,
	 * but all resources held should be closed/disposed carefully.
	 * @deprecated It's recommended to use {@link #stopIfRequested()} instead.
	 */
	@Deprecated
	boolean requestedToStop();


	// message

	/**
	 *	Returns whether this context can process information transferred by
	 * {@link #setMessage(String)}.
	 */
	boolean supportsMessage();

	/**
	 * Indicates a message describing the current status of the job.
	 *
	 * Should be called only, if
	 * {@link #supportsMessage()}
	 * returns true.
	 * Otherwise calls are ignored.
	 */
	void setMessage(String message);


	// progress

	/**
	 *	Returns whether this context can process information transferred by
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
	 *
	 * Should be called only, if
	 * {@link #supportsProgress()}
	 * returns true.
	 * Otherwise calls are ignored.
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
	 *
	 * Should be called only, if
	 * {@link #supportsProgress()}
	 * returns true.
	 * Otherwise calls are ignored.
	 */
	void incrementProgress(int delta);


	// completeness

	/**
	 *	Returns whether this context can process information transferred by
	 * {@link #setCompleteness(double)}.
	 */
	boolean supportsCompleteness();

	/**
	 * Indicates the current completeness of the job.
	 *
	 * Parameter <i>completeness</i> should be between 0 and 1.
	 * Values out of range are accepted as well,
	 * thus no exception is thrown in that case.
	 *
	 * Should be called only, if
	 * {@link #supportsCompleteness()}
	 * returns true.
	 * Otherwise calls are ignored.
	 */
	void setCompleteness(double completeness);
}
