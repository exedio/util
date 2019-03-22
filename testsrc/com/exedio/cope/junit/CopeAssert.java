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

package com.exedio.cope.junit;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class CopeAssert
{
	public static void assertContainsList(final List<?> expected, final Collection<?> actual)
	{
		if(expected==null && actual==null)
			return;

		assertNotNull(expected, "expected null, but was " + actual);
		assertNotNull(actual, "expected " + expected + ", but was null");

		if(expected.size()!=actual.size() ||
				!expected.containsAll(actual) ||
				!actual.containsAll(expected))
			fail("expected "+expected+", but was "+actual);
	}

	public static void assertContains(final Collection<?> actual)
	{
		assertContainsList(Collections.emptyList(), actual);
	}

	public static void assertContains(final Object o, final Collection<?> actual)
	{
		assertContainsList(Collections.singletonList(o), actual);
	}

	public static void assertContains(final Object o1, final Object o2, final Collection<?> actual)
	{
		assertContainsList(asList(o1, o2), actual);
	}

	public static void assertContains(final Object o1, final Object o2, final Object o3, final Collection<?> actual)
	{
		assertContainsList(asList(o1, o2, o3), actual);
	}

	public static void assertContains(final Object o1, final Object o2, final Object o3, final Object o4, final Collection<?> actual)
	{
		assertContainsList(asList(o1, o2, o3, o4), actual);
	}

	@SuppressWarnings("unused") // OK: for later use
	public static void assertContains(final Object o1, final Object o2, final Object o3, final Object o4, final Object o5, final Collection<?> actual)
	{
		assertContainsList(asList(o1, o2, o3, o4, o5), actual);
	}

	@SuppressWarnings("unused") // OK: for later use
	public static void assertContains(final Object o1, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Collection<?> actual)
	{
		assertContainsList(asList(o1, o2, o3, o4, o5, o6), actual);
	}

	@SuppressWarnings("unused") // OK: for later use
	public static void assertContains(final Object o1, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Collection<?> actual)
	{
		assertContainsList(asList(o1, o2, o3, o4, o5, o6, o7), actual);
	}

	public static void assertContainsUnmodifiable(final Collection<?> actual)
	{
		assertUnmodifiable(actual);
		assertContains(actual);
	}

	public static void assertContainsUnmodifiable(final Object o, final Collection<?> actual)
	{
		assertUnmodifiable(actual);
		assertContains(o, actual);
	}

	@SuppressWarnings("unused") // OK: for later use
	public static void assertContainsUnmodifiable(final Object o1, final Object o2, final Collection<?> actual)
	{
		assertUnmodifiable(actual);
		assertContains(o1, o2, actual);
	}

	@SuppressWarnings("unused") // OK: for later use
	public static void assertContainsUnmodifiable(final Object o1, final Object o2, final Object o3, final Collection<?> actual)
	{
		assertUnmodifiable(actual);
		assertContains(o1, o2, o3, actual);
	}

	@SuppressWarnings("unused") // OK: for later use
	public static void assertContainsUnmodifiable(final Object o1, final Object o2, final Object o3, final Object o4, final Collection<?> actual)
	{
		assertUnmodifiable(actual);
		assertContains(o1, o2, o3, o4, actual);
	}

	@SuppressWarnings("SuspiciousMethodCalls")
	public static <T> void assertUnmodifiable(final Collection<T> c)
	{
		try
		{
			c.add(null);
			fail("should have thrown UnsupportedOperationException");
		}
		catch(final UnsupportedOperationException ignored) {/*OK*/}
		try
		{
			c.addAll(Collections.singleton(null));
			fail("should have thrown UnsupportedOperationException");
		}
		catch(final UnsupportedOperationException ignored) {/*OK*/}

		if(!c.isEmpty())
		{
			final Object o = c.iterator().next();
			try
			{
				c.clear();
				fail("should have thrown UnsupportedOperationException");
			}
			catch(final UnsupportedOperationException ignored) {/*OK*/}
			try
			{
				c.remove(o);
				fail("should have thrown UnsupportedOperationException");
			}
			catch(final UnsupportedOperationException ignored) {/*OK*/}
			try
			{
				c.removeAll(Collections.singleton(o));
				fail("should have thrown UnsupportedOperationException");
			}
			catch(final UnsupportedOperationException ignored) {/*OK*/}
			try
			{
				c.retainAll(Collections.emptyList());
				fail("should have thrown UnsupportedOperationException");
			}
			catch(final UnsupportedOperationException ignored) {/*OK*/}

			final Iterator<?> iterator = c.iterator();
			try
			{
				iterator.next();
				iterator.remove();
				fail("should have thrown UnsupportedOperationException");
			}
			catch(final UnsupportedOperationException ignored) {/*OK*/}
		}

		if(c instanceof List<?>)
		{
			final List<T> l = (List<T>)c;

			if(!l.isEmpty())
			{
				try
				{
					l.set(0, null);
					fail("should have thrown UnsupportedOperationException");
				}
				catch(final UnsupportedOperationException ignored) {/*OK*/}
			}
		}
	}

	public static void assertEqualsUnmodifiable(final List<?> expected, final Collection<?> actual)
	{
		assertUnmodifiable(actual);
		assertEquals(expected, actual);
	}

	@SuppressWarnings("unused") // OK: for later use
	public static void assertEqualsUnmodifiable(final Map<?,?> expected, final Map<?,?> actual)
	{
		try
		{
			actual.put(null, null);
			fail("should have thrown UnsupportedOperationException");
		}
		catch(final UnsupportedOperationException ignored) {/*OK*/}
		if(!actual.isEmpty())
		{
			try
			{
				actual.clear();
				fail("should have thrown UnsupportedOperationException");
			}
			catch(final UnsupportedOperationException ignored) {/*OK*/}
			try
			{
				actual.remove(null);
				fail("should have thrown UnsupportedOperationException");
			}
			catch(final UnsupportedOperationException ignored) {/*OK*/}
		}
		assertUnmodifiable(actual.keySet());
		assertUnmodifiable(actual.values());
		assertUnmodifiable(actual.entrySet());
		assertEquals(expected, actual);
	}

	private static final String DATE_FORMAT_FULL = "dd.MM.yyyy HH:mm:ss.SSS";

	public static void assertWithin(final Date expectedBefore, final Date expectedAfter, final Date actual)
	{
		final SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_FULL, Locale.ENGLISH);
		final String message =
			"expected date within " + df.format(expectedBefore) +
			" and " + df.format(expectedAfter) +
			", but was " + df.format(actual);

		assertTrue(!expectedBefore.after(actual), message);
		assertTrue(!expectedAfter.before(actual), message);
	}

	@SuppressWarnings("unchecked")
	public static <S extends Serializable> S reserialize(final S value, final int maxSize)
	{
		if(value==null)
			throw new NullPointerException();

		try
		{
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try(ObjectOutputStream s = new ObjectOutputStream(bos))
			{
				s.writeObject(value);
			}

			assertTrue(bos.size()<maxSize, String.valueOf(bos.size()));

			final Object result;
			try(ObjectInputStream s = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray())))
			{
				result = s.readObject();
			}
			return (S)result;
		}
		catch(final IOException | ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}


	private CopeAssert()
	{
		// prevent instantiation
	}
}
