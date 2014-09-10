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

import com.exedio.cope.junit.CopeAssert;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.junit.Ignore;
import org.junit.Test;

public class DateFormatConcurrencyTest extends CopeAssert
{
	@Ignore
	@Test public void torment() throws ParseException, InterruptedException
	{
		final DateFormat format = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.ENGLISH);
		final List<DFThread> threads = new ArrayList<>();
		threads.add(new DFThread(format, "Jun 18, 2013 9:02:26 PM"));
		threads.add(new DFThread(format, "Oct 12, 1234 10:11:21 PM"));
		threads.add(new DFThread(format, "Nov 11, 1235 2:13:24 AM"));
		threads.add(new DFThread(format, "Aug 30, 2020 8:19:20 AM"));
		threads.add(new DFThread(format, "Jun 29, 1975 9:20:25 PM"));
		threads.add(new DFThread(format, "Jan 2, 2010 1:03:05 PM"));

		for(final DFThread t : threads)
			t.start();
		for(final DFThread t : threads)
			t.join();
	}

	private static class DFThread extends Thread
	{
		private final Date value;
		private final DateFormat format;
		private final String expected;

		public DFThread(final DateFormat format, final String string) throws ParseException
		{
			this.format = format;
			//this.format = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.ENGLISH);
			this.expected = string;
			this.value = format.parse(string);
		}

		@Override
		public void run()
		{
			for(int i = 0; i<1000; i++)
			{
				final String actual = format.format(value);
				if(!expected.equals(actual))
					throw new RuntimeException(
							"DFThread " + expected + " iteration " + i + ": got " + actual);
			}
			System.out.println("DFThread finished: " + expected);
		}
	}
}
