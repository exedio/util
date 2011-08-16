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

import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.util.InterrupterJobContextAdapter.Body;

@edu.umd.cs.findbugs.annotations.SuppressWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON") // is more compact to write in tests
@SuppressWarnings("deprecation") // OK: testing deprecated api
public class InterrupterJobContextAdapterTest extends CopeAssert
{
	private static final class MockInterrupter implements Interrupter
	{
		int isRequestedCount = 0;
		boolean isRequestedResult = false;

		MockInterrupter()
		{
			// make non-private
		}

		public boolean isRequested()
		{
			isRequestedCount++;
			return isRequestedResult;
		}
	}

	public void testSupports()
	{
		assertEquals(0, InterrupterJobContextAdapter.run(null, new Body(){
			public void run(final JobContext ctx)
			{
				assertEquals(false, ctx.supportsMessage());
				assertEquals(true,  ctx.supportsProgress());
				assertEquals(false, ctx.supportsCompleteness());
				ctx.setMessage("hallo");
				ctx.setCompleteness(0.5);
			}
		}));

		final MockInterrupter interruptor = new MockInterrupter();

		assertEquals(0, InterrupterJobContextAdapter.run(interruptor, new Body(){
			public void run(final JobContext ctx)
			{
				assertEquals(false, ctx.supportsMessage());
				assertEquals(true,  ctx.supportsProgress());
				assertEquals(false, ctx.supportsCompleteness());
				ctx.setMessage("hallo");
				ctx.setCompleteness(0.5);
			}
		}));
	}

	public void testStopIfRequested()
	{
		assertEquals(0, InterrupterJobContextAdapter.run(null, new Body(){
			public void run(final JobContext ctx)
			{
				ctx.stopIfRequested();
			}
		}));

		final MockInterrupter interruptor = new MockInterrupter();

		assertEquals(0, InterrupterJobContextAdapter.run(interruptor, new Body(){
			public void run(final JobContext ctx)
			{
				assertEquals(0, interruptor.isRequestedCount);
				ctx.stopIfRequested();
				assertEquals(1, interruptor.isRequestedCount);
				ctx.stopIfRequested();
				assertEquals(2, interruptor.isRequestedCount);
			}
		}));
	}

	public void testRequestedToStop()
	{
		assertEquals(0, InterrupterJobContextAdapter.run(null, new Body(){
			public void run(final JobContext ctx)
			{
				assertEquals(false, ctx.requestedToStop());
			}
		}));

		final MockInterrupter interruptor = new MockInterrupter();

		assertEquals(0, InterrupterJobContextAdapter.run(interruptor, new Body(){
			public void run(final JobContext ctx)
			{
				assertEquals(0, interruptor.isRequestedCount);
				assertEquals(false, ctx.requestedToStop());
				assertEquals(1, interruptor.isRequestedCount);
				assertEquals(false, ctx.requestedToStop());
				assertEquals(2, interruptor.isRequestedCount);
			}
		}));
	}

	public void testProgress()
	{
		assertEquals(1, InterrupterJobContextAdapter.run(null, new Body(){
			public void run(final JobContext ctx)
			{
				ctx.incrementProgress();
			}
		}));

		final MockInterrupter interruptor = new MockInterrupter();

		assertEquals(1, InterrupterJobContextAdapter.run(interruptor, new Body(){
			public void run(final JobContext ctx)
			{
				ctx.incrementProgress();
			}
		}));
	}

	public void testProgressDelta()
	{
		assertEquals(5, InterrupterJobContextAdapter.run(null, new Body(){
			public void run(final JobContext ctx)
			{
				ctx.incrementProgress(5);
			}
		}));

		final MockInterrupter interruptor = new MockInterrupter();

		assertEquals(5, InterrupterJobContextAdapter.run(interruptor, new Body(){
			public void run(final JobContext ctx)
			{
				ctx.incrementProgress(5);
			}
		}));
	}
}
