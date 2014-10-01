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

import static com.exedio.cope.util.TimeZoneStrict.getTimeZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.junit.CopeAssert;
import org.junit.Test;

@SuppressWarnings("static-method")
public class TimeZoneStrictTest extends CopeAssert
{
	@Test public final void ok()
	{
		assertEquals("Europe/Berlin", getTimeZone("Europe/Berlin").getID());
	}

	@Test public final void wrongID()
	{
		try
		{
			getTimeZone("Europe/Berlinx");
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("Europe/Berlinx#GMT", e.getMessage());
		}
	}

	@Test public final void emptyID()
	{
		try
		{
			getTimeZone("");
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("#GMT", e.getMessage());
		}
	}

	@Test public final void nullID()
	{
		try
		{
			getTimeZone(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}
}
