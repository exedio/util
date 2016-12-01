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

import com.exedio.cope.junit.CopeAssert;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Ignore;
import org.junit.Test;

public class CounterTest extends CopeAssert
{
	@SuppressWarnings("unused")
	private long countNaked;
	@SuppressFBWarnings("VO_VOLATILE_INCREMENT")
	private volatile long countVolatile;
	private VolatileLong countVolatileObject = null;
	private final Object lock = new Object();
	private final AtomicLong atomic = new AtomicLong();

	@Ignore
	@Test public final void testCount()
	{
		final int N = 10_000_000;
		for(int j = 0; j<2; j++)
		{
			System.out.println("---------------"+N+"-"+j);
			{
				countNaked = 0;
				final long start = System.nanoTime();
				for(int i = 0; i<N; i++)
				{
					countNaked++;
				}
				final long elapsed = System.nanoTime()-start;
				System.out.println("-------naked        " + elapsed/1_000_000);
			}
			{
				countVolatile = 0;
				final long start = System.nanoTime();
				for(int i = 0; i<N; i++)
				{
					countVolatile++;
				}
				final long elapsed = System.nanoTime()-start;
				System.out.println("-------volatile     " + elapsed/1_000_000);
			}
			{
				countVolatileObject = new VolatileLong();
				final long start = System.nanoTime();
				for(int i = 0; i<N; i++)
				{
					countVolatileObject.inc();
				}
				final long elapsed = System.nanoTime()-start;
				System.out.println("-------volatileObj  " + elapsed/1_000_000);
			}
			{
				atomic.set(0);
				final long start = System.nanoTime();
				for(int i = 0; i<N; i++)
				{
					atomic.incrementAndGet();
				}
				final long elapsed = System.nanoTime()-start;
				System.out.println("-------atomic       " + elapsed/1_000_000);
			}
			{
				countNaked = 0;
				final long start = System.nanoTime();
				for(int i = 0; i<N; i++)
				{
					synchronized(lock)
					{
						countNaked++;
					}
				}
				final long elapsed = System.nanoTime()-start;
				System.out.println("-------synchronized " + elapsed/1_000_000);
			}
		}
	}

	private static final class VolatileLong
	{
		@SuppressFBWarnings("VO_VOLATILE_INCREMENT")
		private volatile long value = 0;

		public VolatileLong()
		{
			// TODO Auto-generated constructor stub
		}

		void inc()
		{
			value++;
		}
	}
}
