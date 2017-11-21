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
import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * The class <tt>Day</tt> represents a specific day.
 * An instance of this class does <b>NOT</b> represent range of time,
 * but rather something like a calendar sheet.
 * If you want to convert <tt>Day</tt> into {@link Date} or vice versa,
 * the result depends on the {@link TimeZone}.
 * <p>
 * Instance of this class are immutable,
 * so you cannot change the value of an instance of this class.
 *
 * @author Ralf Wiebicke
 */
public final class Day implements Serializable, Comparable<Day>
{
	public static Day valueOf(final Date value, final TimeZone zone)
	{
		return value!=null ? new Day(value, zone) : null;
	}

	public static Day valueOf(final GregorianCalendar value)
	{
		return value!=null ? new Day(value) : null;
	}

	public static Day valueOf(final XMLGregorianCalendar value)
	{
		return value!=null ? new Day(value) : null;
	}

	private static final long serialVersionUID = 1l;

	private final int year;
	private final int month;
	private final int day;

	/**
	 * Creates a new <tt>Day</tt> object,
	 * that represents today.
	 */
	public Day(final TimeZone zone)
	{
		this(Clock.currentTimeMillis(), zone);
	}

	public Day(final Date date, final TimeZone zone)
	{
		this(date.getTime(), zone);
	}

	public Day(final long date, final TimeZone zone)
	{
		this(makeCalendar(date, zone));
	}

	private static GregorianCalendar makeCalendar(final long time, final TimeZone zone)
	{
		final GregorianCalendar result = new GregorianCalendar(zone);
		result.setTimeInMillis(time);
		return result;
	}

	public Day(final GregorianCalendar cal)
	{
		this(cal.get(YEAR), cal.get(MONTH)+1, cal.get(DAY_OF_MONTH));
	}

	public Day(final XMLGregorianCalendar cal)
	{
		this(cal.getYear(), cal.getMonth(), cal.getDay());
	}

	public Day(final int year, final int month, final int day)
	{
		// mysql supports 1000/01/01 to 9999/12/31
		// oracle supports 4712/01/01 BC to 9999/12/31
		check(1000, 9999, year,  "year" );
		check(   1,   12, month, "month");
		check(   1,   31, day,   "day"  );

		final GregorianCalendar c = new GregorianCalendar(year, month-1, day);
		c.setTimeZone(GMT);
		this.year = c.get(YEAR);
		this.month = c.get(MONTH)+1;
		this.day = c.get(DAY_OF_MONTH);
	}

	private static void check(final int from, final int to, final int value, final String name)
	{
		if(value<from || value>to)
			throw new IllegalArgumentException(name + " must be in range " + from + ".." + to + ", but was: " + value);
	}

	public int getYear()
	{
		return year;
	}

	/**
	 * @deprecated Use {@link #getMonthValue()} instead
	 */
	@Deprecated
	public int getMonth()
	{
		return getMonthValue();
	}

	/**
	 * @see LocalDate#getMonthValue
	 */
	public int getMonthValue()
	{
		return month;
	}

	/**
	 * @deprecated Use {@link #getDayOfMonth()} instead
	 */
	@Deprecated
	public int getDay()
	{
		return getDayOfMonth();
	}

	/**
	 * @see LocalDate#getDayOfMonth
	 */
	public int getDayOfMonth()
	{
		return day;
	}

	public Date getTimeFrom(final TimeZone zone)
	{
		return new Date(getTimeInMillisFrom(zone));
	}

	public Date getTimeTo(final TimeZone zone)
	{
		return new Date(getTimeInMillisTo(zone));
	}

	public long getTimeInMillisFrom(final TimeZone zone)
	{
		return getGregorianCalendar(zone).getTimeInMillis();
	}

	public long getTimeInMillisTo(final TimeZone zone)
	{
		final GregorianCalendar cal = getGregorianCalendar(zone);
		cal.add(DAY_OF_MONTH, 1);
		cal.add(MILLISECOND, -1);
		return cal.getTimeInMillis();
	}

	public GregorianCalendar getGregorianCalendar(final TimeZone zone)
	{
		final GregorianCalendar result = new GregorianCalendar(getYear(), getMonthValue()-1, getDayOfMonth());
		result.setTimeZone(zone);
		return result;
	}

	public XMLGregorianCalendar getXMLGregorianCalendar()
	{
		try
		{
			return getXMLGregorianCalendar(DatatypeFactory.newInstance());
		}
		catch(final DatatypeConfigurationException e)
		{
			throw new RuntimeException(e);
		}
	}

