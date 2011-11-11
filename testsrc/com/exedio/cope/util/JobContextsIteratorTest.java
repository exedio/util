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

import static com.exedio.cope.util.JobContextDeprecated.requestedToStop;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.exedio.cope.junit.CopeAssert;

public class JobContextsIteratorTest extends CopeAssert
{
	private static final Iterator<String> ITERATOR_FAIL = new Iterator<String>()
	{
		public boolean hasNext()
		{
			throw new RuntimeException();
		}

		public String next()
		{
			throw new RuntimeException();
		}

		public void remove()
		{
			throw new RuntimeException();
		}
	};

	private static final JobContext CONTEXT_FAIL = new JobContext()
	{
		public void stopIfRequested()
		{
			throw new RuntimeException();
		}

		@Deprecated
		public boolean requestedToStop()
		{
			throw new RuntimeException();
		}

		public boolean supportsMessage()
		{
			throw new RuntimeException();
		}

		public void setMessage(final String message)
		{
			throw new RuntimeException();
		}

		public boolean supportsProgress()
		{
			throw new RuntimeException();
		}

		public void incrementProgress()
		{
			throw new RuntimeException();
		}

		public void incrementProgress(final int delta)
		{
			throw new RuntimeException();
		}

		public boolean supportsCompleteness()
		{
			throw new RuntimeException();
		}

		public void setCompleteness(final double completeness)
		{
			throw new RuntimeException();
		}
	};

	public void testFail()
	{
		assertSame(null, iterator(null, null));
		assertSame(null, iterator(null, CONTEXT_FAIL));
		assertSame   (ITERATOR_FAIL, iterator(ITERATOR_FAIL, null));
		assertNotSame(ITERATOR_FAIL, iterator(ITERATOR_FAIL, CONTEXT_FAIL));
	}

	public void testImmediateStop()
	{
		final Iterator<?> iterator = createStrictMock(Iterator.class);
		final JobContext ctx = createStrictMock(JobContext.class);

		requestedToStop(ctx);
		expectLastCall().andReturn(Boolean.TRUE);

		replay(iterator);
		replay(ctx);

		final Iterator<?> tested = iterator(iterator, ctx);
		assertEquals(false, tested.hasNext());

		verify(iterator);
		verify(ctx);
	}

	@edu.umd.cs.findbugs.annotations.SuppressWarnings("RV_RETURN_VALUE_IGNORED")
	public void testLaterStop()
	{
		final Iterator<?> iterator = createStrictMock(Iterator.class);
		final JobContext ctx = createStrictMock(JobContext.class);

		requestedToStop(ctx);
		expectLastCall().andReturn(Boolean.FALSE);
		iterator.hasNext();
		expectLastCall().andReturn(Boolean.TRUE);
		iterator.next();
		expectLastCall().andReturn("first");
		iterator.next();
		expectLastCall().andReturn("second");
		requestedToStop(ctx);
		expectLastCall().andReturn(Boolean.TRUE);

		replay(iterator);
		replay(ctx);

		final Iterator<?> tested = iterator(iterator, ctx);
		assertEquals(true, tested.hasNext());
		assertEquals("first", tested.next());
		assertEquals("second", tested.next());
		assertEquals(false, tested.hasNext());
		try
		{
			tested.next();
			fail();
		}
		catch(final NoSuchElementException e)
		{
			assertEquals("requestedToStop", e.getMessage());
		}

		verify(iterator);
		verify(ctx);
	}

	@edu.umd.cs.findbugs.annotations.SuppressWarnings("RV_RETURN_VALUE_IGNORED")
	public void testNoStop()
	{
		final Iterator<?> iterator = createStrictMock(Iterator.class);
		final JobContext ctx = createStrictMock(JobContext.class);

		requestedToStop(ctx);
		expectLastCall().andReturn(Boolean.FALSE);
		iterator.hasNext();
		expectLastCall().andReturn(Boolean.TRUE);
		iterator.next();
		expectLastCall().andReturn("first");
		iterator.next();
		expectLastCall().andReturn("second");
		requestedToStop(ctx);
		expectLastCall().andReturn(Boolean.FALSE);
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
		try
		{
			tested.next();
			fail();
		}
		catch(final NoSuchElementException e)
		{
			assertEquals("alliballi", e.getMessage());
		}

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
