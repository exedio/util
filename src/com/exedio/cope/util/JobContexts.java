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
import java.util.NoSuchElementException;

public final class JobContexts
{
	// vain -------------------

	public static final JobContext EMPTY = new EmptyJobContext();


	// sleep ------------------

	static void sleepAndStopIfRequestedPolling(
			final JobContext ctx,
			final Duration duration)
			throws JobStop
	{
		requireNonNull(ctx, "ctx");
		requireNonNull(duration, "duration");

		ctx.stopIfRequested();

		if(duration.isZero() || duration.isNegative())
			return;

		final long durationMillis = duration.toMillis(); // fails if too large for toMillis
		if(durationMillis<=0)
			return;

		if(durationMillis<=STOP_POLLING_INTERVAL_MILLIS)
		{
			sleep(ctx, durationMillis);
			return;
		}

		long now = System.nanoTime();
		final long endNow = now + duration.toNanos(); // fails if too large for toNanos
		do
		{
			if(now>=endNow)
				return;

			final long millisToWait = (endNow - now) / 1_000_000;
			if(millisToWait<=0)
				return;

			if(millisToWait<=STOP_POLLING_INTERVAL_MILLIS)
			{
				sleep(ctx, millisToWait);
				return;
			}

			sleep(ctx, STOP_POLLING_INTERVAL_MILLIS);
			now = System.nanoTime();
		}
		while(true);
	}

	private static final long STOP_POLLING_INTERVAL_MILLIS = 100;

	private static void sleep(final JobContext ctx, final long millis) throws JobStop
	{
		try
		{
			Thread.sleep(millis);
		}
		catch(final InterruptedException e)
		{
			throw new RuntimeException("sleep " + millis, e);
		}
		ctx.stopIfRequested();
	}


	// iterator ---------------

	@Deprecated
	public static <E> java.util.Iterator<E> iterator(
			final java.util.Iterator<E> iterator,
			final JobContext ctx)
	{
		return
			(iterator!=null && ctx!=null)
			? new Iterator<>(iterator, ctx)
			: iterator;
	}

	@Deprecated
	private static final class Iterator<E> implements java.util.Iterator<E>
	{
		private final java.util.Iterator<E> iterator;
		private final JobContext ctx;
		private boolean stopRequested = false;
		private String  stopRequestedMessage = null;

		@Deprecated
		Iterator(
				final java.util.Iterator<E> iterator,
				final JobContext ctx)
		{
			assert iterator!=null;
			assert ctx!=null;

			this.iterator = iterator;
			this.ctx = ctx;
		}

		@Override
		public boolean hasNext()
		{
			try
			{
				ctx.stopIfRequested();
			}
			catch(final JobStop js)
			{
				stopRequested = true;
				stopRequestedMessage = js.getMessage();
				return false;
			}

			return iterator.hasNext();
		}

		/**
		 * Must not call {@link JobContext#stopIfRequested()} here,
		 * because {@link #hasNext()} may already have promised
		 * to have one more element.
		 */
		@Override
		public E next()
		{
			if(stopRequested)
				throw new NoSuchElementException("stopRequested: " + stopRequestedMessage);

			return iterator.next();
		}

		/**
		 * Must not call {@link JobContext#stopIfRequested()} here,
		 * because {@link #hasNext()} may already have promised
		 * to have one more element.
		 */
		@Override
		public void remove()
		{
			if(stopRequested)
				throw new NoSuchElementException("stopRequested: " + stopRequestedMessage);

			iterator.remove();
		}
	}


	private JobContexts()
	{
		// prevent instantiation
	}
}
