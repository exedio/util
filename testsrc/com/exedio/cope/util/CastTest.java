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
import static com.exedio.cope.util.Cast.castElements;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;
import org.junit.jupiter.api.Test;

public class CastTest
{
	private static final String string1 = "string1";
	private static final Object string1Object = string1;

	@Deprecated // OK: testing deprecated API
	@Test void testVerboseCast()
	{
		assertNull(Cast.verboseCast(String.class, null));
		assertSame(string1, Cast.verboseCast(String.class, string1Object));
	}

	@Deprecated // OK: testing deprecated API
	@Test void testVerboseCastClassNull()
	{
		//noinspection DataFlowIssue
		assertFails(() ->
			Cast.verboseCast(null, string1),
			NullPointerException.class,
			"Cannot invoke \"java.lang.Class.cast(Object)\" " +
			"because \"clazz\" is null");
	}

	@Deprecated // OK: testing deprecated API
	@Test void testVerboseCastAllNull()
	{
		//noinspection DataFlowIssue
		assertFails(() ->
			Cast.verboseCast(null, null),
			NullPointerException.class,
			"Cannot invoke \"java.lang.Class.cast(Object)\" " +
			"because \"clazz\" is null");
	}

	@Deprecated // OK: testing deprecated API
	@Test void testVerboseCastWrongCast()
	{
		assertFails(() ->
			Cast.verboseCast(Integer.class, string1),
			ClassCastException.class,
			"Cannot cast java.lang.String to java.lang.Integer");
	}

	@Test void testCastElements()
	{
		final List<String> strings = asList(string1);
		final List<String> strings2 = asList(string1, string1);
		final List<String> nulls = asList(string1, null);

		assertNull(castElements(String.class, null));
		assertEquals(strings, castElements(String.class, strings)); // TODO should be same
		assertEquals(strings2, castElements(String.class, strings2)); // TODO should be same
		assertEquals(nulls, castElements(String.class, nulls)); // TODO should be same
	}

	@Test void testCastElementsClassNull()
	{
		final List<String> strings = asList(string1);
		assertFails(() ->
			castElements(null, strings),
			NullPointerException.class,
			"Cannot invoke \"java.lang.Class.cast(Object)\" " +
			"because \"clazz\" is null");
	}

	@Test void testCastElementsAllNull()
	{
		assertNull(castElements(null, null));
	}

	@Test void testCastElementsWrongCast()
	{
		final List<String> strings = asList(string1);
		assertFails(() ->
			castElements(Integer.class, strings),
			ClassCastException.class,
			"Cannot cast java.lang.String to java.lang.Integer");
	}
}
