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

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

@SuppressWarnings("deprecation") // OK: testing deprecated api
public class InterruptersIteratorTest
{
	private static final Iterator<String> ITERATOR_FAIL = new Iterator<>()
	{
		@Override
		public boolean hasNext()
		{
			throw new RuntimeException();
		}

		@Override
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

	private static final Interrupter INTERRUPTER_FAIL = () ->
		{
			throw new RuntimeException();
		};

	@Test void testFail()
	{
		assertSame(null, iterator(null, null));
		assertSame(null, iterator(null, INTERRUPTER_FAIL));
		assertSame   (ITERATOR_FAIL, iterator(ITERATOR_FAIL, null));
		assertNotSame(ITERATOR_FAIL, iterator(ITERATOR_FAIL, INTERRUPTER_FAIL));
	}

	@Test void testImmediateInterrupt()
	{
		final Iterator<?> iterator = createStrictMock(Iterator.class);
		final Interrupter interrupter = createStrictMock(Interrupter.class);

		interrupter.isRequested();
		expectLastCall().andReturn(Boolean.TRUE);

		replay(iterator);
		replay(interrupter);

		final Iterator<?> tested = iterator(iterator, interrupter);
		assertEquals(false, tested.hasNext());

		verify(iterator);
		verify(interrupter);
	}

	@SuppressWarnings("ResultOfMethodCallIgnored") // results are meaningless with easymock
	@Test void testLaterInterrupt()
	{
		final Iterator<?> iterator = createStrictMock(Iterator.class);
		final Interrupter interrupter = createStrictMock(Interrupter.class);

		interrupter.isRequested();
		expectLastCall().andReturn(Boolean.FALSE);
		iterator.hasNext();
		expectLastCall().andReturn(Boolean.TRUE);
		iterator.next();
		expectLastCall().andReturn("first");
		iterator.next();
		expectLastCall().andReturn("second");
		interrupter.isRequested();
		expectLastCall().andReturn(Boolean.TRUE);

		replay(iterator);
		replay(interrupter);

		final Iterator<?> tested = iterator(iterator, interrupter);
		assertEquals(true, tested.hasNext());
		assertEquals("first", tested.next());
		assertEquals("second", tested.next());
		assertEquals(false, tested.hasNext());
		assertFails(
			tested::next,
			NoSuchElementException.class,
			"stopRequested: Interrupter.isRequested");

		verify(iterator);
		verify(interrupter);
	}

	@SuppressWarnings("ResultOfMethodCallIgnored") // results are meaningless with easymock
	@Test void testNoInterrupt()
	{
		final Iterator<?> iterator = createStrictMock(Iterator.class);
		final Interrupter interrupter = createStrictMock(Interrupter.class);

		interrupter.isRequested();
		expectLastCall().andReturn(Boolean.FALSE);
		iterator.hasNext();
		expectLastCall().andReturn(Boolean.TRUE);
		iterator.next();
		expectLastCall().andReturn("first");
		iterator.next();
		expectLastCall().andReturn("second");
		interrupter.isRequested();
		expectLastCall().andReturn(Boolean.FALSE);
		iterator.hasNext();
		expectLastCall().andReturn(Boolean.FALSE);
		iterator.next();
		expectLastCall().andThrow(new NoSuchElementException("alliballi"));

		replay(iterator);
		replay(interrupter);

		final Iterator<?> tested = iterator(iterator, interrupter);
		assertEquals(true, tested.hasNext());
		assertEquals("first", tested.next());
		assertEquals("second", tested.next());
		assertEquals(false, tested.hasNext());
		assertFails(
			tested::next,
			NoSuchElementException.class, "alliballi");

		verify(iterator);
		verify(interrupter);
	}


	/**
	 * Replaces static import and avoids deprecation warning in IDEA 2017.2.3.
	 */
	private static <E> Iterator<E> iterator(
			final Iterator<E> iterator,
			final Interrupter interrupter)
	{
		return Interrupters.iterator(iterator, interrupter);
	}
}
