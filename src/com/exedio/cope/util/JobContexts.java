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

import java.util.NoSuchElementException;

public final class JobContexts
{
	// vain -------------------

	public static final JobContext EMPTY_INTERRUPTER = new EmptyJobContext();


	// iterator ---------------

	public static <E> java.util.Iterator<E> iterator(
			final java.util.Iterator<E> iterator,
			final JobContext interrupter)
	{
		return
			(iterator!=null && interrupter!=null)
			? new Iterator<E>(iterator, interrupter)
			: iterator;
	}

	private static final class Iterator<E> implements java.util.Iterator<E>
	{
		private final java.util.Iterator<E> iterator;
		private final JobContext interrupter;
		private boolean interrupted = false;

		Iterator(
				final java.util.Iterator<E> iterator,
				final JobContext interrupter)
		{
			assert iterator!=null;
			assert interrupter!=null;

			this.iterator = iterator;
			this.interrupter = interrupter;
		}

		public boolean hasNext()
		{
			if(interrupter.requestedToStop())
			{
				interrupted = true;
				return false;
			}

			return iterator.hasNext();
		}

		/**
		 * Must not check {@link JobContext#requestedToStop()} here,
		 * because {@link #hasNext()} may already have promised
		 * to have one more element.
		 */
		public E next()
		{
			if(interrupted)
				throw new NoSuchElementException("interrupted");

			return iterator.next();
		}

		/**
		 * Must not check {@link JobContext#requestedToStop()} here,
		 * because {@link #hasNext()} may already have promised
		 * to have one more element.
		 */
		public void remove()
		{
			if(interrupted)
				throw new NoSuchElementException("interrupted");

			iterator.remove();
		}
	}


	private JobContexts()
	{
		// prevent instantiation
	}
}
