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

public final class InterrupterJobContextAdapter
{
	@Deprecated
	public static final int run(final Interrupter interrupter, final Body body)
	{
		final Adapter ctx = new Adapter(interrupter);
		body.run(ctx);
		return ctx.getProgressAndClose();
	}

	public interface Body
	{
		void run(JobContext ctx);
	}

	@Deprecated
	private static final class Adapter implements JobContext
	{
		private final Interrupter interrupter;
		private int progress = 0;
		private boolean closed = false;

		Adapter(final Interrupter interrupter)
		{
			this.interrupter = interrupter;
		}

		private void assertNotClosed()
		{
			if(closed)
				throw new IllegalStateException("closed");
		}

		int getProgressAndClose()
		{
			assertNotClosed();
			closed = true;
			return progress;
		}

		public void stopIfRequested() throws JobStop
		{
			if(requestedToStop())
				throw new JobStop("requestedToStop");
		}

		@Deprecated // needed for jdk 1.5
		public boolean requestedToStop()
		{
			assertNotClosed();
			return interrupter!=null && interrupter.isRequested();
		}

		// message

		public boolean supportsMessage()
		{
			assertNotClosed();
			return false;
		}

		public void setMessage(final String message)
		{
			assertNotClosed();
		}

		// progress

		public boolean supportsProgress()
		{
			assertNotClosed();
			return true;
		}

		public void incrementProgress()
		{
			assertNotClosed();
			progress++;
		}

		public void incrementProgress(final int delta)
		{
			assertNotClosed();
			progress += delta;
		}

		// completeness

		public boolean supportsCompleteness()
		{
			assertNotClosed();
			return false;
		}

		public void setCompleteness(final double completeness)
		{
			assertNotClosed();
		}
	}


	private InterrupterJobContextAdapter()
	{
		// prevent instantiation
	}
}
