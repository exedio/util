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

import static com.exedio.cope.util.Cast.castElements;
import static com.exedio.cope.util.Cast.verboseCast;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.fail;

import com.exedio.cope.junit.CopeAssert;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class CastTest extends CopeAssert
{
	private static final String string1 = "string1";
	private static final Object string1Object = string1;

	@Test public void testVerboseCast()
	{
		assertNull(verboseCast(String.class, null));
		assertSame(string1, verboseCast(String.class, string1Object));
	}

	@Test public void testVerboseCastClassNull()
	{
		try
		{
			verboseCast(null, string1);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	@Test public void testVerboseCastAllNull()
	{
		try
		{
			verboseCast(null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	@Test public void testVerboseCastWrongCast()
	{
		try
		{
			verboseCast(Integer.class, string1);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected a java.lang.Integer, but was a java.lang.String", e.getMessage());
		}
	}

	@Test public void testCastElements()
	{
		final List<String> strings = Arrays.asList(string1);
		final List<String> strings2 = Arrays.asList(string1, string1);
		final List<String> nulls = Arrays.asList(string1, null);

		assertNull(castElements(String.class, null));
		assertEquals(strings, castElements(String.class, strings)); // TODO should be same
		assertEquals(strings2, castElements(String.class, strings2)); // TODO should be same
		assertEquals(nulls, castElements(String.class, nulls)); // TODO should be same
	}

	@Test public void testCastElementsClassNull()
	{
		final List<String> strings = Arrays.asList(string1);
		try
		{
			castElements(null, strings);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	@Test public void testCastElementsAllNull()
	{
		assertNull(castElements(null, null));
	}

	@Test public void testCastElementsWrongCast()
	{
		final List<String> strings = Arrays.asList(string1);
		try
		{
			castElements(Integer.class, strings);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected a java.lang.Integer, but was a java.lang.String", e.getMessage());
		}
	}
}
