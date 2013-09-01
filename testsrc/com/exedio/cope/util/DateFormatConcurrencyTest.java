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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.exedio.cope.junit.CopeAssert;

public class DateFormatConcurrencyTest extends CopeAssert
{
	@Ignore
	@Test public void torment() throws ParseException, InterruptedException
	{
		final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		final List<DFThread> threads = new ArrayList<DFThread>();
		threads.add(new DFThread(format, "2013-07-18 09:02:26.001"));
		threads.add(new DFThread(format, "1234-10-12 10:11:21.002"));
		threads.add(new DFThread(format, "1235-11-02 12:13:24.003"));
		threads.add(new DFThread(format, "2020-08-30 18:19:20.004"));
		threads.add(new DFThread(format, "1975-05-29 19:20:25.005"));
		threads.add(new DFThread(format, "2010-01-02 01:03:05.006"));

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
			//this.format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
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
