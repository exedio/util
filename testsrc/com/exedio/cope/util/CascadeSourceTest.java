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
import static com.exedio.cope.util.Sources.cascade;
import static com.exedio.cope.util.Sources.view;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.util.Properties.Source;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.Test;

public class CascadeSourceTest
{
	@Test void normal()
	{
		final Properties properties1 = new Properties();
		properties1.setProperty("key1a", "value1a");
		properties1.setProperty("key1b", "value1b");

		final Properties properties2 = new Properties();
		properties2.setProperty("key2a", "value2a");
		properties2.setProperty("key2b", "value2b");

		properties1.setProperty("key12", "value12-1");
		properties2.setProperty("key12", "value12-2");

		final Source s = cascade(
				view(properties1, "description1"),
				view(properties2, "description2"));

		assertEquals("value1a", s.get("key1a"));
		assertEquals("value1b", s.get("key1b"));
		assertEquals("value2a", s.get("key2a"));
		assertEquals("value2b", s.get("key2b"));
		assertEquals("value12-1", s.get("key12"));
		assertEquals(null, s.get("keyx"));
		assertEquals(new HashSet<>(asList("key1a", "key1b", "key2a", "key2b", "key12")), s.keySet());
		assertEquals("description1 / description2", s.getDescription());
		assertEquals("description1 / description2", s.toString());

		assertFails(() ->
			s.get(""),
			IllegalArgumentException.class, "key must not be empty");
		assertFails(() ->
			s.get(null),
			NullPointerException.class, "key");
	}

	@Test void nullArray()
	{
		assertFails(() ->
			cascade((Source[])null),
			NullPointerException.class, null);
	}

	@Test void nullElementSingleton()
	{
		assertFails(() ->
			cascade(new Source[]{null}),
			NullPointerException.class, null);
	}

	@Test void nullElementMultiple()
	{
		assertFails(() ->
			cascade(new Source[]{null, null}),
			NullPointerException.class, null);
	}

	@Test void empty()
	{
		assertSame(Sources.EMPTY, cascade());
	}

	@Test void singleton()
	{
		final Source singleton = view(new Properties(), "description1");
		assertSame(singleton, cascade(singleton));
	}

	@Test void exposeArray()
	{
		final Source[] sources = {
				view(new Properties(), "description1"),
				view(new Properties(), "description2")};

		final Source s = cascade(sources);
		assertEquals("description1 / description2", s.getDescription());

		sources[0] = null;
		// NOTE
		// the following line throws a NullPointerException,
		// if sources are not copied.
		assertEquals("description1 / description2", s.getDescription());
	}

	@Test void testNested()
			throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException
	{
		final Source source1 = view(new Properties(), "description1");
		final Source source2 = view(new Properties(), "description2");
		final Source cascaded1 = cascade(source1, source2);
		assertEquals("description1 / description2", cascaded1.getDescription());
		assertEquals(asList(source1, source2), cascadedSources(cascaded1));

		final Source source3 = view(new Properties(), "description3");
		final Source cascaded2 = cascade(cascaded1, source3);
		assertEquals("description1 / description2 / description3", cascaded2.getDescription());
		assertEquals(asList(cascaded1, source3), cascadedSources(cascaded2));
	}

	private static List<Source> cascadedSources(final Source source)
			throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException
	{
		final Class<?> cascadeClass = Class.forName("com.exedio.cope.util.CascadeSource$Cascade");
		assertEquals(cascadeClass, source.getClass());
		final Field sourcesField = cascadeClass.getDeclaredField("sources");
		sourcesField.setAccessible(true);
		return asList((Source[])sourcesField.get(source));
	}

	@Test void testReload()
	{
		final Source s = cascade(
				new ReloadablePropertiesSource("desc1"),
				new ReloadablePropertiesSource("desc2"));
		final Source r = s.reload();

		assertEquals("desc1(0) / desc2(0)", s.getDescription());
		assertEquals("desc1(1) / desc2(1)", r.getDescription());
	}

	@Test void testReloadAlmostNotNeeded()
	{
		final Source s = cascade(
				new ReloadablePropertiesSource("desc1"),
				RELOADS_TO_THIS_2);
		final Source r = s.reload();

		assertEquals("desc1(0) / RELOADS_TO_THIS_2", s.getDescription());
		assertEquals("desc1(1) / RELOADS_TO_THIS_2", r.getDescription());
	}

	@Test void testReloadNotNeeded()
	{
		final Source s = cascade(RELOADS_TO_THIS_1, RELOADS_TO_THIS_2);
		assertSame(s, s.reload());
	}

	private static final Source RELOADS_TO_THIS_1 = new AssertionErrorPropertiesSource()
	{
		@Override public Source reload()
		{
			return this;
		}
	};

	private static final Source RELOADS_TO_THIS_2 = new AssertionErrorPropertiesSource()
	{
		@Override public Source reload()
		{
			return this;
		}
		@Override public String getDescription()
		{
			return "RELOADS_TO_THIS_2";
		}
	};
}
