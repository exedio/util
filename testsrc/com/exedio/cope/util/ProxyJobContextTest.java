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

import static com.exedio.cope.util.JobContextDeprecated.requestedToStop;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.Test;

public class ProxyJobContextTest
{
	private JC target;
	private ProxyJobContext c;
	@BeforeEach void before()
	{
		target = new JC();
		c = new ProxyJobContext(target);
	}

	@Test void testStopIfRequested()
	{
		c.stopIfRequested();
		target.assertIt("stopIfRequested");
	}
	@Test void testRequestedToStop()
	{
		assertEquals(false, requestedToStop(c));
		target.assertIt("requestedToStop");

		target.requestedToStop = true;
		assertEquals(true, requestedToStop(c));
		target.assertIt("requestedToStop");
	}
	@Test void testSupportsMessage()
	{
		assertEquals(false, c.supportsMessage());
		target.assertIt("supportsMessage");

		target.supportsMessage = true;
		assertEquals(true, c.supportsMessage());
		target.assertIt("supportsMessage");
	}
	@Test void testSupportsProgress()
	{
		assertEquals(false, c.supportsProgress());
		target.assertIt("supportsProgress");

		target.supportsProgress = true;
		assertEquals(true, c.supportsProgress());
		target.assertIt("supportsProgress");
	}
	@Test void testSupportsCompleteness()
	{
		assertEquals(false, c.supportsCompleteness());
		target.assertIt("supportsCompleteness");

		target.supportsCompleteness = true;
		assertEquals(true, c.supportsCompleteness());
		target.assertIt("supportsCompleteness");
	}
	@Test void testSetMessage()
	{
		c.setMessage("");
		target.assertIt("setMessage()");

		c.setMessage("myMessage");
		target.assertIt("setMessage(myMessage)");
	}
	@Test void testIncrementProgress()
	{
		c.incrementProgress();
		target.assertIt("incrementProgress");

		c.incrementProgress(5);
		target.assertIt("incrementProgress(5)");
	}
	@Test void testSetCompleteness()
	{
		c.setCompleteness(0.5);
		target.assertIt("setCompleteness(0.5)");
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
			actual.add("stopIfRequested");
		}

		boolean requestedToStop = false;
		@Override
		@Deprecated
		@SuppressWarnings("deprecation") // needed for idea
		public boolean requestedToStop()
		{
			actual.add("requestedToStop");
			return requestedToStop;
		}

		boolean supportsMessage = false;
		@Override
		public boolean supportsMessage()
		{
			actual.add("supportsMessage");
			return supportsMessage;
		}

		@Override
		public void setMessage(final String message)
		{
			actual.add("setMessage(" + message + ")");
		}

		boolean supportsProgress = false;
		@Override
		public boolean supportsProgress()
		{
			actual.add("supportsProgress");
			return supportsProgress;
		}

		@Override
		public void incrementProgress()
		{
			actual.add("incrementProgress");
		}

		@Override
		public void incrementProgress(final int delta)
		{
			actual.add("incrementProgress(" + delta + ")");
		}

		boolean supportsCompleteness = false;
		@Override
		public boolean supportsCompleteness()
		{
			actual.add("supportsCompleteness");
			return supportsCompleteness;
		}

		@Override
		public void setCompleteness(final double completeness)
		{
			actual.add("setCompleteness(" + completeness + ")");
		}
	}


	@SuppressWarnings("unused")
	@Test public void testNull()
	{
		try
		{
			new ProxyJobContext(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("target", e.getMessage());
		}
	}
}
