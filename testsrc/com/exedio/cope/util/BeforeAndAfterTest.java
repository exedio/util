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

	public void initDays()
	{
		final GregorianCalendar calendar = new GregorianCalendar();
		today = new Day(calendar);
		calendar.add(Calendar.DATE, 1);
		tomorrow = new Day(calendar);
		calendar.add(Calendar.DATE, -2);
		yesterday = new Day(calendar);
	}

	public void BeforeAssert()
	{
		assertEquals(true, yesterday.before(today));
		assertEquals(true, yesterday.before(tomorrow));
		assertEquals(false, today.before(yesterday));
		assertEquals(true, today.before(tomorrow));
		assertEquals(false, tomorrow.before(yesterday));
		assertEquals(false, tomorrow.before(today));
	}

	public void AfterAssert()
	{
		assertEquals(false, yesterday.after(today));
		assertEquals(false, yesterday.after(tomorrow));
		assertEquals(true, today.after(yesterday));
		assertEquals(false, today.after(tomorrow));
		assertEquals(true, tomorrow.after(yesterday));
		assertEquals(true, tomorrow.after(today));
	}

	public void testIt()
	{
		initDays();
		BeforeAssert();
		AfterAssert();
	}
}