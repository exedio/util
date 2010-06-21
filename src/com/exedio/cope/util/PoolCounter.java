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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simulates the behaviour of a set of pools with different pool idle limits,
 * and collects statistics about the efficiency of such pools.
 * Useful for determining pool idle limits.
 *
 * @author Ralf Wiebicke
 */
public final class PoolCounter
{
	private final Object lock = new Object();

	private final int[] idleLimitA;
	private final int[] idleA;
	private final int[] idleMaxA;
	private final int[] createA;
	private final int[] destroyA;
	private int count;

	int get = 0;
	int put = 0;

	public PoolCounter()
	{
		this(1,2,4,6,8,10,15,20,25,30,40,50,60,70,80,90,100,150,200,250,300,400,500,600,700,800,900,1000);
	}

	public PoolCounter(final int... idleLimits)
	{
		if(idleLimits.length<1)
			throw new IllegalArgumentException("number of idleLimits must be at least 1");

		for(final int s : idleLimits)
		{
			if(s<=0)
				throw new IllegalArgumentException("idleLimits must be greater than zero");
		}

		for(int i=1; i<idleLimits.length; i++)
		{
			if(idleLimits[i-1]>=idleLimits[i])
				throw new IllegalArgumentException("idleLimits must be strictly monotonic increasing");
		}

		this.idleLimitA = copy(idleLimits);
		this.idleA    = new int[idleLimits.length];
		this.idleMaxA = new int[idleLimits.length];
		this.createA  = new int[idleLimits.length];
		this.destroyA = new int[idleLimits.length];
		this.count = 1;
	}

	public PoolCounter(final PoolCounter source)
	{
		this.idleLimitA = source.idleLimitA;
		this.idleA    = copy(source.idleA);
		this.idleMaxA = copy(source.idleMaxA);
		this.createA  = copy(source.createA);
		this.destroyA = copy(source.destroyA);
		this.get = source.get;
		this.put = source.put;
		this.count = source.count;
	}

	private static int[] copy(final int[] array)
	{
		final int[] result = new int[array.length];
		for(int i = 0; i<array.length; i++)
			result[i] = array[i];
		return result;
	}

	public void incrementGet()
	{
		synchronized(lock)
		{
			get++;

			final int count = this.count;
			for(int i = 0; i<count; i++)
			{
				final int idleI = idleA[i];

				if(idleI>0)
					idleA[i] = idleI-1;
				else
					createA[i]++;
			}
		}
	}

	public void incrementPut()
	{
		synchronized(lock)
		{
			put++;

			int count = this.count;
			for(int i = 0; i<count; i++)
			{
				int idleI = idleA[i];

				if(idleI<idleLimitA[i])
				{
					idleA[i] = ++idleI;

					if(idleI>idleMaxA[i])
						idleMaxA[i] = idleI;
				}
				else
				{
					final int destroyI = destroyA[i];

					if(destroyI==0 && count<idleLimitA.length)
					{
						assert i==(count-1);
						idleA   [count] = idleI;
						idleMaxA[count] = idleMaxA[i];
						createA [count] = createA[i];
						destroyA[count] = 0/*equals to destroy[i]*/;
						count++; // causes another iteration
						this.count = count;
					}
					destroyA[i] = destroyI+1;
				}
			}
		}
	}

	public List<Pool> getPools()
	{
		final ArrayList<Pool> result = new ArrayList<Pool>(idleLimitA.length);
		synchronized(lock)
		{
			final int count = this.count;
			for(int i = 0; i<count; i++)
				result.add(new Pool(idleLimitA[i], idleA[i], idleMaxA[i], createA[i], destroyA[i]));
		}
		return Collections.unmodifiableList(result);
	}

	public int getGetCounter()
	{
		return get;
	}

	public int getPutCounter()
	{
		return put;
	}

	public final class Pool
	{
		private final int idleLimit;
		private final int idle;
		private final int idleMax;
		private final int create;
		private final int destroy;

		Pool(final int idleLimit, final int idle, final int idleMax, final int create, final int destroy)
		{
			this.idleLimit = idleLimit;
			this.idle = idle;
			this.idleMax = idleMax;
			this.create = create;
			this.destroy = destroy;

			assert idleLimit>0;
			assert idle>=0;
			assert idle<=idleLimit;
			assert idleMax>=0;
			assert idleMax>=idle;
			assert idleMax<=idleLimit;
			assert create>=0;
			assert destroy>=0;
		}

		/**
		 * @deprecated renamed to {@link #getIdleLimit()}.
		 */
		@Deprecated
		public int getSize()
		{
			return getIdleLimit();
		}

		public int getIdleLimit()
		{
			return idleLimit;
		}

		public int getIdleCount()
		{
			return idle;
		}

		public int getIdleCountMax()
		{
			return idleMax;
		}

		public int getCreateCounter()
		{
			return create;
		}

		public int getDestroyCounter()
		{
			return destroy;
		}

		public boolean isConsistent()
		{
			return (get - put) == (create - destroy - idle);
		}

		public int getLoss()
		{
			final int getCounter = PoolCounter.this.get;
			return (getCounter==0) ? 0 : ((100*destroy)/getCounter);
		}
	}
}
