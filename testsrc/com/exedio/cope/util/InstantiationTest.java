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

import java.lang.reflect.Constructor;
import java.text.NumberFormat;
import java.util.Locale;
import org.junit.Ignore;
import org.junit.Test;

public class InstantiationTest
{
	// large numbers needed for JIT to jump in
	private static final int count = 200000;
	private static final int warmup = 50000;

	@Ignore
	@SuppressWarnings("unused")
	@Test public void test() throws ReflectiveOperationException
	{
		{
			long sum = 0;
			for(int i = 0; i<count; i++)
			{
				final long x = System.nanoTime();
				final long y = System.nanoTime();
				//System.out.println("empty      " + formatNanos(y-x));
				if(i>warmup)
					sum += y-x;
			}
			System.out.println("empty      " + formatNanos(sum / (count-warmup)));
		}
		{
			long sum = 0;
			for(int i = 0; i<count; i++)
			{
				final long x = System.nanoTime();
				new MyClass();
				final long y = System.nanoTime();
				//System.out.println("normal     " + formatNanos(y-x));
				if(i>warmup)
					sum += y-x;
			}
			System.out.println("direct     " + formatNanos(sum / (count-warmup)));
		}
		{
			long sum = 0;
			for(int i = 0; i<count; i++)
			{
				final long x = System.nanoTime();
				MyClass.class.newInstance();
				final long y = System.nanoTime();
				//System.out.println("reflection " + formatNanos(y-x));
				if(i>warmup)
					sum += y-x;
			}
			System.out.println("reflection " + formatNanos(sum / (count-warmup)));
		}
		{
			final Constructor<?> cons = MyClass.class.getDeclaredConstructor();
			long sum = 0;
			for(int i = 0; i<count; i++)
			{
				final long x = System.nanoTime();
				cons.newInstance();
				final long y = System.nanoTime();
				//System.out.println("reflect co " + formatNanos(y-x));
				if(i>warmup)
					sum += y-x;
			}
			System.out.println("reflect co " + formatNanos(sum / (count-warmup)));
		}
	}

	static final class MyClass
	{
		// empty
	}

	private static String formatNanos(final long nanos)
	{
		return NumberFormat.getInstance(Locale.ENGLISH).format(nanos);
	}
}
