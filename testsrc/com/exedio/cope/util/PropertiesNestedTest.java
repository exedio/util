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

@edu.umd.cs.findbugs.annotations.SuppressWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON") // is more compact to write in tests
public class PropertiesNestedTest extends CopeAssert
{
	static class OuterProperties extends Properties
	{
		final IntField outer1 = field("outer1", 1001, 501);
		final IntField outer2 = field("outer2", 1002, 502);
		final PropertiesField<InnerProperties> nested = field("nested", InnerProperties.factory());
		final IntField nestedInner1 = (IntField)fields.get(fields.size()-4);
		final IntField nestedInner2 = (IntField)fields.get(fields.size()-3);
		final IntField nestedDrinner1 = (IntField)fields.get(fields.size()-2);
		final IntField nestedDrinner2 = (IntField)fields.get(fields.size()-1);

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
					nestedDrinner1,
					nestedDrinner2,
			}), getFields());

			assertEquals("outer1", outer1.getKey());
			assertEquals("outer2", outer2.getKey());
			assertEquals("nested", nested.getKey());
			assertEquals("nested.inner1", nestedInner1.getKey());
			assertEquals("nested.inner2", nestedInner2.getKey());
			assertEquals("nested.nested.drinner1", nestedDrinner1.getKey());
			assertEquals("nested.nested.drinner2", nestedDrinner2.getKey());

			assertEquals(Integer.valueOf(1001), outer1.getDefaultValue());
			assertEquals(Integer.valueOf(1002), outer2.getDefaultValue());
			assertEquals(Integer.valueOf(101), nestedInner1.getDefaultValue());
			assertEquals(Integer.valueOf(102), nestedInner2.getDefaultValue());
			assertEquals(Integer.valueOf(11), nestedDrinner1.getDefaultValue());
			assertEquals(Integer.valueOf(12), nestedDrinner2.getDefaultValue());

			assertEquals(false, outer1.hasHiddenValue());
			assertEquals(false, outer2.hasHiddenValue());
			assertEquals(false, nestedInner1.hasHiddenValue());
			assertEquals(false, nestedInner2.hasHiddenValue());
			assertEquals(false, nestedDrinner1.hasHiddenValue());
			assertEquals(false, nestedDrinner2.hasHiddenValue());

			assertEquals(501, outer1.getMinimum());
			assertEquals(502, outer2.getMinimum());
			assertEquals( 51, nestedInner1.getMinimum());
			assertEquals( 52, nestedInner2.getMinimum());
			assertEquals(  1, nestedDrinner1.getMinimum());
			assertEquals(  2, nestedDrinner2.getMinimum());
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
		final PropertiesField<DrinnerProperties> nested = field("nested", DrinnerProperties.factory());
		final IntField nestedDrinner1 = (IntField)fields.get(fields.size()-2);
		final IntField nestedDrinner2 = (IntField)fields.get(fields.size()-1);

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
					nestedDrinner1,
					nestedDrinner2,
			}), getFields());

			assertEquals("inner1", inner1.getKey());
			assertEquals("inner2", inner2.getKey());
			assertEquals("nested.drinner1", nestedDrinner1.getKey());
			assertEquals("nested.drinner2", nestedDrinner2.getKey());

			assertEquals(Integer.valueOf(101), inner1.getDefaultValue());
			assertEquals(Integer.valueOf(102), inner2.getDefaultValue());
			assertEquals(Integer.valueOf(11), nestedDrinner1.getDefaultValue());
			assertEquals(Integer.valueOf(12), nestedDrinner2.getDefaultValue());

			assertEquals(false, inner1.hasHiddenValue());
			assertEquals(false, inner2.hasHiddenValue());
			assertEquals(false, nestedDrinner1.hasHiddenValue());
			assertEquals(false, nestedDrinner2.hasHiddenValue());

			assertEquals(51, inner1.getMinimum());
			assertEquals(52, inner2.getMinimum());
			assertEquals( 1, nestedDrinner1.getMinimum());
			assertEquals( 2, nestedDrinner2.getMinimum());
		}
	}

	static class DrinnerProperties extends Properties
	{
		static Factory<DrinnerProperties> factory()
		{
			return new Factory<DrinnerProperties>()
			{
				public DrinnerProperties create(final Source source)
				{
					return new DrinnerProperties(source);
				}
			};
		}

		final IntField drinner1 = field("drinner1", 11, 1);
		final IntField drinner2 = field("drinner2", 12, 2);

		DrinnerProperties(final Source source)
		{
			super(source, null);
		}

		void assertIt()
		{
			assertEquals(list(), getTests());
			assertEqualsUnmodifiable(Arrays.asList(new Properties.Field[]{
					drinner1,
					drinner2,
			}), getFields());

			assertEquals("drinner1", drinner1.getKey());
			assertEquals("drinner2", drinner2.getKey());

			assertEquals(Integer.valueOf(11), drinner1.getDefaultValue());
			assertEquals(Integer.valueOf(12), drinner2.getDefaultValue());

			assertEquals(false, drinner1.hasHiddenValue());
			assertEquals(false, drinner2.hasHiddenValue());

			assertEquals(1, drinner1.getMinimum());
			assertEquals(2, drinner2.getMinimum());
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
		assertEquals(11, outer.nestedDrinner1.get());
		assertEquals(12, outer.nestedDrinner2.get());

		final InnerProperties inner = outer.nested.get();
		inner.assertIt();
		assertEquals(101, inner.inner1.get());
		assertEquals(102, inner.inner2.get());
		assertEquals(11, inner.nestedDrinner1.get());
		assertEquals(12, inner.nestedDrinner2.get());

		final DrinnerProperties drinner = inner.nested.get();
		drinner.assertIt();
		assertEquals(11, drinner.drinner1.get());
		assertEquals(12, drinner.drinner2.get());
	}

	public void testSet()
	{
		final java.util.Properties source = new java.util.Properties();
		source.setProperty("outer1", "1009");
		source.setProperty("nested.inner1", "109");
		source.setProperty("nested.nested.drinner1", "19");

		final OuterProperties outer = new OuterProperties(source);
		outer.assertIt();
		assertEquals(1009, outer.outer1.get());
		assertEquals(1002, outer.outer2.get());
		assertEquals(109, outer.nestedInner1.get());
		assertEquals(102, outer.nestedInner2.get());
		assertEquals(19, outer.nestedDrinner1.get());
		assertEquals(12, outer.nestedDrinner2.get());

		final InnerProperties inner = outer.nested.get();
		inner.assertIt();
		assertEquals(109, inner.inner1.get());
		assertEquals(102, inner.inner2.get());
		assertEquals(19, inner.nestedDrinner1.get());
		assertEquals(12, inner.nestedDrinner2.get());

		final DrinnerProperties drinner = inner.nested.get();
		drinner.assertIt();
		assertEquals(19, drinner.drinner1.get());
		assertEquals(12, drinner.drinner2.get());
	}

	@SuppressFBWarnings("DLS_DEAD_LOCAL_STORE")
	public void testWrong()
	{
		final java.util.Properties source = new java.util.Properties();
		source.setProperty("nested.inner1", "109x");

		try
		{
			@SuppressWarnings("unused")
			final OuterProperties x =
				new OuterProperties(source);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("property nested in someDescription invalid, see nested exception", e.getMessage());
			final Throwable cause = e.getCause();
			assertEquals(
					"property inner1 in someDescription (prefix nested.) has invalid value, expected an integer greater or equal 51, but got >109x<.",
					cause.getMessage());
			assertTrue(cause instanceof IllegalArgumentException);
		}
	}

	@SuppressFBWarnings("DLS_DEAD_LOCAL_STORE")
	public void testWrongDrinner()
	{
		final java.util.Properties source = new java.util.Properties();
		source.setProperty("nested.nested.drinner1", "19x");

		try
		{
			@SuppressWarnings("unused")
			final OuterProperties x =
				new OuterProperties(source);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("property nested in someDescription invalid, see nested exception", e.getMessage());
			final Throwable cause = e.getCause();
			assertEquals("property nested in someDescription (prefix nested.) invalid, see nested exception", cause.getMessage());
			assertTrue(cause instanceof IllegalArgumentException);
			final Throwable causeCause = cause.getCause();
			assertEquals(
					"property drinner1 in someDescription (prefix nested.) (prefix nested.) has invalid value, expected an integer greater or equal 1, but got >19x<.",
					causeCause.getMessage());
			assertTrue(causeCause instanceof IllegalArgumentException);
		}
	}
}
