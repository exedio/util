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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Pool<E>
{
	private static final Logger log = LoggerFactory.getLogger(Pool.class);

	public interface Factory<E>
	{
		E create();
		boolean isValidOnGet(E e);

		/**
		 * If this method returns false, it should also try its best
		 * to release any resources associated with <tt>e</tt>,
		 * because in this case {@link #dispose(Object)} is not called by the pool.
		 */
		boolean isValidOnPut(E e);
		void dispose(E e);
	}

	// TODO: allow changing pool size
	// TODO: implement idle timeout
	//       ensure, that idle items in the pool do
	//       not stay idle for a indefinite time,
	//       but are disposed after a certain time to avoid
	//       running into some idle timeout implemented by the
	//       item itself.
	//       maybe then no ring buffer is needed.

	private final Factory<E> factory;
	private final int idleLimit;
	private final int idleInitial;

	private final E[] idle;
	private int idleLevel, idleFrom, idleTo;
	private final Object lock = new Object();

	private final PoolCounter counter;
	@SuppressFBWarnings("VO_VOLATILE_INCREMENT") private volatile int invalidOnGet = 0;
	@SuppressFBWarnings("VO_VOLATILE_INCREMENT") private volatile int invalidOnPut = 0;

	/**
	 * @deprecated Use {@link #Pool(Factory, PoolProperties, PoolCounter)} instead.
	 */
	@Deprecated
	public Pool(final Factory<E> factory, final int idleLimit, final int idleInitial, final PoolCounter counter)
	{
		this(
				factory,
				source(idleLimit, idleInitial),
				counter);
	}

	@Deprecated
	@SuppressFBWarnings("BC_UNCONFIRMED_CAST_OF_RETURN_VALUE")
	private static PoolProperties source(final int idleLimit, final int idleInitial)
	{
		final java.util.Properties props = new java.util.Properties();
		props.setProperty("idleLimit", String.valueOf(idleLimit));
		props.setProperty("idleInitial", String.valueOf(idleInitial));
		return PoolProperties.factory(50).create(
				Sources.view(props, "Pool#Pool(Factory, int, int, PoolCounter)"));
	}

	public Pool(final Factory<E> factory, final PoolProperties properties, final PoolCounter counter)
	{
		if(factory==null)
			throw new NullPointerException("factory");
		if(properties==null)
			throw new NullPointerException("properties");

		this.factory = factory;
		this.idleLimit = properties.idleLimit;
		this.idleInitial = properties.idleInitial;

		this.idle = idleLimit>0 ? cast(new Object[idleLimit]) : null;

		this.idleLevel = idleInitial;
		this.idleFrom = 0;
		this.idleTo = idleInitial<idleLimit ? idleInitial : 0;
		for(int i = 0; i<idleInitial; i++)
			idle[i] = factory.create();

		this.counter = counter;
	}

	@SuppressWarnings({"unchecked", "static-method"}) // OK: no generic arrays
	private E[] cast(final Object[] o)
	{
		return (E[])o;
	}

	private int inc(int pos)
	{
		pos++;
		return (pos==idle.length) ? 0 : pos;
	}

	public E get()
	{
		E result = null;

		do
		{
			synchronized(lock)
			{
				if(idle!=null && idleLevel>0)
				{
					result = idle[idleFrom];
					idle[idleFrom] = null; // do not reference active items
					idleLevel--;
					idleFrom = inc(idleFrom);
				}
			}
			if(result==null)
				break;

			// Important to do this outside the synchronized block!
			if(factory.isValidOnGet(result))
				break;

			invalidOnGet++;

			result = null;
		}
		while(true);

		// Important to do this outside the synchronized block!
		if(result==null)
			result = factory.create();

		if(counter!=null)
			counter.incrementGet();

		return result;
	}

	/**
	 * TODO: If we want to implement changing item parameters on-the-fly
	 * somewhere in the future, it's important, that client return items
	 * to exactly the same instance of Pool.
	 */
	public void put(final E e)
	{
		if(e==null)
			throw new NullPointerException();

		if(counter!=null)
			counter.incrementPut();

		if(!factory.isValidOnPut(e))
		{
			invalidOnPut++;
			return;
		}

		synchronized(lock)
		{
			if(idle!=null && idleLevel<idle.length)
			{
				idle[idleTo] = e;
				idleLevel++;
				idleTo = inc(idleTo);
				return;
			}
		}

		// Important to do this outside the synchronized block!
		factory.dispose(e);
	}

	public void flush()
	{
		if(idle!=null)
		{
			// make a copy of idle to avoid disposing idle items
			// inside the synchronized block
			final ArrayList<E> copyOfIdle = new ArrayList<>(idle.length);

			synchronized(lock)
			{
				if(idleLevel==0)
					return;

				int f = idleFrom;
				for(int i = 0; i<idleLevel; i++)
				{
					copyOfIdle.add(idle[f]);
					idle[f] = null; // do not reference disposed items
					f = inc(f);
				}
				idleLevel = 0;
				idleFrom = idleTo;
			}

			for(final E e : copyOfIdle)
			{
				try
				{
					factory.dispose(e);
				}
				catch(final Exception | AssertionError ex)
				{
					log.error("on flushing pool", ex);
				}
			}
		}
	}

	public Info getInfo()
	{
		return new Info(
				idleLimit,
				idleInitial,
				idleLevel,
				invalidOnGet,
				invalidOnPut,
				counter!=null ? new PoolCounter(counter) : null);
	}

	public static final class Info
	{
		private final int idleLimit;
		private final int idleInitial;
		private final int idleLevel;
		private final int invalidOnGet;
		private final int invalidOnPut;
		private final PoolCounter counter;

		public Info(
				final int idleLimit,
				final int idleInitial,
				final int idleLevel,
				final int invalidOnGet,
				final int invalidOnPut,
				final PoolCounter counter)
		{
			this.idleLimit = idleLimit;
			this.idleInitial = idleInitial;
			this.idleLevel = idleLevel;
			this.invalidOnGet = invalidOnGet;
			this.invalidOnPut = invalidOnPut;
			this.counter = counter;
		}

		public int getIdleLimit()
		{
			return idleLimit;
		}

		public int getIdleInitial()
		{
			return idleInitial;
		}

		/**
		 * @deprecated Use {@link #getIdleLevel()} instead
		 */
		@Deprecated
		public int getIdleCounter()
		{
			return getIdleLevel();
		}

		public int getIdleLevel()
		{
			return idleLevel;
		}

		public int getInvalidOnGet()
		{
			return invalidOnGet;
		}

		public int getInvalidOnPut()
		{
			return invalidOnPut;
		}

		public PoolCounter getCounter()
		{
			return counter;
		}
	}
}
