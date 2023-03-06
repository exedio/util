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

import static com.exedio.cope.junit.Assert.assertFails;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public class PoolTest
{
	@Test void testSimple()
	{
		final Pooled c1 = new Pooled();
		final Factory f = new Factory(asList(c1));
		f.assertV(0);

		final Pool<Pooled> cp = newPool(f, 1, 0);
		assertEquals(1, cp.getInfo().getIdleLimit());
		assertEquals(0, cp.getInfo().getIdleInitial());
		assertEquals(0, cp.getInfo().getIdleLevel());
		c1.assertV(0, 0, 0);
		f.assertV(0);

		// get and create
		assertSame(c1, cp.get());
		assertEquals(0, cp.getInfo().getIdleLevel());
		c1.assertV(0, 0, 0);
		f.assertV(1);

		// put into idle
		cp.put(c1);
		assertEquals(1, cp.getInfo().getIdleLevel());
		c1.assertV(0, 1, 0);
		f.assertV(1);

		// get from idle
		assertSame(c1, cp.get());
		assertEquals(0, cp.getInfo().getIdleLevel());
		c1.assertV(1, 1, 0);
		f.assertV(1);

		// put into idle
		cp.put(c1);
		assertEquals(1, cp.getInfo().getIdleLevel());
		c1.assertV(1, 2, 0);
		f.assertV(1);

		// get from idle with other autoCommit
		assertSame(c1, cp.get());
		assertEquals(0, cp.getInfo().getIdleLevel());
		c1.assertV(2, 2, 0);
		f.assertV(1);

		assertEquals(0, cp.getInfo().getInvalidOnGet());
		assertEquals(0, cp.getInfo().getInvalidOnPut());
	}

	@Test void testOverflow()
	{
		final Pooled c1 = new Pooled();
		final Pooled c2 = new Pooled();
		final Factory f = new Factory(asList(c1, c2));
		f.assertV(0);

		final Pool<Pooled> cp = newPool(f, 1, 0);
		assertEquals(1, cp.getInfo().getIdleLimit());
		assertEquals(0, cp.getInfo().getIdleInitial());
		assertEquals(0, cp.getInfo().getIdleLevel());
		c1.assertV(0, 0, 0);
		c2.assertV(0, 0, 0);
		f.assertV(0);

		// get and create
		assertSame(c1, cp.get());
		assertEquals(0, cp.getInfo().getIdleLevel());
		c1.assertV(0, 0, 0);
		c2.assertV(0, 0, 0);
		f.assertV(1);

		// get and create (2)
		assertSame(c2, cp.get());
		assertEquals(0, cp.getInfo().getIdleLevel());
		c1.assertV(0, 0, 0);
		c2.assertV(0, 0, 0);
		f.assertV(2);

		// put into idle
		cp.put(c1);
		assertEquals(1, cp.getInfo().getIdleLevel());
		c1.assertV(0, 1, 0);
		c2.assertV(0, 0, 0);
		f.assertV(2);

		// put and close
		cp.put(c2);
		assertEquals(1, cp.getInfo().getIdleLevel());
		c1.assertV(0, 1, 0);
		c2.assertV(0, 1, 1);
		f.assertV(2);

		assertEquals(0, cp.getInfo().getInvalidOnGet());
		assertEquals(0, cp.getInfo().getInvalidOnPut());
	}

	@Test void testPrecendence()
	{
		final Pooled c1 = new Pooled();
		final Pooled c2 = new Pooled();
		final Factory f = new Factory(asList(c1, c2));
		f.assertV(0);

		final Pool<Pooled> cp = newPool(f, 2, 0);
		assertEquals(2, cp.getInfo().getIdleLimit());
		assertEquals(0, cp.getInfo().getIdleInitial());
		assertEquals(0, cp.getInfo().getIdleLevel());
		c1.assertV(0, 0, 0);
		c2.assertV(0, 0, 0);
		f.assertV(0);

		// get and create
		assertSame(c1, cp.get());
		assertEquals(0, cp.getInfo().getIdleLevel());
		c1.assertV(0, 0, 0);
		c2.assertV(0, 0, 0);
		f.assertV(1);

		// get and create (2)
		assertSame(c2, cp.get());
		assertEquals(0, cp.getInfo().getIdleLevel());
		c1.assertV(0, 0, 0);
		c2.assertV(0, 0, 0);
		f.assertV(2);

		// put into idle
		cp.put(c1);
		assertEquals(1, cp.getInfo().getIdleLevel());
		c1.assertV(0, 1, 0);
		c2.assertV(0, 0, 0);
		f.assertV(2);

		// put into idle (2)
		cp.put(c2);
		assertEquals(2, cp.getInfo().getIdleLevel());
		c1.assertV(0, 1, 0);
		c2.assertV(0, 1, 0);
		f.assertV(2);

		// get from idle, fifo
		assertSame(c1, cp.get());
		assertEquals(1, cp.getInfo().getIdleLevel());
		c1.assertV(1, 1, 0);
		c2.assertV(0, 1, 0);
		f.assertV(2);

		// get from idle, fifo
		assertSame(c2, cp.get());
		assertEquals(0, cp.getInfo().getIdleLevel());
		c1.assertV(1, 1, 0);
		c2.assertV(1, 1, 0);
		f.assertV(2);

		// put into idle
		cp.put(c2);
		assertEquals(1, cp.getInfo().getIdleLevel());
		c1.assertV(1, 1, 0);
		c2.assertV(1, 2, 0);
		f.assertV(2);

		// get from idle
		assertSame(c2, cp.get());
		assertEquals(0, cp.getInfo().getIdleLevel());
		c1.assertV(1, 1, 0);
		c2.assertV(2, 2, 0);
		f.assertV(2);

		// put into idle
		cp.put(c1);
		assertEquals(1, cp.getInfo().getIdleLevel());
		c1.assertV(1, 2, 0);
		c2.assertV(2, 2, 0);
		f.assertV(2);

		// get from idle
		assertSame(c1, cp.get());
		assertEquals(0, cp.getInfo().getIdleLevel());
		c1.assertV(2, 2, 0);
		c2.assertV(2, 2, 0);
		f.assertV(2);

		// put into idle
		cp.put(c2);
		assertEquals(1, cp.getInfo().getIdleLevel());
		c1.assertV(2, 2, 0);
		c2.assertV(2, 3, 0);
		f.assertV(2);

		// get from idle
		assertSame(c2, cp.get());
		assertEquals(0, cp.getInfo().getIdleLevel());
		c1.assertV(2, 2, 0);
		c2.assertV(3, 3, 0);
		f.assertV(2);

		assertEquals(0, cp.getInfo().getInvalidOnGet());
		assertEquals(0, cp.getInfo().getInvalidOnPut());
	}

	@Test void testIdleInitial()
	{
		final Pooled c1 = new Pooled();
		final Factory f = new Factory(asList(c1));
		f.assertV(0);

		final Pool<Pooled> cp = newPool(f, 1, 1);
		assertEquals(1, cp.getInfo().getIdleLimit());
		assertEquals(1, cp.getInfo().getIdleInitial());
		assertEquals(1, cp.getInfo().getIdleLevel());
		c1.assertV(0, 0, 0);
		f.assertV(1); // already created

		// get from idle
		assertSame(c1, cp.get());
		assertEquals(0, cp.getInfo().getIdleLevel());
		c1.assertV(1, 0, 0);
		f.assertV(1);

		cp.put(c1);
		assertEquals(1, cp.getInfo().getIdleLevel());
		c1.assertV(1, 1, 0);
		f.assertV(1);

		assertEquals(0, cp.getInfo().getInvalidOnGet());
		assertEquals(0, cp.getInfo().getInvalidOnPut());
	}

	@Test void testIdleInitialNotFull()
	{
		final Pooled c1 = new Pooled();
		final Factory f = new Factory(asList(c1));
		f.assertV(0);

		final Pool<Pooled> cp = newPool(f, 2, 1);
		assertEquals(2, cp.getInfo().getIdleLimit());
		assertEquals(1, cp.getInfo().getIdleInitial());
		assertEquals(1, cp.getInfo().getIdleLevel());
		c1.assertV(0, 0, 0);
		f.assertV(1); // already created

		// get from idle
		assertSame(c1, cp.get());
		assertEquals(0, cp.getInfo().getIdleLevel());
		c1.assertV(1, 0, 0);
		f.assertV(1);

		cp.put(c1);
		assertEquals(1, cp.getInfo().getIdleLevel());
		c1.assertV(1, 1, 0);
		f.assertV(1);

		assertEquals(0, cp.getInfo().getInvalidOnGet());
		assertEquals(0, cp.getInfo().getInvalidOnPut());
	}

	@Test void testIsValidOnPut()
	{
		final Pooled c1 = new Pooled();
		final Pooled c2 = new Pooled();
		final Factory f = new Factory(asList(c1, c2));
		f.assertV(0);

		final Pool<Pooled> cp = newPool(f, 1, 0);
		final Counter iog = (Counter)meter(METER_NAME + ".invalid", Tags.of("some", "tag", "operation", "get"));
		final Counter iop = (Counter)meter(METER_NAME + ".invalid", Tags.of("some", "tag", "operation", "put"));
		assertEquals(0, iog.count());
		assertEquals(0, iop.count());
		assertNotSame(iog, iop);
		assertEquals(1, cp.getInfo().getIdleLimit());
		assertEquals(0, cp.getInfo().getIdleInitial());
		assertEquals(0, cp.getInfo().getIdleLevel());
		assertEquals(0, cp.getInfo().getInvalidOnGet());
		assertEquals(0, cp.getInfo().getInvalidOnPut());
		assertEquals(0, iog.count());
		assertEquals(0, iop.count());
		c1.assertV(0, 0, 0);
		c2.assertV(0, 0, 0);
		f.assertV(0);

		// get and create
		assertSame(c1, cp.get());
		assertEquals(0, cp.getInfo().getIdleLevel());
		assertEquals(0, cp.getInfo().getInvalidOnGet());
		assertEquals(0, cp.getInfo().getInvalidOnPut());
		assertEquals(0, iog.count());
		assertEquals(0, iop.count());
		c1.assertV(0, 0, 0);
		c2.assertV(0, 0, 0);
		f.assertV(1);

		// don't put into idle, because its closed
		c1.isValidOnPut = false;
		cp.put(c1);
		assertEquals(0, cp.getInfo().getIdleLevel());
		assertEquals(0, cp.getInfo().getInvalidOnGet());
		assertEquals(1, cp.getInfo().getInvalidOnPut());
		assertEquals(0, iog.count());
		assertEquals(1, iop.count());
		c1.assertV(0, 1, 0);
		c2.assertV(0, 0, 0);
		f.assertV(1);

		// create new because no idle available
		assertSame(c2, cp.get());
		assertEquals(0, cp.getInfo().getIdleLevel());
		assertEquals(0, cp.getInfo().getInvalidOnGet());
		assertEquals(1, cp.getInfo().getInvalidOnPut());
		assertEquals(0, iog.count());
		assertEquals(1, iop.count());
		c1.assertV(0, 1, 0);
		c2.assertV(0, 0, 0);
		f.assertV(2);
	}

	@Test void testFlush()
	{
		final Pooled c1 = new Pooled();
		final Pooled c2 = new Pooled();
		final Factory f = new Factory(asList(c1, c2));
		f.assertV(0);

		final Pool<Pooled> cp = newPool(f, 1, 0);
		assertEquals(1, cp.getInfo().getIdleLimit());
		assertEquals(0, cp.getInfo().getIdleInitial());
		assertEquals(0, cp.getInfo().getIdleLevel());
		c1.assertV(0, 0, 0);
		c2.assertV(0, 0, 0);
		f.assertV(0);

		// get and create
		assertSame(c1, cp.get());
		assertEquals(0, cp.getInfo().getIdleLevel());
		c1.assertV(0, 0, 0);
		c2.assertV(0, 0, 0);
		f.assertV(1);

		// put into idle
		cp.put(c1);
		assertEquals(1, cp.getInfo().getIdleLevel());
		c1.assertV(0, 1, 0);
		c2.assertV(0, 0, 0);
		f.assertV(1);

		// flush closes c1
		cp.flush();
		assertEquals(0, cp.getInfo().getIdleLevel());
		c1.assertV(0, 1, 1);
		c2.assertV(0, 0, 0);
		f.assertV(1);

		// create new because flushed
		assertSame(c2, cp.get());
		assertEquals(0, cp.getInfo().getIdleLevel());
		c1.assertV(0, 1, 1);
		c2.assertV(0, 0, 0);
		f.assertV(2);

		assertEquals(0, cp.getInfo().getInvalidOnGet());
		assertEquals(0, cp.getInfo().getInvalidOnPut());
	}

	@Test void testNoPool()
	{
		final Pooled c1 = new Pooled();
		final Pooled c2 = new Pooled();
		final Factory f = new Factory(asList(c1, c2));
		f.assertV(0);

		final Pool<Pooled> cp = newPool(f, 0, 0);
		assertEquals(0, cp.getInfo().getIdleLimit());
		assertEquals(0, cp.getInfo().getIdleInitial());
		assertEquals(0, cp.getInfo().getIdleLevel());
		c1.assertV(0, 0, 0);
		c2.assertV(0, 0, 0);
		f.assertV(0);

		// get and create
		assertSame(c1, cp.get());
		assertEquals(0, cp.getInfo().getIdleLevel());
		c1.assertV(0, 0, 0);
		c2.assertV(0, 0, 0);
		f.assertV(1);

		// put and close because no idle
		cp.put(c1);
		assertEquals(0, cp.getInfo().getIdleLevel());
		c1.assertV(0, 1, 1);
		c2.assertV(0, 0, 0);
		f.assertV(1);

		// create new because no idle
		assertSame(c2, cp.get());
		assertEquals(0, cp.getInfo().getIdleLevel());
		c1.assertV(0, 1, 1);
		c2.assertV(0, 0, 0);
		f.assertV(2);

		assertEquals(0, cp.getInfo().getInvalidOnGet());
		assertEquals(0, cp.getInfo().getInvalidOnPut());
	}

	@Test void testValidOnGet()
	{
		final Pooled c1 = new Pooled();
		final Pooled c2 = new Pooled();
		final Factory f = new Factory(asList(c1, c2));
		f.assertV(0);

		final Pool<Pooled> cp = newPool(f, 1, 0);
		final Counter iog = (Counter)meter(METER_NAME + ".invalid", Tags.of("some", "tag", "operation", "get"));
		final Counter iop = (Counter)meter(METER_NAME + ".invalid", Tags.of("some", "tag", "operation", "put"));
		assertEquals(0, iog.count());
		assertEquals(0, iop.count());
		assertNotSame(iog, iop);
		assertEquals(1, cp.getInfo().getIdleLimit());
		assertEquals(0, cp.getInfo().getIdleInitial());
		assertEquals(0, cp.getInfo().getIdleLevel());
		assertEquals(0, cp.getInfo().getInvalidOnGet());
		assertEquals(0, cp.getInfo().getInvalidOnPut());
		c1.assertV(0, 0, 0);
		c2.assertV(0, 0, 0);
		f.assertV(0);

		// get and create
		assertSame(c1, cp.get());
		assertEquals(0, cp.getInfo().getIdleLevel());
		assertEquals(0, cp.getInfo().getInvalidOnGet());
		assertEquals(0, cp.getInfo().getInvalidOnPut());
		assertEquals(0, iog.count());
		assertEquals(0, iop.count());
		c1.assertV(0, 0, 0);
		c2.assertV(0, 0, 0);
		f.assertV(1);

		// put into idle
		cp.put(c1);
		assertEquals(1, cp.getInfo().getIdleLevel());
		assertEquals(0, cp.getInfo().getInvalidOnGet());
		assertEquals(0, cp.getInfo().getInvalidOnPut());
		assertEquals(0, iog.count());
		assertEquals(0, iop.count());
		c1.assertV(0, 1, 0);
		c2.assertV(0, 0, 0);
		f.assertV(1);

		// create new because c1 timed out
		c1.isValidOnGet = false;
		assertSame(c2, cp.get());
		assertEquals(0, cp.getInfo().getIdleLevel());
		assertEquals(1, cp.getInfo().getInvalidOnGet());
		assertEquals(0, cp.getInfo().getInvalidOnPut());
		assertEquals(1, iog.count());
		assertEquals(0, iop.count());
		c1.assertV(1, 1, 0);
		c2.assertV(0, 0, 0);
		f.assertV(2);
	}

	@Test void testErrorDeprecated()
	{
		assertFails(() ->
			newPool(null, 0, 0),
			NullPointerException.class, "factory");
		final Factory f = new Factory(asList());
		assertFails(() ->
			newPool(null, -1, 0),
			IllegalPropertiesException.class,
					"property idleLimit in Pool#Pool(Factory, int, int, PoolCounter) " +
					"must be an integer greater or equal 0, "+
					"but was -1");
		assertFails(() ->
			newPool(null, -1, -1),
			IllegalPropertiesException.class,
					"property idleInitial in Pool#Pool(Factory, int, int, PoolCounter) " +
					"must be an integer greater or equal 0, " +
					"but was -1");
		assertFails(() ->
			newPool(null, 0, 1),
			IllegalPropertiesException.class,
					"property idleInitial in Pool#Pool(Factory, int, int, PoolCounter) " +
					"must be less or equal idleLimit=0, " +
					"but was 1");
		newPool(f, 0, 0);
	}

	@SuppressWarnings("unused")
	@Test void testError()
	{
		assertFails(() ->
			new Pool<>((Factory)null, null, null),
			NullPointerException.class, "factory");
		final Factory f = new Factory(asList());
		assertFails(() ->
			new Pool<>(f, null, null),
			NullPointerException.class, "properties");
		final Pool<Pooled> p =
				new Pool<>(f, PoolProperties.factory(50).create(Sources.EMPTY), null);
		assertFails(
				() -> p.put(null),
				NullPointerException.class, null);
	}

	@Test void testRegisterError()
	{
		final Factory f = new Factory(asList());
		final Pool<Pooled> p =
				new Pool<>(f, PoolProperties.factory(50).create(Sources.EMPTY), null);
		assertFails(
				() -> p.register(null, null, null),
				NullPointerException.class, "name");
		assertFails(
				() -> p.register("", null, null),
				IllegalArgumentException.class, "name must not be empty");
		assertFails(
				() -> p.register("myName", null, null),
				NullPointerException.class, "tags");
		assertFails(
				() -> p.register("myName", Tags.empty(), null),
				NullPointerException.class, "registry");
	}

	static class Factory implements Pool.Factory<Pooled>
	{
		final Iterator<Pooled> connections;
		int createCount = 0;

		Factory(final List<Pooled> connections)
		{
			this.connections = connections.iterator();
		}

		void assertV(final int createCount)
		{
			assertEquals(createCount, this.createCount);
		}

		@Override
		public Pooled create()
		{
			createCount++;
			return connections.next();
		}

		@Override
		public boolean isValidOnGet(final Pooled e)
		{
			return e.isValidOnGet();
		}

		@Override
		public boolean isValidOnPut(final Pooled e)
		{
			return e.isValidOnPut();
		}

		@Override
		public void dispose(final Pooled e)
		{
			e.dispose();
		}
	}

	static class Pooled
	{
		boolean isValidOnGet = true;
		int isValidOnGetCount = 0;
		boolean isValidOnPut = true;
		int isValidOnPutCount = 0;
		int disposeCount = 0;

		void assertV(final int isValidOnGetCount, final int isValidOnPutCount, final int disposeCount)
		{
			assertEquals(isValidOnGetCount, this.isValidOnGetCount);
			assertEquals(isValidOnPutCount, this.isValidOnPutCount);
			assertEquals(disposeCount, this.disposeCount);
		}

		boolean isValidOnGet()
		{
			isValidOnGetCount++;
			return isValidOnGet;
		}

		boolean isValidOnPut()
		{
			isValidOnPutCount++;
			return isValidOnPut;
		}

		void dispose()
		{
			disposeCount++;
		}
	}

	private Pool<Pooled> newPool(final Pool.Factory<Pooled> factory, final int idleLimit, final int idleInitial)
	{
		final Pool<Pooled> result = newPool(factory, idleLimit, idleInitial, null);
		result.register(METER_NAME, Tags.of("some", "tag"), meterRegistry);
		return result;
	}

	private final MeterRegistry meterRegistry = new PrometheusMeterRegistry(key -> null);

	private static final String METER_NAME = PoolWithCounterTest.class.getName();

	static <P> Pool<P> newPool(
			final Pool.Factory<P> factory,
			final int idleLimit, final int idleInitial,
			final PoolCounter poolCounter)
	{
		final java.util.Properties props = new java.util.Properties();
		props.setProperty("idleLimit", String.valueOf(idleLimit));
		props.setProperty("idleInitial", String.valueOf(idleInitial));
		final PoolProperties p = PoolProperties.factory(50).create(
				Sources.view(props, "Pool#Pool(Factory, int, int, PoolCounter)"));
		assertEquals(idleLimit, p.getIdleLimit());
		assertEquals(idleInitial, p.getIdleInitial());
		return new Pool<>(factory, p, poolCounter);
	}

	private Meter meter(
			final String name,
			final Tags tags)
	{
		return meter(name, tags, meterRegistry);
	}

	static Meter meter(
			final String name,
			final Tags tags,
			final MeterRegistry registry)
	{
		Meter result = null;
		for(final Meter m : registry.getMeters())
		{
			final Meter.Id id = m.getId();
			if(id.getName().equals(name) &&
				Tags.of(id.getTags()).equals(tags))
			{
				assertNotNull(      id.getDescription(), "description: " + name);
				assertNotEquals("", id.getDescription(), "description: " + name);
				assertNull(result);
				result = m;
			}
		}
		if(result==null)
			throw new AssertionFailedError("not found: >" + name + "< " + tags);
		return result;
	}
}
