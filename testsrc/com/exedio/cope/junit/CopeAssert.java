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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class CopeAssert
{
	public static <T> void assertContainsList(final List<T> expected, final Collection<T> actual)
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

	public static <T> void assertContains(final Collection<T> actual)
	{
		assertContainsList(Collections.emptyList(), actual);
	}

	public static <T> void assertContains(final T o, final Collection<T> actual)
	{
		assertContainsList(Collections.singletonList(o), actual);
	}

	public static <T> void assertContains(final T o1, final T o2, final Collection<T> actual)
	{
		assertContainsList(asList(o1, o2), actual);
	}

	public static <T> void assertContains(final T o1, final T o2, final T o3, final Collection<T> actual)
	{
		assertContainsList(asList(o1, o2, o3), actual);
	}

	public static <T> void assertContains(final T o1, final T o2, final T o3, final T o4, final Collection<T> actual)
	{
		assertContainsList(asList(o1, o2, o3, o4), actual);
	}

	@SuppressWarnings("unused") // OK: for later use
	public static <T> void assertContains(final T o1, final T o2, final T o3, final T o4, final T o5, final Collection<T> actual)
	{
		assertContainsList(asList(o1, o2, o3, o4, o5), actual);
	}

	@SuppressWarnings("unused") // OK: for later use
	public static <T> void assertContains(final T o1, final T o2, final T o3, final T o4, final T o5, final T o6, final Collection<T> actual)
	{
		assertContainsList(asList(o1, o2, o3, o4, o5, o6), actual);
	}

	@SuppressWarnings("unused") // OK: for later use
	public static <T> void assertContains(final T o1, final T o2, final T o3, final T o4, final T o5, final T o6, final T o7, final Collection<T> actual)
	{
		assertContainsList(asList(o1, o2, o3, o4, o5, o6, o7), actual);
	}

	public static <T> void assertContainsUnmodifiable(final Collection<T> actual)
	{
		assertUnmodifiable(actual);
		assertContains(actual);
	}

	public static <T> void assertContainsUnmodifiable(final T o, final Collection<T> actual)
	{
		assertUnmodifiable(actual);
		assertContains(o, actual);
	}

	@SuppressWarnings("unused") // OK: for later use
	public static <T> void assertContainsUnmodifiable(final T o1, final T o2, final Collection<T> actual)
	{
		assertUnmodifiable(actual);
		assertContains(o1, o2, actual);
	}

	@SuppressWarnings("unused") // OK: for later use
	public static <T> void assertContainsUnmodifiable(final T o1, final T o2, final T o3, final Collection<T> actual)
	{
		assertUnmodifiable(actual);
		assertContains(o1, o2, o3, actual);
	}

	@SuppressWarnings("unused") // OK: for later use
	public static <T> void assertContainsUnmodifiable(final T o1, final T o2, final T o3, final T o4, final Collection<T> actual)
	{
		assertUnmodifiable(actual);
		assertContains(o1, o2, o3, o4, actual);
	}

	public static <T> void assertUnmodifiable(final Collection<T> c)
	{
		final String name = c.getClass().getName();
		assertTrue(UNMODIFIABLE_COLLECTIONS.contains(name), name);
	}

	private static final HashSet<String> UNMODIFIABLE_COLLECTIONS = new HashSet<>(asList(
			"java.util.ImmutableCollections$List12",
			"java.util.ImmutableCollections$ListN",
			"java.util.Collections$UnmodifiableCollection",
			"java.util.Collections$UnmodifiableRandomAccessList",
			"java.util.Collections$SingletonList",
			"java.util.Collections$EmptyList",
			"java.util.Collections$UnmodifiableSet",
			"java.util.Collections$UnmodifiableNavigableSet$EmptyNavigableSet"));

	public static void assertEqualsUnmodifiable(final List<?> expected, final Collection<?> actual)
	{
		assertUnmodifiable(actual);
		assertEquals(expected, actual);
	}

	@SuppressWarnings("unused") // OK: for later use
	public static void assertEqualsUnmodifiable(final Map<?,?> expected, final Map<?,?> actual)
	{
		final String name = actual.getClass().getName();
		assertTrue(UNMODIFIABLE_MAPS.contains(name), name);
		assertEquals(expected, actual);
	}

	private static final HashSet<String> UNMODIFIABLE_MAPS = new HashSet<>(asList(
			"java.util.Collections$UnmodifiableMap"));

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
