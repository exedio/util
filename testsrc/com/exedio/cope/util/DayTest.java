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

import static com.exedio.cope.util.Day.valueOf;
import static javax.xml.datatype.DatatypeConstants.FIELD_UNDEFINED;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.exedio.cope.junit.CopeAssert;


public class DayTest extends CopeAssert
{
	@SuppressWarnings("unused")
	public void testIt() throws ParseException, DatatypeConfigurationException
	{
		try
		{
			new Day(999, 31, 12);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("year must be in range 1000..9999, but was: 999", e.getMessage());
		}
		try
		{
			new Day(10000, 31, 12);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("year must be in range 1000..9999, but was: 10000", e.getMessage());
		}
		try
		{
			new Day(2005, 0, 12);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("month must be in range 1..12, but was: 0", e.getMessage());
		}
		try
		{
			new Day(2005, 32, 12);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("month must be in range 1..12, but was: 32", e.getMessage());
		}
		try
		{
			new Day(2005, 9, 0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("day must be in range 1..31, but was: 0", e.getMessage());
		}
		try
		{
			new Day(2005, 9, 32);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("day must be in range 1..31, but was: 32", e.getMessage());
		}
		assertEquals(2005, new Day(2005, 2, 31).getYear());
		assertEquals(3,    new Day(2005, 2, 31).getMonth());
		assertEquals(3,    new Day(2005, 2, 31).getDay());

		final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

		final Day d = new Day(2005, 9, 23);
		assertEquals(2005, d.getYear());
		assertEquals(9, d.getMonth());
		assertEquals(23, d.getDay());
		assertEquals(df.parse("2005-09-23 00:00:00.000").getTime(), d.getTimeInMillisFrom());
		assertEquals(df.parse("2005-09-23 23:59:59.999").getTime(), d.getTimeInMillisTo());
		assertEquals(df.parse("2005-09-23 00:00:00.000"), d.getTimeFrom());
		assertEquals(df.parse("2005-09-23 23:59:59.999"), d.getTimeTo());
		assertGregorianCalendar(2005, Calendar.SEPTEMBER, 23, d);
		assertXMLGregorianCalendar(2005, 9, 23, d);
		assertEquals("2005/9/23", d.toString());

		assertFalse(d.equals(null));
		assertEquals(d, new Day(2005, 9, 23));
		assertNotEquals(d, new Day(2004, 9, 23));
		assertNotEquals(d, new Day(2005, 8, 23));
		assertNotEquals(d, new Day(2005, 9, 22));
		assertTrue(!d.equals("hallo"));
		assertTrue(!d.equals(Integer.valueOf(22)));

		assertEquals(-1, new Day(2004,  9, 23).compareTo(d));
		assertEquals( 0, new Day(2005,  9, 23).compareTo(d));
		assertEquals( 1, new Day(2006,  9, 23).compareTo(d));
		assertEquals(-1, new Day(2005,  8, 23).compareTo(d));
		assertEquals( 0, new Day(2005,  9, 23).compareTo(d));
		assertEquals( 1, new Day(2005, 10, 23).compareTo(d));
		assertEquals(-1, new Day(2005,  9, 22).compareTo(d));
		assertEquals( 0, new Day(2005,  9, 23).compareTo(d));
		assertEquals( 1, new Day(2005,  9, 24).compareTo(d));

		assertEquals(new Day(2005, 2, 22), valueOf(df.parse("2005-02-22 00:00:00.000")));
		assertEquals(new Day(2005, 2, 22), valueOf(df.parse("2005-02-22 23:59:59.999")));
		assertEquals(new Day(2005, 2, 22), new Day(df.parse("2005-02-22 00:00:00.000").getTime()));
		assertEquals(new Day(2005, 2, 22), new Day(df.parse("2005-02-22 23:59:59.999").getTime()));

		assertEquals(new Day(2005, 2, 23), new Day(2005,  2, 22).add(1));
		assertEquals(new Day(2005, 3,  1), new Day(2005,  2, 28).add(1));
		assertEquals(new Day(2006, 1,  1), new Day(2005, 12, 31).add(1));

		assertNull(valueOf((Date)null));
		assertNull(valueOf((GregorianCalendar)null));
		assertNull(valueOf((XMLGregorianCalendar)null));

		assertEquals(  new Day(2005, 2, 23),
			reserialize(new Day(2005, 2, 23), 80));
		assertEquals(  list(new Day(2007, 2, 23), new Day(2009, 8, 25)),
			reserialize(list(new Day(2007, 2, 23), new Day(2009, 8, 25)), 210));
	}

	static final void assertEquals(final Day expected, final Day actual)
	{
		assertEquals((Object)expected, (Object)actual);
		assertEquals((Object)actual, (Object)expected);
		assertEquals(expected.hashCode(), actual.hashCode());
		assertEquals(0, expected.compareTo(actual));
		assertEquals(0, actual.compareTo(expected));
	}

	static final void assertNotEquals(final Day expected, final Day actual)
	{
		assertTrue(!expected.equals(actual));
		assertTrue(!actual.equals(expected));
		assertTrue(expected.hashCode()!=actual.hashCode());
		assertTrue(expected.compareTo(actual)!=0);
		assertTrue(actual.compareTo(expected)!=0);
	}

	static final void assertGregorianCalendar(final int year, final int month, final int day, final Day actual)
	{
		final GregorianCalendar cal = actual.getGregorianCalendar();
		assertEquals(0, cal.get(Calendar.MILLISECOND));
		assertEquals(0, cal.get(Calendar.SECOND));
		assertEquals(0, cal.get(Calendar.MINUTE));
		assertEquals(0, cal.get(Calendar.HOUR));
		assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
		assertEquals(0, cal.get(Calendar.AM_PM));
		assertEquals(day, cal.get(Calendar.DAY_OF_MONTH));
		assertEquals(month, cal.get(Calendar.MONDAY));
		assertEquals(year, cal.get(Calendar.YEAR));
		assertEquals(1, cal.get(Calendar.ERA));
		assertEquals(actual, valueOf(cal));
	}

	static final void assertXMLGregorianCalendar(final int year, final int month, final int day, final Day actual) throws DatatypeConfigurationException
	{
		final DatatypeFactory factory = DatatypeFactory.newInstance();
		final XMLGregorianCalendar cal = actual.getXMLGregorianCalendar(factory);
		assertEquals(FIELD_UNDEFINED, cal.getMillisecond());
		assertEquals(FIELD_UNDEFINED, cal.getSecond());
		assertEquals(FIELD_UNDEFINED, cal.getMinute());
		assertEquals(FIELD_UNDEFINED, cal.getHour());
		assertEquals(day, cal.getDay());
		assertEquals(month, cal.getMonth());
		assertEquals(year, cal.getYear());
		assertEquals(null, cal.getEon());
		assertEquals(actual, valueOf(cal));
	}
}
