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

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON") // is more compact to write in tests
@SuppressWarnings("deprecation") // OK: testing deprecated api
public class InterrupterJobContextAdapterTest
{
	private static final class MockInterrupter implements Interrupter
	{
		int isRequestedCount = 0;

		MockInterrupter()
		{
			// make non-private
		}

		@Override
		public boolean isRequested()
		{
			isRequestedCount++;
			return false;
		}
	}

	@Test void testSupports()
	{
		assertEquals(0, InterrupterJobContextAdapter.run(null, ctx ->
			{
				assertEquals(false, ctx.supportsMessage());
				assertEquals(true,  ctx.supportsProgress());
				assertEquals(false, ctx.supportsCompleteness());
				ctx.setMessage("hallo");
				ctx.setCompleteness(0.5);
			}
		));

		final MockInterrupter interruptor = new MockInterrupter();

		assertEquals(0, InterrupterJobContextAdapter.run(interruptor, ctx ->
			{
				assertEquals(false, ctx.supportsMessage());
				assertEquals(true,  ctx.supportsProgress());
				assertEquals(false, ctx.supportsCompleteness());
				ctx.setMessage("hallo");
				ctx.setCompleteness(0.5);
			}
		));
	}

	@Test void testStopIfRequested()
	{
		assertEquals(0, InterrupterJobContextAdapter.run(null,
				JobContext::stopIfRequested
		));

		final MockInterrupter interruptor = new MockInterrupter();

		assertEquals(0, InterrupterJobContextAdapter.run(interruptor, ctx ->
			{
				assertEquals(0, interruptor.isRequestedCount);
				ctx.stopIfRequested();
				assertEquals(1, interruptor.isRequestedCount);
				ctx.stopIfRequested();
				assertEquals(2, interruptor.isRequestedCount);
			}
		));
	}

	@Test void testProgress()
	{
		assertEquals(1, InterrupterJobContextAdapter.run(null,
				JobContext::incrementProgress
		));

		final MockInterrupter interruptor = new MockInterrupter();

		assertEquals(1, InterrupterJobContextAdapter.run(interruptor,
				JobContext::incrementProgress
		));
	}

	@Test void testProgressDelta()
	{
		assertEquals(5, InterrupterJobContextAdapter.run(null,
				ctx -> ctx.incrementProgress(5)
		));

		final MockInterrupter interruptor = new MockInterrupter();

		assertEquals(5, InterrupterJobContextAdapter.run(interruptor,
				ctx -> ctx.incrementProgress(5)
		));
	}
}
