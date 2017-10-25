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

import java.util.NoSuchElementException;

public final class JobContexts
{
	// vain -------------------

	public static final JobContext EMPTY = new EmptyJobContext();


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
		@Deprecated // Needed for jdk 1.5
		public boolean hasNext()
		{
			try
			{
				ctx.stopIfRequested();
			}
			catch(final JobStop ignored)
			{
				stopRequested = true;
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
				throw new NoSuchElementException("requestedToStop");

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
				throw new NoSuchElementException("requestedToStop");

			iterator.remove();
		}
	}


	private JobContexts()
	{
		// prevent instantiation
	}
}
