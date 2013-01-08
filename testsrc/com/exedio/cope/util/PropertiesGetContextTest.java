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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.exedio.cope.junit.CopeAssert;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON") // is more compact to write in tests
public class PropertiesGetContextTest extends CopeAssert
{
	static class TestProperties extends MyProperties
	{
		@SuppressFBWarnings("URF_UNREAD_FIELD") // is read by reflection
		final String stringMandatory = value("stringMandatory", (String)null);

		@Deprecated
		TestProperties(final java.util.Properties source, final String sourceDescription, final Source context)
		{
			super(getSource(source, sourceDescription), context);
		}
	}

	public void testGetContext()
	{
		final java.util.Properties pcontext = new java.util.Properties();
		pcontext.setProperty("stringMandatory", "stringMandatory.minimalValue");

		final Properties.Source context = new Properties.Source(){

			public String get(final String key)
			{
				throw new RuntimeException(key);
			}

			public Collection<String> keySet()
			{
				throw new RuntimeException();
			}

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
		final TestProperties properties = new TestProperties(pcontext, "context", context);
		assertSame(context, properties.getContext());

		final TestProperties none = new TestProperties(pcontext, "none", null);
		try
		{
			none.getContext();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("no context available", e.getMessage());
		}
	}

	@Deprecated
	public void testGetContextDeprecated()
	{
		final java.util.Properties pcontext = new java.util.Properties();
		pcontext.setProperty("stringMandatory", "stringMandatory.minimalValue");
		final TestProperties context = new TestProperties(pcontext, "context", new Properties.Source(){

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

			public Collection<String> keySet()
			{
				return Collections.unmodifiableList(Arrays.asList("a", "a1"));
			}

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

		try
		{
			context.getContext(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("key", e.getMessage());
		}
		try
		{
			context.getContext("n");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("no value available for key >n< in context TestGetContextDescription", e.getMessage());
		}

		final TestProperties none = new TestProperties(pcontext, "none", null);
		try
		{
			none.getContext("c");
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("no context available", e.getMessage());
		}
	}
}
