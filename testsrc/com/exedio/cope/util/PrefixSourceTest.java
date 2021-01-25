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
import static com.exedio.cope.util.PrefixSource.wrap;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.util.Properties.Source;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;

public class PrefixSourceTest
{
	private static class MockSource extends AssertionErrorPropertiesSource
	{
		private final List<String> keySet = asList(
				"alpha.one", "prefix.one", "prefix.two", "prefix.",
				"inner.outer.one", "inner.outer.two");
		private final boolean keySetNull;
		private final String description;

		MockSource(final boolean keySetNull, final String description)
		{
			this.keySetNull = keySetNull;
			this.description = description;
		}

		@Override
		public String get(final String key)
		{
			assertNotNull(key);

			if(keySet.contains(key))
				return key + "/val";
			else
				return null;
		}

		@Override
		public Collection<String> keySet()
		{
			return keySetNull ? null : keySet;
		}

		@Override
		public String getDescription()
		{
			return description;
		}

		@Override
		public String toString()
		{
			return description!=null ? ("toString(" + description + ")") : null;
		}
	}

	@Test void testIt()
	{
		final MockSource ms = new MockSource(false, "description");
		final PrefixSource ps = (PrefixSource)wrap(ms, "prefix.");

		assertEquals("prefix.one/val", ps.get("one"));
		assertEquals("prefix.two/val", ps.get("two"));
		assertEquals(null, ps.get("none"));
		assertFails(() ->
			ps.get(""),
			IllegalArgumentException.class,
			"key must not be empty");
		assertFails(() ->
			ps.get(null),
			NullPointerException.class, "key");
		assertEqualsUnmodifiable(asList("one", "two", ""), ps.keySet());
		assertEquals("description (prefix prefix.)", ps.getDescription());
		assertEquals("toString(description) (prefix prefix.)", ps.toString());

		assertSame(ms, wrap(ms, null));
		assertSame(ms, wrap(ms, ""));
	}

	@Test void testNull()
	{
		final MockSource ms = new MockSource(true, null);
		final PrefixSource ps = (PrefixSource)wrap(ms, "prefix.");

		assertEquals("prefix.one/val", ps.get("one"));
		assertEquals("prefix.two/val", ps.get("two"));
		assertEquals(null, ps.get("none"));
		assertFails(() ->
			ps.get(""),
			IllegalArgumentException.class,
			"key must not be empty");
		assertFails(() ->
			ps.get(null),
			NullPointerException.class, "key");
		assertEquals(null, ps.keySet());
		assertEquals("unknown prefix prefix.", ps.getDescription());
		assertEquals(null, ps.toString());

		assertNull(wrap(null, null));
		assertNull(wrap(null, ""));
	}

	@SuppressWarnings("unused")
	@Test void testFail()
	{
		//noinspection ConstantConditions
		assertFails(() ->
			new PrefixSource(null, null),
			NullPointerException.class, "source");
		assertFails(() ->
			wrap(null, "prefix."),
			NullPointerException.class, "source");

		final MockSource ms = new MockSource(false, "description");
		//noinspection ConstantConditions
		assertFails(() ->
			new PrefixSource(ms, null),
			NullPointerException.class, "prefix");
		assertFails(() ->
			new PrefixSource(ms, ""),
			IllegalArgumentException.class, "prefix");
	}

	@Test void nest()
	{
		final Source s =
				wrap(wrap(new MockSource(false, "description"), "inner."), "outer.");
		assertEquals(
				"description (prefix inner.outer.)",
				s.getDescription());
		assertEquals("inner.outer.one/val", s.get("one"));
		assertEquals("inner.outer.two/val", s.get("two"));
		assertEquals(asList("one", "two"), s.keySet());
	}

	@Test void nestConstructor()
	{
		final Source s =
				new PrefixSource(new PrefixSource(new MockSource(false, "description"), "inner."), "outer.");
		assertEquals(
				"description (prefix inner.) (prefix outer.)",
				s.getDescription());
		assertEquals("inner.outer.one/val", s.get("one"));
		assertEquals("inner.outer.two/val", s.get("two"));
		assertEquals(asList("one", "two"), s.keySet());
	}

	@Test void testReload()
	{
		final Source s = wrap(new ReloadablePropertiesSource("desc"), "px.");
		final Source r = s.reload();

		assertEquals("desc(0) (prefix px.)", s.getDescription());
		assertEquals("desc(1) (prefix px.)", r.getDescription());
	}

	@Test void testReloadNotNeeded()
	{
		final Source s = wrap(RELOADS_TO_THIS, "px.");
		assertSame(s, s.reload());
	}

	private static final Source RELOADS_TO_THIS = new AssertionErrorPropertiesSource()
	{
		@Override public Source reload()
		{
			return this;
		}
	};
}
