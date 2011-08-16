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

public class EmptyJobContextTest extends CopeAssert
{
	public void testIt()
	{
		final EmptyJobContext c = new EmptyJobContext();

		c.stopIfRequested();
		assertEquals(false, c.requestedToStop());
		assertEquals(false, c.supportsMessage());
		assertEquals(false, c.supportsProgress());
		assertEquals(false, c.supportsCompleteness());

		c.setMessage("");
		c.incrementProgress();
		c.incrementProgress(5);
		c.setCompleteness(0.5);
	}

	public void testStop()
	{
		final EmptyJobContext c = new EmptyJobContext(){
			@Override public boolean requestedToStop()
			{
				return true;
			}
		};

		assertEquals(true, c.requestedToStop());
		try
		{
			c.stopIfRequested();
			fail();
		}
		catch(final JobStop js)
		{
			assertEquals(null, js.getMessage());
		}
	}
}
