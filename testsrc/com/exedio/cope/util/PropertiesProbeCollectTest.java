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

import static com.exedio.cope.junit.CopeAssert.assertUnmodifiable;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.junit.Test;

public class PropertiesProbeCollectTest
{
	@Test
	public void testOrder() throws Exception
	{
		final Order props = new Order();

		assertEquals(asList(
				wrap("A", "OrderA"),
				wrap("B", "OrderB"),
				wrap("C", "OrderC"),
				wrap("D", "OrderD")),
				wrap(props.getProbes()));
	}

	static class Order extends Properties
	{
		@Probe String probeB() { return "resultOrderB"; }
		@Probe String probeA() { return "resultOrderA"; } // deliberately out of order
		@Probe String probeD() { return "resultOrderD"; }
		@Probe String probeC() { return "resultOrderC"; } // deliberately out of order

		Order() { super(Sources.EMPTY); }
	}



	@Test
	public void testNested() throws Exception
	{
		final Outer outer = new Outer();
		final Inner inner1 = outer.inner1;
		final Inner inner2 = outer.inner2;
		final Drinner drinner11 = inner1.drinner1;
		final Drinner drinner12 = inner1.drinner2;
		final Drinner drinner21 = inner2.drinner1;
		final Drinner drinner22 = inner2.drinner2;

		assertEquals(asList(
				wrap("A", "OuterA"),
				wrap("B", "OuterB"),
				wrap("inner1.A", "InnerA"),
				wrap("inner1.B", "InnerB"),
				wrap("inner1.drinner1.A", "DrinnerA"),
				wrap("inner1.drinner1.B", "DrinnerB"),
				wrap("inner1.drinner2.A", "DrinnerA"),
				wrap("inner1.drinner2.B", "DrinnerB"),
				wrap("inner2.A", "InnerA"),
				wrap("inner2.B", "InnerB"),
				wrap("inner2.drinner1.A", "DrinnerA"),
				wrap("inner2.drinner1.B", "DrinnerB"),
				wrap("inner2.drinner2.A", "DrinnerA"),
				wrap("inner2.drinner2.B", "DrinnerB")),
				wrap(outer.getProbes()));
		assertEquals(asList(
				wrap("A", "InnerA"),
				wrap("B", "InnerB"),
				wrap("drinner1.A", "DrinnerA"),
				wrap("drinner1.B", "DrinnerB"),
				wrap("drinner2.A", "DrinnerA"),
				wrap("drinner2.B", "DrinnerB")),
				wrap(inner1.getProbes()));
		assertEquals(asList(
				wrap("A", "InnerA"),
				wrap("B", "InnerB"),
				wrap("drinner1.A", "DrinnerA"),
				wrap("drinner1.B", "DrinnerB"),
				wrap("drinner2.A", "DrinnerA"),
				wrap("drinner2.B", "DrinnerB")),
				wrap(inner2.getProbes()));
		assertEquals(asList(
				wrap("A", "DrinnerA"),
				wrap("B", "DrinnerB")),
				wrap(drinner11.getProbes()));
		assertEquals(asList(
				wrap("A", "DrinnerA"),
				wrap("B", "DrinnerB")),
				wrap(drinner12.getProbes()));
		assertEquals(asList(
				wrap("A", "DrinnerA"),
				wrap("B", "DrinnerB")),
				wrap(drinner21.getProbes()));
		assertEquals(asList(
				wrap("A", "DrinnerA"),
				wrap("B", "DrinnerB")),
				wrap(drinner22.getProbes()));
	}

	static class Outer extends Properties
	{
		final Inner inner1 = valnp("inner1", Inner::new);
		final Inner inner2 = valnp("inner2", Inner::new);

		@Probe String probeA() { return "resultOuterA"; }
		@Probe String probeB() { return "resultOuterB"; }

		Outer() { super(Sources.EMPTY); }
	}

	static class Inner extends Properties
	{
		final Drinner drinner1 = valnp("drinner1", Drinner::new);
		final Drinner drinner2 = valnp("drinner2", Drinner::new);

