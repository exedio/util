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
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

import com.exedio.cope.junit.CopeAssert;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.Test;

@SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON") // is more compact to write in tests
public class EmptyJobContextTest extends CopeAssert
{
	@Test public final void testIt()
	{
		final EmptyJobContext c = new EmptyJobContext();

		c.stopIfRequested();
		assertEquals(false, requestedToStop(c));
		assertEquals(false, c.supportsMessage());
		assertEquals(false, c.supportsProgress());
		assertEquals(false, c.supportsCompleteness());

		c.setMessage("");
		c.incrementProgress();
		c.incrementProgress(5);
		c.setCompleteness(0.5);
	}

	@Test public final void testStop()
	{
		final EmptyJobContext c = new EmptyJobContext(){
			@Override @Deprecated public boolean requestedToStop()
			{
				return true;
			}
		};

		assertEquals(true, requestedToStop(c));
		try
		{
			c.stopIfRequested();
			fail();
		}
		catch(final JobStop js)
		{
			assertEquals("requestedToStop", js.getMessage());
		}
	}
}
