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

import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"FieldCanBeLocal", "NonAtomicOperationOnVolatileField"})
public class CounterTest
{
	@SuppressWarnings({"unused", "FieldAccessedSynchronizedAndUnsynchronized"})
	private long countNaked;
	@SuppressWarnings("VolatileLongOrDoubleField")
	private volatile long countVolatile;
	private VolatileLong countVolatileObject = null;
	private final Object lock = new Object();
	private final AtomicLong atomic = new AtomicLong();

	@Disabled
	@Test final void testCount()
	{
		final int N = 10_000_000;
		for(int j = 0; j<8; j++)
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
				System.out.println("-------atom incGet  " + elapsed/1_000_000);
			}
			{
				atomic.set(0);
				final long start = System.nanoTime();
				for(int i = 0; i<N; i++)
				{
					atomic.getAndIncrement();
				}
				final long elapsed = System.nanoTime()-start;
				System.out.println("-------atom getInc  " + elapsed/1_000_000);
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
		@SuppressWarnings("VolatileLongOrDoubleField")
		private volatile long value = 0;

		VolatileLong()
		{
			// TODO Auto-generated constructor stub
		}

		void inc()
		{
			value++;
		}
	}

	@Disabled
	@Test final void testNow()
	{
		final int N = 10_000_000;
		for(int j = 0; j<2; j++)
		{
			System.out.println("---------------"+N+"-"+j);
			{
				final long start = System.nanoTime();
				for(int i = 0; i<N; i++)
				{
					System.currentTimeMillis();
				}
				final long elapsed = System.nanoTime()-start;
				System.out.println("-------System.currentTimeMillis() " + elapsed/1_000_000);
			}
			{
				final long start = System.nanoTime();
				for(int i = 0; i<N; i++)
				{
					new Date();
				}
				final long elapsed = System.nanoTime()-start;
				System.out.println("-------new Date()                 " + elapsed/1_000_000);
			}
			{
				final long start = System.nanoTime();
				for(int i = 0; i<N; i++)
				{
					Instant.now();
				}
				final long elapsed = System.nanoTime()-start;
				System.out.println("-------Instant.now()              " + elapsed/1_000_000);
			}
			{
				final Clock clock = Clock.systemUTC();
				final long start = System.nanoTime();
				for(int i = 0; i<N; i++)
				{
					Instant.now(clock);
				}
				final long elapsed = System.nanoTime()-start;
				System.out.println("-------Instant.now(clockUTC)      " + elapsed/1_000_000);
			}
			{
				final Clock clock = Clock.systemDefaultZone();
				final long start = System.nanoTime();
				for(int i = 0; i<N; i++)
				{
					Instant.now(clock);
				}
				final long elapsed = System.nanoTime()-start;
				System.out.println("-------Instant.now(clockDefault)  " + elapsed/1_000_000);
			}
		}
	}

	@Disabled
	@Test final void testHashCode()
	{
		final int N = 100_000_000;
		int x = 0; // prevents compiler from omitting calls
		for(int j = 0; j<8; j++)
		{
			System.out.println("---------------"+N+"-"+j);
			{
				final Object o = new Object();
				final long start = System.nanoTime();
				for(int i = 0; i<N; i++)
				{
					x += o.hashCode();
				}
				final long elapsed = System.nanoTime()-start;
				System.out.println("-------Object " + elapsed/1_000_000);
			}
			{
				final Object o = new Object();
				final long start = System.nanoTime();
				for(int i = 0; i<N; i++)
				{
					x += System.identityHashCode(o);
				}
				final long elapsed = System.nanoTime()-start;
				System.out.println("-------System " + elapsed/1_000_000);
			}
		}
		System.out.println("-------x " + x);
	}
}
