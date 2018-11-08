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

import static com.exedio.cope.junit.Assert.assertFails;
import static com.exedio.cope.util.TimeZoneStrict.getTimeZone;
import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
public class TimeZoneStrictTest
{
	@Test void ok()
	{
		assertEquals("Europe/Berlin", getTimeZone("Europe/Berlin").getID());
	}

	@Test void wrongID()
	{
		assertFails(() ->
			getTimeZone("Europe/Berlinx"),
			IllegalArgumentException.class,
			"unsupported time zone: Europe/Berlinx");
	}

	@Test void emptyID()
	{
		assertFails(() ->
			getTimeZone(""),
			IllegalArgumentException.class,
			"ID must not be empty");
	}

	@Test void nullID()
	{
		//noinspection ConstantConditions
		assertFails(() ->
			getTimeZone(null),
			NullPointerException.class, "ID");
	}
}
