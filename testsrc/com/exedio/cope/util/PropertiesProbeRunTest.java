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
import static com.exedio.cope.junit.CopeAssert.assertEqualsUnmodifiable;
import static com.exedio.cope.junit.CopeAssert.assertUnmodifiable;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.Serial;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.Callable;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public class PropertiesProbeRunTest
{
	@Test void test() throws Exception
	{
		final MyProps props = new MyProps();
		final List<? extends Callable<?>> probes = props.getProbes();

		assertUnmodifiable(probes);
		assertEqualsUnmodifiable(probes, props.getProbes());
		assertEquals(9, probes.size());

		final Callable<?> probeReturn = probes.get(0);
		final Callable<?> probePrimit = probes.get(1);
		final Callable<?> probeNull   = probes.get(2);
		final Callable<?> probeVoid   = probes.get(3);
		final Callable<?> probeExcp   = probes.get(4);
		final Callable<?> probeError  = probes.get(5);
		final Callable<?> probeThrow  = probes.get(6);
		final Callable<?> probeAbort  = probes.get(7);
		assertSame(MORE,                probes.get(8));

		assertEquals("10Return", probeReturn.toString());
		assertEquals("20Primit", probePrimit.toString());
		assertEquals("30Null"  , probeNull  .toString());
		assertEquals("40Void"  , probeVoid  .toString());
		assertEquals("50Excp"  , probeExcp  .toString());
		assertEquals("60Error" , probeError .toString());
		assertEquals("70Throw" , probeThrow .toString());
		assertEquals("80Abort" , probeAbort .toString());

		assertEquals("probeReturnResult", probeReturn.call());
		assertEquals(Integer.valueOf(777), probePrimit.call());
		assertEquals(null, probeNull.call());
		assertEquals(null, probeVoid.call());

		assertFails(
			probeExcp::call,
			IOException.class, "probeExcp");
		assertFails(
			probeError::call,
			AssertionError.class, "probeError");
		final InvocationTargetException ew = assertThrows(
				InvocationTargetException.class,
				probeThrow::call);
		final Throwable e = ew.getTargetException();
		assertEquals("probeThrow", e.getMessage());
		assertEquals(MyThrowable.class, e.getClass());
		assertFails(
				probeAbort::call,
				Properties.ProbeAbortedException.class, "probeAbort");
	}

	static class MyProps extends Properties
	{
		@Probe String probe10Return() { return "probeReturnResult"; }
		@Probe int    probe20Primit() { return 777; }
		@Probe String probe30Null()   { return null; }
		@Probe void   probe40Void()   { /* empty */ }
		@Probe String probe50Excp()   throws IOException { throw new IOException("probeExcp"); }
		@Probe String probe60Error()  { throw new AssertionError("probeError"); }
		@Probe String probe70Throw()  throws MyThrowable { throw new MyThrowable(); }
		@Probe String probe80Abort()  throws ProbeAbortedException { throw newProbeAbortedException("probeAbort"); }

		@Override public List<Callable<?>> probeMore() { return asList(MORE); }

		MyProps() { super(Sources.EMPTY); }
	}

	private static final Callable<?> MORE = () -> { throw new AssertionFailedError(); };

	static class MyThrowable extends Throwable
	{
		@Serial
		private static final long serialVersionUID = 1l;

		MyThrowable()
		{
			super("probeThrow");
		}
	}
}
