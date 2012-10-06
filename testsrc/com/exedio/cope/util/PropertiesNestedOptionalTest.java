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
public class PropertiesNestedOptionalTest extends CopeAssert
{
	static class OuterProperties extends Properties
	{
		protected final <T extends Properties> PropertiesField<T> fieldOptional(final String rootKey, final Factory<T> factory)
		{
			final BooleanField enable = field(rootKey, false);
			return enable.get() ? field(rootKey, factory) : null;
		}

		final IntField outer = field("outer", 1001, 501);

		final PropertiesField<InnerProperties> nested = fieldOptional("nested", InnerProperties.factory());

		final BooleanField nestedEnable = (BooleanField)fields.get(fields.size()-((nested!=null) ? 3:1));
		final IntField nestedInner1 = (nested!=null) ? (IntField)fields.get(fields.size()-2) : null;
		final IntField nestedInner2 = (nested!=null) ? (IntField)fields.get(fields.size()-1) : null;

		OuterProperties(final java.util.Properties source)
		{
			super(getSource(source, "someDescription"), null);
		}

		private void assertOuter()
		{
			assertEquals("outer", outer.getKey());
			assertEquals(Integer.valueOf(1001), outer.getDefaultValue());
			assertEquals(false, outer.hasHiddenValue());
			assertEquals(501, outer.getMinimum());

			assertEquals("nested", nestedEnable.getKey());
			assertEquals(Boolean.FALSE, nestedEnable.getDefaultValue());
			assertEquals(false, nestedEnable.hasHiddenValue());
		}

		void assertDisabled()
		{
			assertEquals(list(), getTests());
			assertEqualsUnmodifiable(Arrays.asList(new Properties.Field[]{
					outer,
					nestedEnable,
			}), getFields());

			assertOuter();

			assertNull(nested);
			assertNull(nestedInner1);
			assertNull(nestedInner2);
		}

		void assertEnabled()
		{
			assertEquals(list(), getTests());
			assertEqualsUnmodifiable(Arrays.asList(new Properties.Field[]{
					outer,
					nestedEnable,
					nestedInner1,
					nestedInner2,
			}), getFields());

			assertOuter();

			assertEquals("nested", nested.getRootKey());
			assertEquals("nested.inner1", nestedInner1.getKey());
			assertEquals("nested.inner2", nestedInner2.getKey());

			assertEquals(Integer.valueOf(101), nestedInner1.getDefaultValue());
			assertEquals(Integer.valueOf(102), nestedInner2.getDefaultValue());

			assertEquals(false, nestedInner1.hasHiddenValue());
			assertEquals(false, nestedInner2.hasHiddenValue());

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
			assertEquals(false, inner2.hasHiddenValue());

			assertEquals(51, inner1.getMinimum());
			assertEquals(52, inner2.getMinimum());
		}
	}

	public void testDefaults()
	{
		final java.util.Properties source = new java.util.Properties();

		final OuterProperties outer = new OuterProperties(source);
		outer.assertDisabled();
		assertEquals(1001, outer.outer.get());
		assertEquals(false, outer.nestedEnable.get());
		assertNull(outer.nestedInner1);
		assertNull(outer.nestedInner2);
		assertNull(outer.nested);
	}

	public void testDefaultsNested()
	{
		final java.util.Properties source = new java.util.Properties();
		source.setProperty("nested", "true");

		final OuterProperties outer = new OuterProperties(source);
		outer.assertEnabled();
		assertEquals(1001, outer.outer.get());
		assertEquals(true, outer.nestedEnable.get());
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
		source.setProperty("outer", "1009");
		source.setProperty("nested", "true");
		source.setProperty("nested.inner1", "109");

		final OuterProperties outer = new OuterProperties(source);
		outer.assertEnabled();
		assertEquals(1009, outer.outer.get());
		assertEquals(true, outer.nestedEnable.get());
		assertEquals(109, outer.nestedInner1.get());
		assertEquals(102, outer.nestedInner2.get());

		final InnerProperties inner = outer.nested.get();
		inner.assertIt();
		assertEquals(109, inner.inner1.get());
		assertEquals(102, inner.inner2.get());
	}
}
