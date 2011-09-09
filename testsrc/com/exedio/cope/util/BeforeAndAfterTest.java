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

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.exedio.cope.junit.CopeAssert;

public class BeforeAndAfterTest extends CopeAssert
{
	private Day today;
	private Day tomorrow;
	private Day yesterday;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		final GregorianCalendar calendar = new GregorianCalendar();
		today = new Day(calendar);
		calendar.add(Calendar.DATE, 1);
		tomorrow = new Day(calendar);
		calendar.add(Calendar.DATE, -2);
		yesterday = new Day(calendar);
	}

	public void testBeforeAssert()
	{
		assertEquals(true, yesterday.before(today));
		assertEquals(true, yesterday.before(tomorrow));
		assertEquals(false, today.before(yesterday));
		assertEquals(true, today.before(tomorrow));
		assertEquals(false, tomorrow.before(yesterday));
		assertEquals(false, tomorrow.before(today));
	}

	public void testAfterAssert()
	{
		assertEquals(false, yesterday.after(today));
		assertEquals(false, yesterday.after(tomorrow));
		assertEquals(true, today.after(yesterday));
		assertEquals(false, today.after(tomorrow));
		assertEquals(true, tomorrow.after(yesterday));
		assertEquals(true, tomorrow.after(today));
	}

	public void testCombiAssert()
	{
		assertEquals(true, new Day(2011, 8, 5).before(new Day(2011, 8, 6)));
		assertEquals(false, new Day(2011, 8, 5).before(new Day(2011, 7, 6)));
		assertEquals(false, new Day(2015, 8, 5).before(new Day(2008, 7, 6)));
		assertEquals(true, new Day(2011, 9, 8).after(new Day(2011, 9, 6)));
		assertEquals(false, new Day(2011, 8, 5).after(new Day(2011, 9, 9)));
		assertEquals(false, new Day(2011, 8, 5).after(new Day(2090, 9, 9)));
	}
}