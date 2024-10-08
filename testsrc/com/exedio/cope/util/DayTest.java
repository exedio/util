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
import static com.exedio.cope.junit.CopeAssert.reserialize;
import static com.exedio.cope.junit.EqualsAssert.assertEqualsAndHash;
import static com.exedio.cope.junit.EqualsAssert.assertNotEqualsAndHash;
import static com.exedio.cope.util.Day.valueOf;
import static com.exedio.cope.util.TimeZoneStrict.getTimeZone;
import static javax.xml.datatype.DatatypeConstants.FIELD_UNDEFINED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.exedio.cope.util.junit.ClockRule;
import com.exedio.cope.util.junit.TimeZoneDefaultRule;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(TimeZoneDefaultRule.class)
public class DayTest
{
	@Test void printDefaultTimeZone()
	{
		final TimeZone d = TimeZone.getDefault();
		System.out.println("Default TimeZone " + d.getID() + ' ' + d.getOffset(System.currentTimeMillis())/1000);
	}
	@Test void yearSmall()
	{
		assertFails(() ->
			new Day(999, 31, 12),
			IllegalArgumentException.class,
			"year must be in range 1000..9999, but was: 999");
	}
	@Test void yearLarge()
	{
		assertFails(() ->
			new Day(10000, 31, 12),
			IllegalArgumentException.class,
			"year must be in range 1000..9999, but was: 10000");
	}
	@Test void monthSmall()
	{
		assertFails(() ->
			new Day(2005, 0, 12),
			IllegalArgumentException.class,
			"month must be in range 1..12, but was: 0");
	}
	@Test void monthLarge()
	{
		assertFails(() ->
			new Day(2005, 32, 12),
			IllegalArgumentException.class,
			"month must be in range 1..12, but was: 32");
	}
	@Test void daySmall()
	{
		assertFails(() ->
			new Day(2005, 9, 0),
			IllegalArgumentException.class,
			"day must be in range 1..31, but was: 0");
	}
	@Test void dayLarge()
	{
		assertFails(() ->
			new Day(2005, 9, 32),
			IllegalArgumentException.class,
			"day must be in range 1..31, but was: 32");
	}
	@Test void getters() throws DatatypeConfigurationException
	{
		final Day d = new Day(2005, 9, 23);
		assertEquals(2005, d.getYear());
		assertEquals(9, d.getMonthValue());
		assertEquals(23, d.getDayOfMonth());
		assertGregorianCalendar(2005, Calendar.SEPTEMBER, 23, d, getTimeZone("Europe/Berlin"));
		assertGregorianCalendar(2005, Calendar.SEPTEMBER, 23, d, getTimeZone("Etc/GMT"));
		assertXMLGregorianCalendar(2005, 9, 23, d);
		assertEquals("2005/9/23", d.toString());
	}
	@Test void overflow()
	{
		final Day d = new Day(2005, 2, 31);
		assertEquals(2005, d.getYear());
		assertEquals(3,    d.getMonthValue());
		assertEquals(3,    d.getDayOfMonth());
	}
	@Test void nullZone()
	{
		assertFails(() ->
			new Day(5555l, null),
			NullPointerException.class,
			"Cannot invoke \"java.util.TimeZone.getOffset(long)\" " +
			"because \"tz\" is null");
	}
	@Deprecated // OK testing deprecated API
	@Test void conversionDateDeprecated(final TimeZoneDefaultRule timeZoneDefault) throws ParseException
	{
		timeZoneDefault.set(getTimeZone("Europe/Berlin"));
		final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS (Z)", Locale.ENGLISH);

		final Day summer = new Day(2005, 9, 23);
		final Day winter = new Day(2005, 2, 22);

		assertEquals(df.parse("2005-09-23 00:00:00.000 (+0200)"), summer.getTimeFrom());
		assertEquals(df.parse("2005-09-23 23:59:59.999 (+0200)"), summer.getTimeTo());
		assertEquals(df.parse("2005-02-22 00:00:00.000 (+0100)"), winter.getTimeFrom());
		assertEquals(df.parse("2005-02-22 23:59:59.999 (+0100)"), winter.getTimeTo());

		assertEquals(summer, valueOf(df.parse("2005-09-23 00:00:00.000 (+0200)")));
		assertEquals(summer, valueOf(df.parse("2005-09-23 23:59:59.999 (+0200)")));
		assertEquals(winter, valueOf(df.parse("2005-02-22 00:00:00.000 (+0100)")));
		assertEquals(winter, valueOf(df.parse("2005-02-22 23:59:59.999 (+0100)")));
	}
	@Test void conversionDateBerlin() throws ParseException
	{
		final TimeZone tz = getTimeZone("Europe/Berlin");
		final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS (Z)", Locale.ENGLISH);

		final Day summer = new Day(2005, 9, 23);
		final Day winter = new Day(2005, 2, 22);

		assertEquals(df.parse("2005-09-23 00:00:00.000 (+0200)"), summer.getTimeFrom(tz));
		assertEquals(df.parse("2005-09-23 23:59:59.999 (+0200)"), summer.getTimeTo(tz));
		assertEquals(df.parse("2005-02-22 00:00:00.000 (+0100)"), winter.getTimeFrom(tz));
		assertEquals(df.parse("2005-02-22 23:59:59.999 (+0100)"), winter.getTimeTo(tz));

		assertEquals(summer, valueOf(df.parse("2005-09-23 00:00:00.000 (+0200)"), tz));
		assertEquals(summer, valueOf(df.parse("2005-09-23 23:59:59.999 (+0200)"), tz));
		assertEquals(winter, valueOf(df.parse("2005-02-22 00:00:00.000 (+0100)"), tz));
		assertEquals(winter, valueOf(df.parse("2005-02-22 23:59:59.999 (+0100)"), tz));
	}
	@Test void conversionDateGMT(final TimeZoneDefaultRule timeZoneDefault) throws ParseException
	{
		timeZoneDefault.set(getTimeZone("Europe/Berlin"));
		final TimeZone tz = getTimeZone("Etc/GMT");
		final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS (Z)", Locale.ENGLISH);

		final Day summer = new Day(2005, 9, 23);
		final Day winter = new Day(2005, 2, 22);

		assertEquals(df.parse("2005-09-23 00:00:00.000 (+0000)"), summer.getTimeFrom(tz));
		assertEquals(df.parse("2005-09-23 23:59:59.999 (+0000)"), summer.getTimeTo(tz));
		assertEquals(df.parse("2005-02-22 00:00:00.000 (+0000)"), winter.getTimeFrom(tz));
		assertEquals(df.parse("2005-02-22 23:59:59.999 (+0000)"), winter.getTimeTo(tz));

		assertEquals(summer, valueOf(df.parse("2005-09-23 00:00:00.000 (+0000)"), tz));
		assertEquals(summer, valueOf(df.parse("2005-09-23 23:59:59.999 (+0000)"), tz));
		assertEquals(winter, valueOf(df.parse("2005-02-22 00:00:00.000 (+0000)"), tz));
		assertEquals(winter, valueOf(df.parse("2005-02-22 23:59:59.999 (+0000)"), tz));
	}
	@Test void conversionMillis() throws ParseException
	{
		final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS (Z)", Locale.ENGLISH);
		final TimeZone tz = getTimeZone("Europe/Berlin"); // TODO use GMT

		final Day d = new Day(2005, 9, 23);
		assertEquals(df.parse("2005-09-23 00:00:00.000 (+0200)").getTime(), d.getTimeInMillisFrom(tz));
		assertEquals(df.parse("2005-09-23 23:59:59.999 (+0200)").getTime(), d.getTimeInMillisTo(tz));

		assertEquals(new Day(2005, 2, 22), new Day(df.parse("2005-02-22 00:00:00.000 (+0100)").getTime(), tz));
		assertEquals(new Day(2005, 2, 22), new Day(df.parse("2005-02-22 23:59:59.999 (+0100)").getTime(), tz));
	}
	@Test void plusDays()
	{
		assertEquals(new Day(2005, 2, 23), new Day(2005,  2, 22).plusDays(1));
		assertEquals(new Day(2005, 3,  1), new Day(2005,  2, 28).plusDays(1));
		assertEquals(new Day(2006, 1,  1), new Day(2005, 12, 31).plusDays(1));
		assertEquals(new Day(2005, 2, 27), new Day(2005,  2, 22).plusDays( 5));
		assertEquals(new Day(2005, 2, 17), new Day(2005,  2, 22).plusDays(-5));
	}
	@Test void plusDaysOverflow()
	{
		final Day limit = new Day(9999, 12, 31);
		assertEquals(limit, new Day(9999, 12, 30).plusDays(1));
		assertFails(
				() -> limit.plusDays(1),
				IllegalArgumentException.class,
				"year must be in range 1000..9999, but was: 10000");
	}
	@Test void plusDaysUnderflow()
	{
		final Day limit = new Day(1000, 1, 1);
		assertEquals(limit, new Day(1000, 1, 2).plusDays(-1));
		assertFails(
				() -> limit.plusDays(-1),
				IllegalArgumentException.class,
				"year must be in range 1000..9999, but was: 999");
	}
	@SuppressWarnings("RedundantCast")
	@Test void valueOfNull()
	{
		assertNull(valueOf((Date)null, (TimeZone)null));
		assertNull(valueOf((GregorianCalendar)null));
		assertNull(valueOf((XMLGregorianCalendar)null));
	}
	@Test void equalsHash()
	{
		final Day d = new Day(2005, 9, 23);
		assertEqualsAndHash(d, new Day(2005, 9, 23));
		assertNotEqualsAndHash(d,
				new Day(2004, 9, 23),
				new Day(2005, 8, 23),
				new Day(2005, 9, 22));
	}
	@Test void compare()
	{
		final Day d = new Day(2005, 9, 23);
		// year
		assertEquals(-1, new Day(2004,  9, 23).compareTo(d));
		assertEquals( 0, new Day(2005,  9, 23).compareTo(d));
		assertEquals( 1, new Day(2006,  9, 23).compareTo(d));
		// month
		assertEquals(-1, new Day(2005,  8, 23).compareTo(d));
		assertEquals( 0, new Day(2005,  9, 23).compareTo(d));
		assertEquals( 1, new Day(2005, 10, 23).compareTo(d));
		// day
		assertEquals(-1, new Day(2005,  9, 22).compareTo(d));
		assertEquals( 0, new Day(2005,  9, 23).compareTo(d));
		assertEquals( 1, new Day(2005,  9, 24).compareTo(d));
	}
	@Test void serialize()
	{
		assertEquals(
				new Day(2005, 2, 23), reserialize(
				new Day(2005, 2, 23), 80));
		assertEquals(
				new Day(2007, 2, 23), reserialize(
				new Day(2007, 2, 23), 80));
		assertEquals(
				new Day(2009, 8, 25), reserialize(
				new Day(2009, 8, 25), 80));
	}

