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

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Ignore;
import org.junit.Test;

import com.exedio.cope.junit.CopeAssert;

public class NumberFormatConcurrencyTest extends CopeAssert
{
	@Ignore
	@Test public void torment() throws ParseException, InterruptedException
	{
		final NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
		final List<DFThread> threads = new ArrayList<DFThread>();
		threads.add(new DFThread(format, "2,223.44"));
		threads.add(new DFThread(format, "2,224.44"));
		threads.add(new DFThread(format, "2,225.44"));
		threads.add(new DFThread(format, "2,226.44"));
		threads.add(new DFThread(format, "2,227.44"));
		threads.add(new DFThread(format, "2,228.44"));

		for(final DFThread t : threads)
			t.start();
		for(final DFThread t : threads)
			t.join();
	}

	private static class DFThread extends Thread
	{
		private final Number value;
		private final NumberFormat format;
		private final String expected;

		public DFThread(final NumberFormat format, final String string) throws ParseException
		{
			this.format = format;
			//this.format = NumberFormat.getInstance(Locale.ENGLISH);
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
