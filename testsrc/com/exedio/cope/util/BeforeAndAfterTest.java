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
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
public class BeforeAndAfterTest
{
	private Day today;
	private Day tomorrow;
	private Day yesterday;

	@BeforeEach final void setUp()
	{
		final GregorianCalendar calendar = new GregorianCalendar();
		today = new Day(calendar);
		calendar.add(Calendar.DATE, 1);
		tomorrow = new Day(calendar);
		calendar.add(Calendar.DATE, -2);
		yesterday = new Day(calendar);
	}

	@Test void testBeforeAssert()
	{
		assertEquals(true, yesterday.before(today));
		assertEquals(true, yesterday.before(tomorrow));
		assertEquals(false, today.before(yesterday));
		assertEquals(true, today.before(tomorrow));
		assertEquals(false, tomorrow.before(yesterday));
		assertEquals(false, tomorrow.before(today));
	}

	@Test void testAfterAssert()
	{
		assertEquals(false, yesterday.isAfter(today));
		assertEquals(false, yesterday.isAfter(tomorrow));
		assertEquals(true, today.isAfter(yesterday));
		assertEquals(false, today.isAfter(tomorrow));
		assertEquals(true, tomorrow.isAfter(yesterday));
		assertEquals(true, tomorrow.isAfter(today));
	}

	@Test void testCombiAssert()
	{
		assertEquals(true, new Day(2011, 8, 5).before(new Day(2011, 8, 6)));
		assertEquals(false, new Day(2011, 8, 5).before(new Day(2011, 7, 6)));
		assertEquals(false, new Day(2015, 8, 5).before(new Day(2008, 7, 6)));
		assertEquals(true, new Day(2011, 9, 8).isAfter(new Day(2011, 9, 6)));
		assertEquals(false, new Day(2011, 8, 5).isAfter(new Day(2011, 9, 9)));
		assertEquals(false, new Day(2011, 8, 5).isAfter(new Day(2090, 9, 9)));
	}
}
