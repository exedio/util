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

import com.exedio.cope.junit.CopeAssert;

@edu.umd.cs.findbugs.annotations.SuppressWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON") // is more compact to write in tests
public class PropertiesNestedTest extends CopeAssert
{
	static class OuterProperties extends Properties
	{
		final IntField outer1 = field("outer1", 1001, 501);
		final IntField outer2 = field("outer2", 1002, 502);
		final PropertiesField<InnerProperties> nested = field("nested", InnerProperties.factory());
		final IntField nestedInner1 = (IntField)fields.get(fields.size()-2);
		final IntField nestedInner2 = (IntField)fields.get(fields.size()-1);

		OuterProperties(final java.util.Properties source)
		{
			super(getSource(source, "someDescription"), null);
		}

		void assertIt()
		{
			assertEquals(list(), getTests());
			assertEqualsUnmodifiable(Arrays.asList(new Properties.Field[]{
					outer1,
					outer2,
					nestedInner1,
					nestedInner2,
			}), getFields());

			assertEquals("outer1", outer1.getKey());
			assertEquals("outer2", outer2.getKey());
			assertEquals("nested", nested.getRootKey());
			assertEquals("nested.inner1", nestedInner1.getKey());
			assertEquals("nested.inner2", nestedInner2.getKey());

			assertEquals(Integer.valueOf(1001), outer1.getDefaultValue());
			assertEquals(Integer.valueOf(1002), outer2.getDefaultValue());
			assertEquals(Integer.valueOf(101), nestedInner1.getDefaultValue());
			assertEquals(Integer.valueOf(102), nestedInner2.getDefaultValue());

			assertEquals(false, outer1.hasHiddenValue());
			assertEquals(false, outer2.hasHiddenValue());
			assertEquals(false, nestedInner1.hasHiddenValue());
			assertEquals(false, nestedInner2.hasHiddenValue());

			assertEquals(501, outer1.getMinimum());
			assertEquals(502, outer2.getMinimum());
			assertEquals( 51, nestedInner1.getMinimum());
			assertEquals( 52, nestedInner2.getMinimum());
		}
	}

	static class InnerProperties extends Properties
	{
		static Factory<InnerProperties> factory()
		{
			return new Factory<InnerProperties>()
			{
				public InnerProperties create(final Source source)
				{
					return new InnerProperties(source);
				}
			};
		}

		final IntField inner1 = field("inner1", 101, 51);
		final IntField inner2 = field("inner2", 102, 52);

		InnerProperties(final Source source)
		{
			super(source, null);
		}

		void assertIt()
		{
			assertEquals(list(), getTests());
			assertEqualsUnmodifiable(Arrays.asList(new Properties.Field[]{
					inner1,
					inner2,
			}), getFields());

			assertEquals("inner1", inner1.getKey());
			assertEquals("inner2", inner2.getKey());

			assertEquals(Integer.valueOf(101), inner1.getDefaultValue());
			assertEquals(Integer.valueOf(102), inner2.getDefaultValue());

			assertEquals(false, inner1.hasHiddenValue());
			assertEquals(false, inner1.hasHiddenValue());

			assertEquals(51, inner1.getMinimum());
			assertEquals(52, inner2.getMinimum());
		}
	}

	public void testDefaults()
	{
		final java.util.Properties source = new java.util.Properties();

		final OuterProperties outer = new OuterProperties(source);
		outer.assertIt();
		assertEquals(1001, outer.outer1.get());
		assertEquals(1002, outer.outer2.get());
		assertEquals(101, outer.nestedInner1.get());
		assertEquals(102, outer.nestedInner2.get());

		final InnerProperties inner = outer.nested.get();
		inner.assertIt();
		assertEquals(101, inner.inner1.get());
		assertEquals(102, inner.inner2.get());
	}

	public void testSet()
	{
		final java.util.Properties source = new java.util.Properties();
		source.setProperty("outer1", "1009");
		source.setProperty("nested.inner1", "109");

		final OuterProperties outer = new OuterProperties(source);
		outer.assertIt();
		assertEquals(1009, outer.outer1.get());
		assertEquals(1002, outer.outer2.get());
		assertEquals(109, outer.nestedInner1.get());
		assertEquals(102, outer.nestedInner2.get());

		final InnerProperties inner = outer.nested.get();
		inner.assertIt();
		assertEquals(109, inner.inner1.get());
		assertEquals(102, inner.inner2.get());
	}

	public void testWrong()
	{
		final java.util.Properties source = new java.util.Properties();
		source.setProperty("nested.inner1", "109x");

		try
		{
			@SuppressWarnings("unused")
			final OuterProperties x =
				new OuterProperties(source);
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"property inner1 in someDescription (prefix nested.) has invalid value, expected an integer greater or equal 51, but got >109x<.",
					e.getMessage());
		}
	}
}