	static final void assertGregorianCalendar(final int year, final int month, final int day, final Day actual, final TimeZone zone)
	{
		final GregorianCalendar cal = actual.getGregorianCalendar(zone);
		assertEquals(0, cal.get(Calendar.MILLISECOND));
		assertEquals(0, cal.get(Calendar.SECOND));
		assertEquals(0, cal.get(Calendar.MINUTE));
		assertEquals(0, cal.get(Calendar.HOUR));
		assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
		assertEquals(0, cal.get(Calendar.AM_PM));
		assertEquals(day, cal.get(Calendar.DAY_OF_MONTH));
		assertEquals(month, cal.get(Calendar.MONTH));
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

		final XMLGregorianCalendar cal2 = actual.getXMLGregorianCalendar();
		assertEquals(FIELD_UNDEFINED, cal2.getMillisecond());
		assertEquals(FIELD_UNDEFINED, cal2.getSecond());
		assertEquals(FIELD_UNDEFINED, cal2.getMinute());
		assertEquals(FIELD_UNDEFINED, cal2.getHour());
		assertEquals(day, cal2.getDay());
		assertEquals(month, cal2.getMonth());
		assertEquals(year, cal2.getYear());
		assertEquals(null, cal2.getEon());
		assertEquals(actual, valueOf(cal2));
	}