		@Probe String probeA() { return "resultInnerA"; }
		@Probe String probeB() { return "resultInnerB"; }

		Inner(final Source source) { super(source); }
	}

	static class Drinner extends Properties
	{
		@Probe public String probeA() { return "resultDrinnerA"; }
		@Probe public String probeB() { return "resultDrinnerB"; }

		Drinner(final Source source) { super(source); }
	}



	@Test
	public void testInheritance() throws Exception
	{
		final Lower props = new Lower();

		assertEquals(asList(
				wrap("A", "UpperA"),
				wrap("B", "UpperB"),
				wrap("C", "UpperC"),
				wrap("A", "LowerA"),
				wrap("B", "LowerB"),
				wrap("C", "LowerC")),
				wrap(props.getProbes()));
	}

	@SuppressWarnings({"static-method", "MethodMayBeStatic"})
	@SuppressFBWarnings("UPM_UNCALLED_PRIVATE_METHOD")
	static class Upper extends Properties
	{
		@Probe private String probeA() { return "resultUpperA"; }
		@Probe private String probeC() { return "resultUpperC"; } // deliberately out of order
		@Probe private String probeB() { return "resultUpperB"; }

		Upper() { super(Sources.EMPTY); }
	}

	@SuppressWarnings("MethodOverridesInaccessibleMethodOfSuper")
	static class Lower extends Upper
	{
		@Probe String probeA() { return "resultLowerA"; }
		@Probe String probeC() { return "resultLowerC"; } // deliberately out of order
		@Probe String probeB() { return "resultLowerB"; }
	}



	@Test
	public void testPrivate() throws Exception
	{
		final Private props = new Private();

		assertEquals(asList(
				wrap("Private", "Private")),
				wrap(props.getProbes()));
	}

	static class Private extends Properties
	{
		@SuppressWarnings({"static-method", "MethodMayBeStatic"})
		@SuppressFBWarnings("UPM_UNCALLED_PRIVATE_METHOD")
		@Probe private String probePrivate() { return "resultPrivate"; }

		Private() { super(Sources.EMPTY); }
	}



	@Test
	public void testNonnamed() throws Exception
	{
		final Nonnamed props = new Nonnamed();

		assertEquals(asList(
				wrap("nonprobeNamed", "NonprobeNamed")),
				wrap(props.getProbes()));
	}

	static class Nonnamed extends Properties
	{
		@Probe String nonprobeNamed() { return "resultNonprobeNamed"; }

		Nonnamed() { super(Sources.EMPTY); }
	}



	@Test
	public void testNamed() throws Exception
	{
		final Named props = new Named();

		assertEquals(asList(
				wrap("overrideNamed", "Named")),
				wrap(props.getProbes()));
	}

	static class Named extends Properties
	{
		@Probe(name="overrideNamed") String probeNamed() { return "resultNamed"; }

		Named() { super(Sources.EMPTY); }
	}



	private static List<Wrapper> wrap(final List<? extends Callable<?>> probes) throws Exception
	{
		assertUnmodifiable(probes);
		final ArrayList<Wrapper> result = new ArrayList<>();
		for(final Callable<?> probe : probes)
			result.add(wrap(probe));
		return result;
	}

	private static Wrapper wrap(final String name, final String result)
	{
		return new Wrapper(name, "result" + result);
	}

	private static Wrapper wrap(final Callable<?> probe) throws Exception
	{
		return new Wrapper(probe.toString(), (String)probe.call());
	}

	static class Wrapper
	{
		final String name;
		final String result;

		Wrapper(final String name, final String result)
		{
			this.name = name;
			this.result = result;
		}

		@Override
		public boolean equals(final Object other)
		{
			if(!(other instanceof Wrapper))
				return false;

			final Wrapper o = (Wrapper)other;
			return
					name.equals(o.name) &&
					result.equals(o.result);
		}

		@Override
		public int hashCode()
		{
			return name.hashCode() ^ result.hashCode();
		}

		@Override
		public String toString()
		{
			return name + '/' + result;
		}
	}
}
