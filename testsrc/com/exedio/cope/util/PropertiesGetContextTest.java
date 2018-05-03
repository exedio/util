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
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collection;
import java.util.Collections;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON") // is more compact to write in tests
public class PropertiesGetContextTest
{
	static class TestProperties extends MyProperties
	{
		@SuppressWarnings("unused")
		@SuppressFBWarnings("URF_UNREAD_FIELD") // is read by reflection
		final String stringMandatory = value("stringMandatory", (String)null);

		@Deprecated
		TestProperties(final java.util.Properties source, final String sourceDescription, final Source context)
		{
			super(getSource(source, sourceDescription), context);
		}
	}

	@Test void testGetContext()
	{
		final java.util.Properties pcontext = new java.util.Properties();
		pcontext.setProperty("stringMandatory", "stringMandatory.minimalValue");

		final Properties.Source context = new Properties.Source(){

			@Override
			public String get(final String key)
			{
				throw new RuntimeException(key);
			}

			@Override
			public Collection<String> keySet()
			{
				throw new RuntimeException();
			}

			@Override
			public String getDescription()
			{
				throw new RuntimeException();
			}

			@Override
			public String toString()
			{
				throw new RuntimeException();
			}
		};
		@SuppressWarnings("deprecation") // needed for idea
		final TestProperties properties = new TestProperties(pcontext, "context", context);
		assertSame(context, properties.getContext());

		@SuppressWarnings("deprecation") // needed for idea
		final TestProperties none = new TestProperties(pcontext, "none", null);
		//noinspection ResultOfMethodCallIgnored
		assertFails(
			none::getContext,
			IllegalStateException.class,
			"no context available");
	}

	@Deprecated
	@Test void testGetContextDeprecated()
	{
		final java.util.Properties pcontext = new java.util.Properties();
		pcontext.setProperty("stringMandatory", "stringMandatory.minimalValue");
		final TestProperties context = new TestProperties(pcontext, "context", new Properties.Source(){

			@Override
			public String get(final String key)
			{
				if("a".equals(key))
					return "b";
				else if("a1".equals(key))
					return "b1";
				else if("n".equals(key))
					return null;
				else
					throw new RuntimeException(key);
			}

			@Override
			public Collection<String> keySet()
			{
				return Collections.unmodifiableList(asList("a", "a1"));
			}

			@Override
			public String getDescription()
			{
				return "TestGetContextDescription";
			}

			@Override
			public String toString()
			{
				return "TestGetContextToString";
			}
		});
		assertEquals("b", context.getContext("a"));
		assertEquals("b1", context.getContext("a1"));

		//noinspection ConstantConditions
		assertFails(() ->
			context.getContext(null),
			NullPointerException.class, "key");
		assertFails(() ->
			context.getContext("n"),
			IllegalArgumentException.class,
			"no value available for key >n< in context TestGetContextDescription");

		final TestProperties none = new TestProperties(pcontext, "none", null);
		assertFails(() ->
			none.getContext("c"),
			IllegalStateException.class,
			"no context available");
	}
}
