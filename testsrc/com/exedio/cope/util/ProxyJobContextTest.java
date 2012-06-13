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

import static com.exedio.cope.util.JobContextDeprecated.requestedToStop;

import com.exedio.cope.junit.CopeAssert;

public class ProxyJobContextTest extends CopeAssert
{
	public void testIt()
	{
		final ProxyJobContext c = new ProxyJobContext(new EmptyJobContext());

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

	@SuppressWarnings("unused")
	public void testNull()
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
