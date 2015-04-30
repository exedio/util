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

import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import org.junit.Test;

/**
 * This test helps me to understand the Calendar framework.
 * @author Ralf Wiebicke
 */
public class CalendarTest
{
	@Test public void setOnDST() throws ParseException
	{
		final Date x = date("TZ+0200 2014/10/26-00:00:00.000");
		final Date a = date("TZ+0200 2014/10/26-01:00:00.000");
		final Date b = date("TZ+0200 2014/10/26-02:00:00.000");
		final Date c = date("TZ+0100 2014/10/26-02:00:00.000");
		final Date d = date("TZ+0100 2014/10/26-03:00:00.000");
		final Date e = date("TZ+0100 2014/10/26-04:00:00.000");
		assertDiff(3600000, x, a);
		assertDiff(3600000, a, b);
		assertDiff(3600000, b, c);
		assertDiff(3600000, c, d);
		assertDiff(3600000, d, e);


		assertSet(x, x, MILLISECOND);
		assertSet(x, x, SECOND);
		assertSet(x, x, MINUTE);

		assertSet(a, a, MILLISECOND);
		assertSet(a, a, SECOND);
		assertSet(a, a, MINUTE);

		assertSet(b, c, MILLISECOND); // TODO wrong
		assertSet(b, c, SECOND); // TODO wrong
		assertSet(b, c, MINUTE); // TODO wrong

		assertSet(c, c, MILLISECOND);
		assertSet(c, c, SECOND);
		assertSet(c, c, MINUTE);

		assertSet(d, d, MILLISECOND);
		assertSet(d, d, SECOND);
		assertSet(d, d, MINUTE);

		assertSet(e, e, MILLISECOND);
		assertSet(e, e, SECOND);
		assertSet(e, e, MINUTE);


		assertSet(date(x, 1), x,          MILLISECOND, 1);
		assertSet(date(x, 1), date(x, 1), SECOND, 0);
		assertSet(date(x, 1), date(x, 1), MINUTE, 0);

		assertSet(date(x, 1000), date(x, 1000), MILLISECOND, 0);
		assertSet(date(x, 1000), x,             SECOND, 1);
		assertSet(date(x, 1000), date(x, 1000), MINUTE, 0);

		assertSet(date(x, 60000), date(x, 60000), MILLISECOND, 0);
		assertSet(date(x, 60000), date(x, 60000), SECOND, 0);
		assertSet(date(x, 60000), x,              MINUTE, 1);


		assertSet(date(b, 1), c,          MILLISECOND, 1); // TODO wrong
		assertSet(date(b, 1), date(c, 1), SECOND, 0); // TODO wrong
		assertSet(date(b, 1), date(c, 1), MINUTE, 0); // TODO wrong

		assertSet(date(b, 1000), date(c, 1000), MILLISECOND, 0); // TODO wrong
		assertSet(date(b, 1000), c,             SECOND, 1); // TODO wrong
		assertSet(date(b, 1000), date(c, 1000), MINUTE, 0); // TODO wrong

		assertSet(date(b, 60000), date(c, 60000), MILLISECOND, 0); // TODO wrong
		assertSet(date(b, 60000), date(c, 60000), SECOND, 0); // TODO wrong
		assertSet(date(b, 60000), c,              MINUTE, 1); // TODO wrong
	}

	private static void assertDiff(final int offset, final Date a, final Date b)
	{
		assertEquals(offset, b.getTime()-a.getTime());
	}

	private static Date date(final Date date, final int offset)
	{
		return new Date(date.getTime() + offset);
	}

	private static void assertSet(final Date setDate, final Date getDate, final int field)
	{
		assertSet(setDate, getDate, field, 0);
	}

	private static void assertSet(
			final Date setDate, final Date getDate,
			final int field, final int fieldValue)
	{
		final GregorianCalendar cal = new GregorianCalendar(
				TimeZoneStrict.getTimeZone("Europe/Berlin"),
				Locale.GERMAN);
		assertTrue(cal.isLenient());
		cal.setLenient(false);

		cal.setTime(setDate);
		assertEquals(setDate, cal.getTime());
		assertEquals(fieldValue, cal.get(field));
		cal.set(field, 0);
		assertEquals(
				"" + df().format(getDate) + " / " + df().format(cal.getTime()),
				getDate, cal.getTime());
	}

	private static SimpleDateFormat df()
	{
		final SimpleDateFormat result = new SimpleDateFormat("'TZ'Z yyyy/MM/dd-HH:mm:ss.SSS");
		result.setTimeZone(TimeZoneStrict.getTimeZone("Europe/Berlin"));
		result.setLenient(false);
		return result;
	}

	private static final Date date(final String s) throws ParseException
	{
		return df().parse(s);
	}

	@Test public void lenient()
	{
		final GregorianCalendar cal = new GregorianCalendar(
				TimeZoneStrict.getTimeZone("Europe/Berlin"),
				Locale.GERMAN);

		cal.set(MINUTE, 61);
		assertEquals(1, cal.get(MINUTE));
	}

	@Test public void nonLenient()
	{
		final GregorianCalendar cal = new GregorianCalendar(
				TimeZoneStrict.getTimeZone("Europe/Berlin"),
				Locale.GERMAN);

		cal.setLenient(false);
		cal.set(MINUTE, 61);

		try
		{
			cal.getTime();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("MINUTE", e.getMessage());
		}

		try
		{
			cal.get(MINUTE);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("MINUTE", e.getMessage());
		}

		try
		{
			cal.get(SECOND);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("MINUTE", e.getMessage());
		}
	}
}
