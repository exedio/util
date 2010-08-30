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

import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * The class <tt>Day</tt> represents a specific day.
 * It is similar to {@link java.util.Date},
 * but with &quot;day precision&quot; instead of millisecond precision.
 * Unlike {@link java.util.Date} its immutable,
 * so you cannot change the value of an instance of this class.
 *
 * @author Ralf Wiebicke
 */
public final class Day implements Serializable, Comparable<Day>
{
	private static final long serialVersionUID = 1l;

	private final int year;
	private final int month;
	private final int day;

   /**
    * Creates a new <tt>Day</tt> object,
    * that represents today.
    */
	public Day()
	{
		this(makeCalendar(System.currentTimeMillis()));
	}

	public Day(final Date date)
	{
		this(makeCalendar(date.getTime()));
	}

	private static GregorianCalendar makeCalendar(final long time)
	{
		final GregorianCalendar result = new GregorianCalendar();
		result.setTimeInMillis(time);
		return result;
	}

	private Day(final GregorianCalendar c)
	{
		this(c.get(YEAR), c.get(MONTH)+1, c.get(DAY_OF_MONTH));
	}

	public Day(final int year, final int month, final int day)
	{
		// mysql supports 1000/01/01 to 9999/12/31
		// oracle supports 4712/01/01 BC to 9999/12/31
		check(1000, 9999, year,  "year" );
		check(   1,   12, month, "month");
		check(   1,   31, day,   "day"  );

		final GregorianCalendar c = new GregorianCalendar(year, month-1, day);
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

	public int getMonth()
	{
		return month;
	}

	public int getDay()
	{
		return day;
	}

	public Date getTimeFrom()
	{
		return new Date(getTimeInMillisFrom());
	}

	public Date getTimeTo()
	{
		return new Date(getTimeInMillisTo());
	}

	public long getTimeInMillisFrom()
	{
		return newCalendar().getTimeInMillis();
	}

	public long getTimeInMillisTo()
	{
		final GregorianCalendar c = newCalendar();
		c.add(DAY_OF_MONTH, 1);
		c.add(MILLISECOND, -1);
		return c.getTimeInMillis();
	}

	public Day add(final int days)
	{
		final GregorianCalendar cal = newCalendar();
		cal.add(DATE, days);
		return new Day(cal);
	}

	private GregorianCalendar newCalendar()
	{
		return new GregorianCalendar(year, month-1, day);
	}

	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof Day))
			return false;

		final Day o = (Day)other;
		return day==o.day && month==o.month && year==o.year;
	}

	@Override
	public int hashCode()
	{
		return day ^ month ^ year;
	}

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

	@Override
	public String toString()
	{
		return String.valueOf(year) + '/' + month + '/' + day;
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #getTimeInMillisFrom()} instead
	 */
	@Deprecated
	public long getTimeInMillis()
	{
		return getTimeInMillisFrom();
	}
}
