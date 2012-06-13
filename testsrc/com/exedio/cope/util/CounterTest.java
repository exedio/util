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

import java.util.concurrent.atomic.AtomicLong;

import com.exedio.cope.junit.CopeAssert;

public class CounterTest extends CopeAssert
{
	@SuppressWarnings("unused")
	private long countNaked;
	@SuppressWarnings("unused")
	private volatile long countVolatile;
	private final Object lock = new Object();
	private final AtomicLong atomic = new AtomicLong();

	public void testCount()
	{
		final int N = 10000000;
		for(int j = 0; j<2; j++)
		{
			System.out.println("---------------"+N+"-"+j);
			{
				countNaked = 0;
				final long start = System.currentTimeMillis();
				for(int i = 0; i<N; i++)
				{
					countNaked++;
				}
				final long elapsed = System.currentTimeMillis()-start;
				System.out.println("-------naked        " + elapsed);
			}
			{
				countVolatile = 0;
				final long start = System.currentTimeMillis();
				for(int i = 0; i<N; i++)
				{
					countVolatile++;
				}
				final long elapsed = System.currentTimeMillis()-start;
				System.out.println("-------volatile     " + elapsed);
			}
			{
				atomic.set(0);
				final long start = System.currentTimeMillis();
				for(int i = 0; i<N; i++)
				{
					atomic.incrementAndGet();
				}
				final long elapsed = System.currentTimeMillis()-start;
				System.out.println("-------atomic       " + elapsed);
			}
			{
				countNaked = 0;
				final long start = System.currentTimeMillis();
				for(int i = 0; i<N; i++)
				{
					synchronized(lock)
					{
						countNaked++;
					}
				}
				final long elapsed = System.currentTimeMillis()-start;
				System.out.println("-------synchronized " + elapsed);
			}
		}
	}
}
