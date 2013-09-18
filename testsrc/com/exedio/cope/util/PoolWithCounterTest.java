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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.fail;

import com.exedio.cope.junit.CopeAssert;
import java.util.Iterator;
import java.util.List;
import org.junit.Test;

public class PoolWithCounterTest extends CopeAssert
{
	@SuppressWarnings("static-method")
	@Test public final void testNormal()
	{
		final Pooled c1 = new Pooled();
		final Factory f = new Factory(listg(c1));

		final Pool<Pooled> cp = newPool(f, 1, 0);
		assertIt(cp, 0, 0);

		// get and create
		assertSame(c1, cp.get());
		assertIt(cp, 1, 0);

		// put into idle
		cp.put(c1);
		assertIt(cp, 1, 1);

		// get from idle
		assertSame(c1, cp.get());
		assertIt(cp, 2, 1);

		// put into idle
		cp.put(c1);
		assertIt(cp, 2, 2);
	}

	@SuppressWarnings("static-method")
	@Test public final void testInvalidOnGet()
	{
		final Pooled c1 = new Pooled();
		final Pooled c2 = new Pooled();
		final Factory f = new Factory(listg(c1, c2));

		final Pool<Pooled> cp = newPool(f, 1, 0);
		assertIt(cp, 0, 0);

		// get and create
		assertSame(c1, cp.get());
		assertIt(cp, 1, 0);

		// put into idle
		cp.put(c1);
		assertIt(cp, 1, 1);

		c1.isValidOnGet = false;
		assertSame(c2, cp.get());
		assertIt(cp, 2, 1);
	}

	@SuppressWarnings("static-method")
	@Test public final void testInvalidOnPut()
	{
		final Pooled c1 = new Pooled();
		final Factory f = new Factory(listg(c1));

		final Pool<Pooled> cp = newPool(f, 1, 0);
		assertIt(cp, 0, 0);

		// get and create
		assertSame(c1, cp.get());
		assertIt(cp, 1, 0);

		// put into idle
		c1.isValidOnPut = false;
		try
		{
			cp.put(c1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("invalid on put", e.getMessage());
		}
		assertIt(cp, 1, 1);
	}

	@SuppressWarnings("static-method")
	@Test public final void testCreateFails()
	{
		final Factory f = new Factory(listg(new Pooled()));
		final Pool<Pooled> cp = newPool(f, 1, 0);
		assertIt(cp, 0, 0);

		f.createFails = true;
		try
		{
			cp.get();
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("createFails", e.getMessage());
		}
		assertIt(cp, 0, 0);
	}

	@SuppressWarnings("static-method")
	@Test public final void testDisposeFails()
	{
		final Pooled c1 = new Pooled();
		final Pooled c2 = new Pooled();
		final Factory f = new Factory(listg(c1, c2));
		final Pool<Pooled> cp = newPool(f, 1, 0);
		assertIt(cp, 0, 0);

		// get and create
		assertSame(c1, cp.get());
		assertIt(cp, 1, 0);

		// get and create
		assertSame(c2, cp.get());
		assertIt(cp, 2, 0);

		cp.put(c2);
		assertIt(cp, 2, 1);

		c1.disposeFails = true;
		try
		{
			cp.put(c1);
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("disposeFails", e.getMessage());
		}
		assertIt(cp, 2, 2);
	}

	static class Factory implements Pool.Factory<Pooled>
	{
		final Iterator<Pooled> connections;
		boolean createFails = false;

		Factory(final List<Pooled> connections)
		{
			this.connections = connections.iterator();
		}

		public Pooled create()
		{
			if(createFails)
				throw new RuntimeException("createFails");
			return connections.next();
		}

		public boolean isValidOnGet(final Pooled e)
		{
			return e.isValidOnGet();
		}

		public boolean isValidOnPut(final Pooled e)
		{
			return e.isValidOnPut();
		}

		public void dispose(final Pooled e)
		{
			e.dispose();
		}
	}

	static class Pooled
	{
		boolean isValidOnGet = true;
		boolean isValidOnPut = true;
		boolean disposeFails = false;

		boolean isValidOnGet()
		{
			return isValidOnGet;
		}

		boolean isValidOnPut()
		{
			return isValidOnPut;
		}

		void dispose()
		{
			if(disposeFails)
				throw new RuntimeException("disposeFails");
		}
	}

	private static final void assertIt(final Pool<Pooled> pool, final int getCounter, final int putCounter)
	{
		final PoolCounter poolCounter = pool.getInfo().getCounter();
		assertEquals(getCounter, poolCounter.getGetCounter());
		assertEquals(putCounter, poolCounter.getPutCounter());
	}

	@SuppressWarnings("deprecation")
	private static Pool<Pooled> newPool(final Pool.Factory<Pooled> factory, final int idleLimit, final int idleInitial)
	{
		return new Pool<Pooled>(factory, idleLimit, idleInitial, new PoolCounter());
	}
}
