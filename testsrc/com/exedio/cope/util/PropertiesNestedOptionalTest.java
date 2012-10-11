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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON") // is more compact to write in tests
public class PropertiesNestedOptionalTest extends CopeAssert
{
	static class OuterProperties extends MyProperties
	{
		final int outer = value("outer", 1001, 501);

		final InnerProperties nested = value("nested", false, InnerProperties.factory());

		final BooleanField nestedEnable = (BooleanField)forKey("nested");
		final IntField nestedInner1 = (IntField)forKey("nested.inner1");
		final IntField nestedInner2 = (IntField)forKey("nested.inner2");

		OuterProperties(final java.util.Properties source)
		{
			super(getSource(source, "someDescription"), null);
		}

		final IntField outerF = (IntField)forKey("outer");
		final PropertiesField<InnerProperties> nestedF = forPrefix("nested", InnerProperties.class);

		private void assertOuter()
		{
			assertEquals("outer", outerF.getKey());
			assertEquals(Integer.valueOf(1001), outerF.getDefaultValue());
			assertEquals(false, outerF.hasHiddenValue());
			assertEquals(501, outerF.getMinimum());

			assertEquals("nested", nestedEnable.getKey());
			assertEquals(Boolean.FALSE, nestedEnable.getDefaultValue());
			assertEquals(false, nestedEnable.hasHiddenValue());
		}

		void assertDisabled()
		{
			assertEquals(list(), getTests());
			assertEqualsUnmodifiable(Arrays.asList(new Properties.Field[]{
					outerF,
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
					outerF,
					nestedEnable,
					nestedInner1,
					nestedInner2,
			}), getFields());

			assertOuter();

			assertEquals("nested", nestedF.getKey());
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

	static class InnerProperties extends MyProperties
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

		final int inner1 = value("inner1", 101, 51);
		final int inner2 = value("inner2", 102, 52);

		InnerProperties(final Source source)
		{
			super(source, null);
		}

		final IntField inner1F = (IntField)forKey("inner1");
		final IntField inner2F = (IntField)forKey("inner2");

		void assertIt()
		{
			assertEquals(list(), getTests());
			assertEqualsUnmodifiable(Arrays.asList(new Properties.Field[]{
					inner1F,
					inner2F,
			}), getFields());

			assertEquals("inner1", inner1F.getKey());
			assertEquals("inner2", inner2F.getKey());

			assertEquals(Integer.valueOf(101), inner1F.getDefaultValue());
			assertEquals(Integer.valueOf(102), inner2F.getDefaultValue());

			assertEquals(false, inner1F.hasHiddenValue());
			assertEquals(false, inner2F.hasHiddenValue());

			assertEquals(51, inner1F.getMinimum());
			assertEquals(52, inner2F.getMinimum());
		}
	}

	public void testDefaults()
	{
		final java.util.Properties source = new java.util.Properties();

		final OuterProperties outer = new OuterProperties(source);
		outer.assertDisabled();
		assertEquals(1001, outer.outer);
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
		assertEquals(1001, outer.outer);
		assertEquals(true, outer.nestedEnable.get());
		assertEquals(101, outer.nestedInner1.get());
		assertEquals(102, outer.nestedInner2.get());

		final InnerProperties inner = outer.nested;
		inner.assertIt();
		assertEquals(101, inner.inner1);
		assertEquals(102, inner.inner2);
	}

	public void testSet()
	{
		final java.util.Properties source = new java.util.Properties();
		source.setProperty("outer", "1009");
		source.setProperty("nested", "true");
		source.setProperty("nested.inner1", "109");

		final OuterProperties outer = new OuterProperties(source);
		outer.assertEnabled();
		assertEquals(1009, outer.outer);
		assertEquals(true, outer.nestedEnable.get());
		assertEquals(109, outer.nestedInner1.get());
		assertEquals(102, outer.nestedInner2.get());

		final InnerProperties inner = outer.nested;
		inner.assertIt();
		assertEquals(109, inner.inner1);
		assertEquals(102, inner.inner2);
	}
}
