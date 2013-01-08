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

import com.exedio.cope.junit.CopeAssert;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON") // is more compact to write in tests
public class PropertiesDuplicateTest extends CopeAssert
{
	@SuppressFBWarnings("URF_UNREAD_FIELD") // is read by reflection
	static class DuplicateProperties extends MyProperties
	{
		final boolean duplicate1 = value("duplicate", false);
		final boolean duplicate2 = value("duplicate", true);

		DuplicateProperties()
		{
			super(Properties.EMPTY_SOURCE);
		}
	}

	@SuppressWarnings("unused")
	public void testDuplicate()
	{
		try
		{
			new DuplicateProperties();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("duplicate key 'duplicate'", e.getMessage());
		}
	}


	static class Nested extends MyProperties
	{
		Nested(final Source source)
		{
			super(source);
			value("field", false);
		}
	}

	static final Properties.Factory<Nested> factory = new Properties.Factory<Nested>()
	{
		public Nested create(final Properties.Source source)
		{
			return new Nested(source);
		}
	};

	@SuppressWarnings("unused")
	public void testSimpleNestedEquals()
	{
		class Props extends MyProperties
		{
			Props()
			{
				super(EMPTY_SOURCE);
				value("duplicate.", false);
				value("duplicate", factory);
			}
		}
		try
		{
			new Props();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("properties field 'duplicate' collides with field 'duplicate.'", e.getMessage());
		}
	}

	@SuppressWarnings("unused")
	public void testSimpleNestedStartsWith()
	{
		class Props extends MyProperties
		{
			Props()
			{
				super(EMPTY_SOURCE);
				value("duplicate.x", false);
				value("duplicate", factory);
			}
		}
		try
		{
			new Props();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("properties field 'duplicate' collides with field 'duplicate.x'", e.getMessage());
		}
	}

	@SuppressWarnings("unused")
	public void testNestedSimpleEquals()
	{
		class Props extends MyProperties
		{
			Props()
			{
				super(EMPTY_SOURCE);
				value("duplicate", factory);
				value("duplicate.", false);
			}
		}
		try
		{
			new Props();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("properties field 'duplicate.' collides with field 'duplicate.'", e.getMessage()); // TODO remove dot
		}
	}

	@SuppressWarnings("unused")
	public void testNestedSimpleStartsWith()
	{
		class Props extends MyProperties
		{
			Props()
			{
				super(EMPTY_SOURCE);
				value("duplicate", factory);
				value("duplicate.x", false);
			}
		}
		try
		{
			new Props();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("properties field 'duplicate.' collides with field 'duplicate.x'", e.getMessage()); // TODO remove dot
		}
	}

	@SuppressWarnings("unused")
	public void testNestedNestedEquals()
	{
		class Props extends MyProperties
		{
			Props()
			{
				super(EMPTY_SOURCE);
				value("duplicate", factory);
				value("duplicate", factory);
			}
		}
		try
		{
			new Props();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("properties field 'duplicate.' collides with properties field 'duplicate.'", e.getMessage());
		}
	}

	@SuppressWarnings("unused")
	public void testNestedNestedStartsWith()
	{
		class Props extends MyProperties
		{
			Props()
			{
				super(EMPTY_SOURCE);
				value("duplicate", factory);
				value("duplicate.x", factory);
			}
		}
		try
		{
			new Props();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("properties field 'duplicate.x.' collides with properties field 'duplicate.'", e.getMessage());
		}
	}

	@SuppressWarnings("unused")
	public void testNestedNestedStartsWith2()
	{
		class Props extends MyProperties
		{
			Props()
			{
				super(EMPTY_SOURCE);
				value("duplicate.x", factory);
				value("duplicate", factory);
			}
		}
		try
		{
			new Props();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("properties field 'duplicate.' collides with properties field 'duplicate.x.'", e.getMessage());
		}
	}
}
