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

		final InnerProperties nestedFalse = value("nested", false, InnerProperties.factory());

		final BooleanField nestedFalseEnable = (BooleanField)forKey("nested");
		final IntField nestedFalseInner1 = (IntField)forKey("nested.inner1");
		final IntField nestedFalseInner2 = (IntField)forKey("nested.inner2");

		OuterProperties(final java.util.Properties source)
		{
			super(getSource(source, "someDescription"), null);
		}

		final IntField outerF = (IntField)forKey("outer");
		final PropertiesField<InnerProperties> nestedFalseF = forPrefix("nested", InnerProperties.class);

		private void assertOuter()
		{
			assertEquals("outer", outerF.getKey());
			assertEquals(Integer.valueOf(1001), outerF.getDefaultValue());
			assertEquals(false, outerF.hasHiddenValue());
			assertEquals(501, outerF.getMinimum());

			assertEquals("nested", nestedFalseEnable.getKey());
			assertEquals(Boolean.FALSE, nestedFalseEnable.getDefaultValue());
			assertEquals(false, nestedFalseEnable.hasHiddenValue());
		}

		void assertDisabled()
		{
			assertEquals(list(), getTests());
			assertEqualsUnmodifiable(Arrays.asList(new Properties.Field[]{
					outerF,
					nestedFalseEnable,
			}), getFields());

			assertOuter();

			assertNull(nestedFalse);
			assertNull(nestedFalseInner1);
			assertNull(nestedFalseInner2);
		}

		void assertEnabled()
		{
			assertEquals(list(), getTests());
			assertEqualsUnmodifiable(Arrays.asList(new Properties.Field[]{
					outerF,
					nestedFalseEnable,
					nestedFalseInner1,
					nestedFalseInner2,
			}), getFields());

			assertOuter();

			assertEquals("nested", nestedFalseF.getKey());
			assertEquals("nested.inner1", nestedFalseInner1.getKey());
			assertEquals("nested.inner2", nestedFalseInner2.getKey());

			assertEquals(Integer.valueOf(101), nestedFalseInner1.getDefaultValue());
			assertEquals(Integer.valueOf(102), nestedFalseInner2.getDefaultValue());

			assertEquals(false, nestedFalseInner1.hasHiddenValue());
			assertEquals(false, nestedFalseInner2.hasHiddenValue());

			assertEquals( 51, nestedFalseInner1.getMinimum());
			assertEquals( 52, nestedFalseInner2.getMinimum());
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
		assertEquals(false, outer.nestedFalseEnable.get());
		assertNull(outer.nestedFalseInner1);
		assertNull(outer.nestedFalseInner2);
		assertNull(outer.nestedFalse);
	}

	public void testDefaultsNested()
	{
		final java.util.Properties source = new java.util.Properties();
		source.setProperty("nested", "true");

		final OuterProperties outer = new OuterProperties(source);
		outer.assertEnabled();
		assertEquals(1001, outer.outer);
		assertEquals(true, outer.nestedFalseEnable.get());
		assertEquals(101, outer.nestedFalseInner1.get());
		assertEquals(102, outer.nestedFalseInner2.get());

		final InnerProperties innerFalse = outer.nestedFalse;
		innerFalse.assertIt();
		assertEquals(101, innerFalse.inner1);
		assertEquals(102, innerFalse.inner2);
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
		assertEquals(true, outer.nestedFalseEnable.get());
		assertEquals(109, outer.nestedFalseInner1.get());
		assertEquals(102, outer.nestedFalseInner2.get());

		final InnerProperties innerFalse = outer.nestedFalse;
		innerFalse.assertIt();
		assertEquals(109, innerFalse.inner1);
		assertEquals(102, innerFalse.inner2);
	}
}
