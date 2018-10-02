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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON") // is more compact to write in tests
public class PropertiesDuplicateTest
{
	@SuppressWarnings("unused")
	@SuppressFBWarnings({"URF_UNREAD_FIELD","NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS"}) // is read by reflection
	static class KeyNullProperties extends Properties
	{
		final boolean value = value(null, false);
		KeyNullProperties()
		{
			super(EMPTY_WITHOUT_KEY_CHECK);
		}
	}

	@Test void testKeyNull()
	{
		assertFails(
				KeyNullProperties::new,
				NullPointerException.class,
				"key");
	}


	@SuppressWarnings("unused")
	@SuppressFBWarnings("URF_UNREAD_FIELD") // is read by reflection
	static class KeyEmptyProperties extends Properties
	{
		final boolean value = value("", false);
		KeyEmptyProperties()
		{
			super(EMPTY_WITHOUT_KEY_CHECK);
		}
	}

	@Test void testKeyEmpty()
	{
		assertFails(
				KeyEmptyProperties::new,
				IllegalArgumentException.class,
				"key must not be empty");
	}


	private static final Properties.Source EMPTY_WITHOUT_KEY_CHECK = new AssertionErrorPropertiesSource()
	{
		@Override
		public String get(final String key)
		{
			return null;
		}
		@Override
		public String getDescription()
		{
			return "empty";
		}
	};


	@SuppressWarnings("unused")
	@SuppressFBWarnings("URF_UNREAD_FIELD") // is read by reflection
	static class DuplicateProperties extends Properties
	{
		final boolean duplicate1 = value("duplicate", false);
		final boolean duplicate2 = value("duplicate", true);

		DuplicateProperties()
		{
			super(Sources.EMPTY);
		}
	}

	@SuppressWarnings("unused")
	@Test void testDuplicate()
	{
		assertFails(
			DuplicateProperties::new,
			IllegalArgumentException.class,
			"duplicate key 'duplicate'");
	}


	static class Nested extends Properties
	{
		Nested(final Source source)
		{
			super(source);
			value("field", false);
		}
	}

	static final Properties.Factory<Nested> factory = Nested::new;

	@SuppressWarnings("unused")
	@Test void testSimpleNestedEquals()
	{
		class Props extends Properties
		{
			Props()
			{
				super(Sources.EMPTY);
				value("duplicate.", false);
				value("duplicate", factory);
			}
		}
		//noinspection Convert2MethodRef method reference does not compile in javac 1.8.0_131
		assertFails(() ->
			new Props(),
			IllegalArgumentException.class,
			"properties field 'duplicate' collides with field 'duplicate.'");
	}

	@SuppressWarnings("unused")
	@Test void testSimpleNestedStartsWith()
	{
		class Props extends Properties
		{
			Props()
			{
				super(Sources.EMPTY);
				value("duplicate.x", false);
				value("duplicate", factory);
			}
		}
		//noinspection Convert2MethodRef method reference does not compile in javac 1.8.0_131
		assertFails(() ->
			new Props(),
			IllegalArgumentException.class,
			"properties field 'duplicate' collides with field 'duplicate.x'");
	}

	@SuppressWarnings("unused")
	@Test void testNestedSimpleEquals()
	{
		class Props extends Properties
		{
			Props()
			{
				super(Sources.EMPTY);
				value("duplicate", factory);
				value("duplicate.", false);
			}
		}
		//noinspection Convert2MethodRef method reference does not compile in javac 1.8.0_131
		assertFails(() ->
			new Props(),
			IllegalArgumentException.class,
			"properties field 'duplicate.' collides with field 'duplicate.'"); // TODO remove dot
	}

	@SuppressWarnings("unused")
	@Test void testNestedSimpleStartsWith()
	{
		class Props extends Properties
		{
			Props()
			{
				super(Sources.EMPTY);
				value("duplicate", factory);
				value("duplicate.x", false);
			}
		}
		//noinspection Convert2MethodRef method reference does not compile in javac 1.8.0_131
		assertFails(() ->
			new Props(),
			IllegalArgumentException.class,
			"properties field 'duplicate.' collides with field 'duplicate.x'"); // TODO remove dot
	}

	@SuppressWarnings("unused")
	@Test void testNestedNestedEquals()
	{
		class Props extends Properties
		{
			Props()
			{
				super(Sources.EMPTY);
				value("duplicate", factory);
				value("duplicate", factory);
			}
		}
		//noinspection Convert2MethodRef method reference does not compile in javac 1.8.0_131
		assertFails(() ->
			new Props(),
			IllegalArgumentException.class,
			"properties field 'duplicate.' collides with properties field 'duplicate.'");
	}

	@SuppressWarnings("unused")
	@Test void testNestedNestedStartsWith()
	{
		class Props extends Properties
		{
			Props()
			{
				super(Sources.EMPTY);
				value("duplicate", factory);
				value("duplicate.x", factory);
			}
		}
		//noinspection Convert2MethodRef method reference does not compile in javac 1.8.0_131
		assertFails(() ->
			new Props(),
			IllegalArgumentException.class,
			"properties field 'duplicate.x.' collides with properties field 'duplicate.'");
	}

	@SuppressWarnings("unused")
	@Test void testNestedNestedStartsWith2()
	{
		class Props extends Properties
		{
			Props()
			{
				super(Sources.EMPTY);
				value("duplicate.x", factory);
				value("duplicate", factory);
			}
		}
		//noinspection Convert2MethodRef method reference does not compile in javac 1.8.0_131
		assertFails(() ->
			new Props(),
			IllegalArgumentException.class,
			"properties field 'duplicate.' collides with properties field 'duplicate.x.'");
	}
}