	@ExtendWith(ClockRule.Extension.class)
	@Test void currentDay(final ClockRule clock)
	{
		clock.override(() -> new Day(1986, 4, 26).getTimeInMillisFrom(getTimeZone("Etc/GMT")));
		assertEquals(new Day(1986, 4, 26), new Day(getTimeZone("Etc/GMT")));
	}

	@Test void javaTime()
	{
		final LocalDate ldy = LocalDate.of(2016, Month.NOVEMBER, 25);
		final Day day = new Day(2016, 11, 25);

		assertEquals("2016-11-25", ldy.toString());
		assertEquals("2016/11/25", day.toString());

		assertEquals(ldy, day.toLocalDate());
		assertEquals(ldy, Day.toLocalDate(day));
		assertEquals(day, Day.from(ldy));

		//noinspection ConstantValue
		assertEquals(null, Day.toLocalDate(null));
		assertEquals(null, Day.from(null));
	}
	@Test void javaTimeOverflow()
	{
		final LocalDate limit = LocalDate.of(9999, Month.DECEMBER, 31);
		assertEquals(new Day(9999, 12, 31), Day.from(limit));

		final LocalDate beyond = LocalDate.of(10000, Month.JANUARY, 1);
		assertEquals(beyond, limit.plusDays(1));
		assertFails(
				() -> Day.from(beyond),
				IllegalArgumentException.class,
				"year must be in range 1000..9999, but was: 10000");
	}
	@Test void javaTimeUnderflow()
	{
		final LocalDate limit = LocalDate.of(1000, Month.JANUARY, 1);
		assertEquals(new Day(1000, 1, 1), Day.from(limit));

		final LocalDate beyond = LocalDate.of(999, Month.DECEMBER, 31);
		assertEquals(beyond, limit.minusDays(1));
		assertFails(
				() -> Day.from(beyond),
				IllegalArgumentException.class,
				"year must be in range 1000..9999, but was: 999");
	}
	@Test void daysUntil()
	{
		assertEquals(1, new Day(2005, 2, 22).daysUntil(new Day(2005, 2, 23)));
		assertEquals(1, new Day(2005, 2, 28).daysUntil(new Day(2005, 3,  1)));
		assertEquals(1, new Day(2005, 12, 31).daysUntil(new Day(2006, 1,  1)));
		assertEquals(5, new Day(2005, 2, 22).daysUntil(new Day(2005, 2, 27) ));
		assertEquals(-5, new Day(2005, 2, 22).daysUntil(new Day(2005, 2, 17)));
		assertEquals(3287181, new Day(1000, 1, 1).daysUntil(new Day(9999, 12, 31)));
		assertEquals(1, new Day(2023, 2, 28).daysUntil(new Day(2023, 3, 1)));
		assertEquals(2, new Day(2024, 2, 28).daysUntil(new Day(2024, 3, 1)));
	}
}
