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
import static org.junit.jupiter.api.Assertions.assertSame;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.Test;

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
		c1.assertV(0, 0, 0);
		c2.assertV(0, 0, 0);
		f.assertV(1);

		// dont put into idle, because its closed
		c1.isValidOnPut = false;
		cp.put(c1);
		assertEquals(0, cp.getInfo().getIdleLevel());
		assertEquals(0, cp.getInfo().getInvalidOnGet());
		assertEquals(1, cp.getInfo().getInvalidOnPut());
		c1.assertV(0, 1, 0);
		c2.assertV(0, 0, 0);
		f.assertV(1);

		// create new because no idle available
		assertSame(c2, cp.get());
		assertEquals(0, cp.getInfo().getIdleLevel());
		assertEquals(0, cp.getInfo().getInvalidOnGet());
		assertEquals(1, cp.getInfo().getInvalidOnPut());
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
		c1.assertV(0, 0, 0);
		c2.assertV(0, 0, 0);
		f.assertV(1);

		// put into idle
		cp.put(c1);
		assertEquals(1, cp.getInfo().getIdleLevel());
		assertEquals(0, cp.getInfo().getInvalidOnGet());
		assertEquals(0, cp.getInfo().getInvalidOnPut());
		c1.assertV(0, 1, 0);
		c2.assertV(0, 0, 0);
		f.assertV(1);

		// create new because c1 timed out
		c1.isValidOnGet = false;
		assertSame(c2, cp.get());
		assertEquals(0, cp.getInfo().getIdleLevel());
		assertEquals(1, cp.getInfo().getInvalidOnGet());
		assertEquals(0, cp.getInfo().getInvalidOnPut());
		c1.assertV(1, 1, 0);
		c2.assertV(0, 0, 0);
		f.assertV(2);
	}

	@SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
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
	@SuppressFBWarnings("BC_UNCONFIRMED_CAST_OF_RETURN_VALUE")
	@Test void testError()
	{
		//noinspection ConstantConditions
		assertFails(() ->
			new Pool<>((Factory)null, null, null),
			NullPointerException.class, "factory");
		final Factory f = new Factory(asList());
		//noinspection ConstantConditions
		assertFails(() ->
			new Pool<>(f, null, null),
			NullPointerException.class, "properties");
		new Pool<>(f, PoolProperties.factory(50).create(Sources.EMPTY), null);
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

	@SuppressWarnings("deprecation")
	private static Pool<Pooled> newPool(final Pool.Factory<Pooled> factory, final int idleLimit, final int idleInitial)
	{
		return new Pool<>(factory, idleLimit, idleInitial, null);
	}
}