	public XMLGregorianCalendar getXMLGregorianCalendar(final DatatypeFactory factory)
	{
		final XMLGregorianCalendar result =
			factory.newXMLGregorianCalendar();
		result.setYear(getYear());
		result.setMonth(getMonthValue());
		result.setDay(getDayOfMonth());
		return result;
	}

	/**
	 * @deprecated Use {@link #plusDays(int)} instead
	 */
	@Deprecated
	public Day add(final int days)
	{
		return plusDays(days);
	}

	/**
	 * @see LocalDate#plusDays(long)
	 */
	public Day plusDays(final int days)
	{
		final GregorianCalendar cal = getGregorianCalendar(GMT);
		cal.add(DATE, days);
		return new Day(cal);
	}

	private static final TimeZone GMT = getTimeZone("Etc/GMT");


	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof Day))
			return false;

		final Day o = (Day)other;
		return day==o.day && month==o.month && year==o.year;
	}

	/**
	 * @deprecated Use {@link #isAfter(Day)} instead
	 */
	@Deprecated
	public boolean after(final Day when)
	{
		return isAfter(when);
	}

	/**
	 * @see LocalDate#isAfter(java.time.chrono.ChronoLocalDate)
	 */
	public boolean isAfter(final Day when)
	{
		return compareTo(when) > 0;
	}

	/**
	 * @deprecated Use {@link #isBefore(Day)} instead
	 */
	@Deprecated
	public boolean before(final Day when)
	{
		return isBefore(when);
	}

	/**
	 * @see LocalDate#isBefore(java.time.chrono.ChronoLocalDate)
	 */
	public boolean isBefore(final Day when)
	{
		return compareTo(when) < 0;
	}

	@Override
	public int hashCode()
	{
		return ((31*31)*day) ^ (31*month) ^ year;
	}

	@Override
	public int compareTo(final Day other)
	{
		if(year<other.year)
			return -1;
		else if(year>other.year)
			return 1;

		if(month<other.month)
			return -1;
		else if(month>other.month)
			return 1;

		if(day<other.day)
			return -1;
		else if(day>other.day)
			return 1;

		return 0;
	}


	// java.time

	/**
	 * @see Date#toInstant()
	 */
	public LocalDate toLocalDate()
	{
		return LocalDate.of(year, month, day);
	}

	public static LocalDate toLocalDate(final Day day)
	{
		return day!=null ? day.toLocalDate() : null;
	}

	/**
	 * @see Date#from(java.time.Instant)
	 */
	public static Day from(final LocalDate localDate)
	{
		return
				localDate!=null
				? new Day(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth())
				: null;
	}


	@Override
	public String toString()
	{
		return String.valueOf(year) + '/' + month + '/' + day;
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Provide {@link TimeZone} as parameter.
	 */
	@Deprecated
	public static Day valueOf(final Date value)
	{
		return valueOf(value, TimeZone.getDefault());
	}

   /**
    * Creates a new <tt>Day</tt> object,
    * that represents today.
	 * @deprecated Provide {@link TimeZone} as parameter.
    */
	@Deprecated
	public Day()
	{
		this(TimeZone.getDefault());
	}

	/**
	 * @deprecated Provide {@link TimeZone} as parameter.
	 */
	@Deprecated
	public Day(final Date date)
	{
		this(date, TimeZone.getDefault());
	}

	/**
	 * @deprecated Provide {@link TimeZone} as parameter.
	 */
	@Deprecated
	public Day(final long date)
	{
		this(date, TimeZone.getDefault());
	}

	/**
	 * @deprecated Provide {@link TimeZone} as parameter.
	 */
	@Deprecated
	public Date getTimeFrom()
	{
		return getTimeFrom(TimeZone.getDefault());
	}

	/**
	 * @deprecated Provide {@link TimeZone} as parameter.
	 */
	@Deprecated
	public Date getTimeTo()
	{
		return getTimeTo(TimeZone.getDefault());
	}

	/**
	 * @deprecated Provide {@link TimeZone} as parameter.
	 */
	@Deprecated
	public long getTimeInMillisFrom()
	{
		return getTimeInMillisFrom(TimeZone.getDefault());
	}

	/**
	 * @deprecated Provide {@link TimeZone} as parameter.
	 */
	@Deprecated
	public long getTimeInMillisTo()
	{
		return getTimeInMillisTo(TimeZone.getDefault());
	}

	/**
	 * @deprecated Provide {@link TimeZone} as parameter.
	 */
	@Deprecated
	public GregorianCalendar getGregorianCalendar()
	{
		return getGregorianCalendar(TimeZone.getDefault());
	}

	/**
	 * @deprecated Use {@link #getTimeInMillisFrom()} instead
	 */
	@Deprecated
	public long getTimeInMillis()
	{
		return getTimeInMillisFrom();
	}
}
