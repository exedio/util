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

import static com.exedio.cope.util.TimeZoneStrict.getTimeZone;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.Test;

public class TimeZoneStrictTest
{
	@Test void ok()
	{
		assertEquals("Europe/Berlin", getTimeZone("Europe/Berlin").getID());
	}

	@Test void wrongID()
	{
		try
		{
			getTimeZone("Europe/Berlinx");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("unsupported time zone: Europe/Berlinx", e.getMessage());
		}
	}

	@Test void emptyID()
	{
		try
		{
			getTimeZone("");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("ID must not be empty", e.getMessage());
		}
	}

	@Test void nullID()
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
