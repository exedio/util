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

import static com.exedio.cope.util.Check.requireNonEmpty;
import static java.util.Objects.requireNonNull;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.noop.NoopCounter;
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
		 * to release any resources associated with {@code e},
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

	private Counter get = noopCounter;
	private Counter put = noopCounter;
	private final PoolCounter counter;
	private Counter invalidOnGet = noopCounter;
	private Counter invalidOnPut = noopCounter;

	public Pool(final Factory<E> factory, final PoolProperties properties, final PoolCounter counter)
	{
		requireNonNull(factory, "factory");
		requireNonNull(properties, "properties");

		this.factory = factory;
		this.idleLimit = properties.idleLimit;
		this.idleInitial = properties.idleInitial;

		this.idle = idleLimit>0 ? cast(new Object[idleLimit]) : null;

		this.idleLevel = idleInitial;
		this.idleFrom = 0;
		this.idleTo = idleInitial<idleLimit ? idleInitial : 0;
		for(int i = 0; i<idleInitial; i++)
			//noinspection DataFlowIssue OK: is not null if idleLimit>0
			idle[i] = factory.create();

		this.counter = counter;
	}

	@SuppressWarnings({"unchecked", "SuspiciousArrayCast"}) // OK: no generic arrays
	private E[] cast(final Object[] o)
	{
		return (E[])o;
	}

	public void register(
			final String name,
			final Tags tags,
			final MeterRegistry registry)
	{
		requireNonEmpty(name, "name");
		requireNonNull(tags, "tags");
		requireNonNull(registry, "registry");

		final Counter.Builder usage = Counter.builder(name + ".usage").
				tags(tags).
				description("Pool#[get|put]");
		final Counter.Builder invalid = Counter.builder(name + ".invalid").
				tags(tags).
				description("Factory#isValidOn[Get|Put]");

		final Counter get          = usage  .tag("operation", "get").register(registry);
		final Counter put          = usage  .tag("operation", "put").register(registry);
		final Counter invalidOnGet = invalid.tag("operation", "get").register(registry);
		final Counter invalidOnPut = invalid.tag("operation", "put").register(registry);

		// separate assignments make method as atomic as possible
		this.get = get;
		this.put = put;
		this.invalidOnGet = invalidOnGet;
		this.invalidOnPut = invalidOnPut;
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

			invalidOnGet.increment();

			result = null;
		}
		while(true);

		// Important to do this outside the synchronized block!
		if(result==null)
			result = factory.create();

		get.increment();
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
		requireNonNull(e);

		put.increment();
		if(counter!=null)
			counter.incrementPut();

		if(!factory.isValidOnPut(e))
		{
			invalidOnPut.increment();
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
		final int idleLevel;
		synchronized(lock)
		{
			idleLevel = this.idleLevel;
		}
		return new Info(
				idleLimit,
				idleInitial,
				idleLevel,
				invalidOnGet,
				invalidOnPut,
				counter!=null ? new PoolCounter(counter) : null);
	}

	@SuppressWarnings("ClassCanBeRecord")
	public static final class Info
	{
		private final int idleLimit;
		private final int idleInitial;
		private final int idleLevel;
		private final int invalidOnGet;
		private final int invalidOnPut;
		private final PoolCounter counter;

		Info(
				final int idleLimit,
				final int idleInitial,
				final int idleLevel,
				final Counter invalidOnGet,
				final Counter invalidOnPut,
				final PoolCounter counter)
		{
			this(idleLimit, idleInitial, idleLevel, count(invalidOnGet), count(invalidOnPut), counter);
		}

		private static int count(final Counter counter)
		{
			final double d = counter.count();
			final long l = Math.round(d);
			//noinspection FloatingPointEquality OK: tests backward conversion
			if(l!=d)
				throw new IllegalStateException(counter.getId().toString() + '/' + d);
			return Math.toIntExact(l);
		}

		/**
		 * @deprecated Do not use this class anymore, use micrometer instead.
		 */
		@Deprecated
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

	private static final Counter noopCounter = new NoopCounter(new Meter.Id(
			Pool.class.getName(),
			Tags.empty(),
			null, null,
			Meter.Type.COUNTER));
}
