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
import static com.exedio.cope.util.Cast.verboseCast;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
public class CastTest
{
	private static final String string1 = "string1";
	private static final Object string1Object = string1;

	@SuppressFBWarnings("ES_COMPARING_STRINGS_WITH_EQ")
	@Test void testVerboseCast()
	{
		assertNull(verboseCast(String.class, null));
		assertSame(string1, verboseCast(String.class, string1Object));
	}

	@Test void testVerboseCastClassNull()
	{
		assertFails(() ->
			verboseCast(null, string1),
			NullPointerException.class, null);
	}

	@Test void testVerboseCastAllNull()
	{
		assertFails(() ->
			verboseCast(null, null),
			NullPointerException.class, null);
	}

	@Test void testVerboseCastWrongCast()
	{
		assertFails(() ->
			verboseCast(Integer.class, string1),
			ClassCastException.class,
			"expected a java.lang.Integer, but was a java.lang.String");
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
			NullPointerException.class, null);
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
			"expected a java.lang.Integer, but was a java.lang.String");
	}
}
