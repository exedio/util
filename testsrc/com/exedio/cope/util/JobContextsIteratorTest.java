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
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

public class JobContextsIteratorTest
{
	private static final Iterator<String> ITERATOR_FAIL = new Iterator<String>()
	{
		@Override
		public boolean hasNext()
		{
			throw new RuntimeException();
		}

		@Override
		@SuppressFBWarnings("IT_NO_SUCH_ELEMENT") // OK
		public String next()
		{
			throw new RuntimeException();
		}

		@Override
		public void remove()
		{
			throw new RuntimeException();
		}
	};

	private static final JobContext CONTEXT_FAIL = new AssertionErrorJobContext();

	@Test void testFail()
	{
		assertSame(null, iterator(null, null));
		assertSame(null, iterator(null, CONTEXT_FAIL));
		assertSame   (ITERATOR_FAIL, iterator(ITERATOR_FAIL, null));
		assertNotSame(ITERATOR_FAIL, iterator(ITERATOR_FAIL, CONTEXT_FAIL));
	}

	@Test void testImmediateStop()
	{
		final Iterator<?> iterator = createStrictMock(Iterator.class);
		final JobContext ctx = createStrictMock(JobContext.class);

		ctx.stopIfRequested();
		expectLastCall().andThrow(new JobStop("whatever"));

		replay(iterator);
		replay(ctx);

		final Iterator<?> tested = iterator(iterator, ctx);
		assertEquals(false, tested.hasNext());

		verify(iterator);
		verify(ctx);
	}

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED")
	@Test void testLaterStop()
	{
		final Iterator<?> iterator = createStrictMock(Iterator.class);
		final JobContext ctx = createStrictMock(JobContext.class);

		ctx.stopIfRequested();
		expectLastCall();
		iterator.hasNext();
		expectLastCall().andReturn(Boolean.TRUE);
		iterator.next();
		expectLastCall().andReturn("first");
		iterator.next();
		expectLastCall().andReturn("second");
		ctx.stopIfRequested();
		expectLastCall().andThrow(new JobStop("whatever"));

		replay(iterator);
		replay(ctx);

		final Iterator<?> tested = iterator(iterator, ctx);
		assertEquals(true, tested.hasNext());
		assertEquals("first", tested.next());
		assertEquals("second", tested.next());
		assertEquals(false, tested.hasNext());
		assertFails(() ->
			tested.next(),
			NoSuchElementException.class,
			"stopRequested: whatever");

		verify(iterator);
		verify(ctx);
	}

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED")
	@Test void testNoStop()
	{
		final Iterator<?> iterator = createStrictMock(Iterator.class);
		final JobContext ctx = createStrictMock(JobContext.class);

		ctx.stopIfRequested();
		expectLastCall();
		iterator.hasNext();
		expectLastCall().andReturn(Boolean.TRUE);
		iterator.next();
		expectLastCall().andReturn("first");
		iterator.next();
		expectLastCall().andReturn("second");
		ctx.stopIfRequested();
		expectLastCall();
		iterator.hasNext();
		expectLastCall().andReturn(Boolean.FALSE);
		iterator.next();
		expectLastCall().andThrow(new NoSuchElementException("alliballi"));

		replay(iterator);
		replay(ctx);

		final Iterator<?> tested = iterator(iterator, ctx);
		assertEquals(true, tested.hasNext());
		assertEquals("first", tested.next());
		assertEquals("second", tested.next());
		assertEquals(false, tested.hasNext());
		assertFails(() ->
			tested.next(),
			NoSuchElementException.class, "alliballi");

		verify(iterator);
		verify(ctx);
	}

	@SuppressWarnings("deprecation") // OK: test deprecated api
	private static <E> java.util.Iterator<E> iterator(
			final java.util.Iterator<E> iterator,
			final JobContext ctx)
	{
		return JobContexts.iterator(iterator, ctx);
	}
}
