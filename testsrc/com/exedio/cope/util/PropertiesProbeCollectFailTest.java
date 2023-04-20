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

import org.junit.jupiter.api.Test;

public class PropertiesProbeCollectFailTest
{
	@Test
	public void testStatic()
	{
		assertFails(
			Static::new,
			IllegalArgumentException.class,
			"@Probe method must be non-static: " +
			"static int " + Static.class.getName() + ".probeStatic()");
	}

	static class Static extends Properties
	{
		@Probe static int probeStatic() { throw new AssertionError(); }

		Static() { super(Sources.EMPTY); }
	}



	@Test
	public void testParams()
	{
		assertFails(
			Params::new,
			IllegalArgumentException.class,
			"@Probe method must have no parameters: " +
			"int " + Params.class.getName() + ".probeParams(int)");
	}

	static class Params extends Properties
	{
		@Probe int probeParams(final int a) { throw new AssertionError(a); }

		Params() { super(Sources.EMPTY); }
	}



	@Test
	public void testCollision()
	{
		assertFails(
			Collision::new,
			IllegalArgumentException.class,
			"@Probe method has duplicate name 'collision': " +
			"int " + Collision.class.getName() + ".probeA() vs. " +
			"int " + Collision.class.getName() + ".probeB()");
	}

	static class Collision extends Properties
	{
		@Probe(name="collision") int probeA() { throw new AssertionError(); }
		@Probe(name="collision") int probeB() { throw new AssertionError(); }

		Collision() { super(Sources.EMPTY); }
	}
}
