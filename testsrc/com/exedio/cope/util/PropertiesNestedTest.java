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

import static com.exedio.cope.util.Sources.view;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import com.exedio.cope.junit.CopeAssert;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import org.junit.Test;

@SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON") // is more compact to write in tests
public class PropertiesNestedTest extends CopeAssert
{
	static class OuterProperties extends MyProperties
	{
		final int outer1 = value("outer1", 1001, 501);
		final int outer2 = value("outer2", 1002, 502);
		final InnerProperties nested = value("nestedO", InnerProperties.factory());
		final IntField nestedInner1 = (IntField)forKey("nestedO.inner1");
		final IntField nestedInner2 = (IntField)forKey("nestedO.inner2");
		final IntField nestedDrinner1 = (IntField)forKey("nestedO.nestedI.drinner1");
		final IntField nestedDrinner2 = (IntField)forKey("nestedO.nestedI.drinner2");

		OuterProperties(final java.util.Properties source)
		{
			super(view(source, "someDescription"));
		}

		final IntField outer1F = (IntField)forKey("outer1");
		final IntField outer2F = (IntField)forKey("outer2");
		final PropertiesField<InnerProperties> nestedF = forPrefix("nestedO", InnerProperties.class);

		void assertIt()
		{
			assertEquals(list(), getTests());
			assertEqualsUnmodifiable(Arrays.asList(new Properties.Field[]{
					outer1F,
					outer2F,
					nestedInner1,
					nestedInner2,
					nestedDrinner1,
					nestedDrinner2,
			}), getFields());

			assertEquals("outer1", outer1F.getKey());
			assertEquals("outer2", outer2F.getKey());
			assertEquals("nestedO", nestedF.getKey());
			assertEquals("nestedO.inner1", nestedInner1.getKey());
			assertEquals("nestedO.inner2", nestedInner2.getKey());
			assertEquals("nestedO.nestedI.drinner1", nestedDrinner1.getKey());
			assertEquals("nestedO.nestedI.drinner2", nestedDrinner2.getKey());

			assertEquals(Integer.valueOf(1001), outer1F.getDefaultValue());
			assertEquals(Integer.valueOf(1002), outer2F.getDefaultValue());
			assertEquals(Integer.valueOf(101), nestedInner1.getDefaultValue());
			assertEquals(Integer.valueOf(102), nestedInner2.getDefaultValue());
			assertEquals(Integer.valueOf(11), nestedDrinner1.getDefaultValue());
			assertEquals(Integer.valueOf(12), nestedDrinner2.getDefaultValue());

			assertEquals(false, outer1F.hasHiddenValue());
			assertEquals(false, outer2F.hasHiddenValue());
			assertEquals(false, nestedInner1.hasHiddenValue());
			assertEquals(false, nestedInner2.hasHiddenValue());
			assertEquals(false, nestedDrinner1.hasHiddenValue());
			assertEquals(false, nestedDrinner2.hasHiddenValue());

			assertEquals(501, outer1F.getMinimum());
			assertEquals(502, outer2F.getMinimum());
			assertEquals( 51, nestedInner1.getMinimum());
			assertEquals( 52, nestedInner2.getMinimum());
			assertEquals(  1, nestedDrinner1.getMinimum());
			assertEquals(  2, nestedDrinner2.getMinimum());
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
		final DrinnerProperties nested = value("nestedI", DrinnerProperties.factory());
		final IntField nestedDrinner1 = (IntField)forKey("nestedI.drinner1");
		final IntField nestedDrinner2 = (IntField)forKey("nestedI.drinner2");

		InnerProperties(final Source source)
		{
			super(source);
		}

		final IntField inner1F = (IntField)forKey("inner1");
		final IntField inner2F = (IntField)forKey("inner2");
		final PropertiesField<DrinnerProperties> nestedF = forPrefix("nestedI", DrinnerProperties.class);

		void assertIt()
		{
			assertEquals(list(), getTests());
			assertEqualsUnmodifiable(Arrays.asList(new Properties.Field[]{
					inner1F,
					inner2F,
					nestedDrinner1,
					nestedDrinner2,
			}), getFields());

			assertEquals("inner1", inner1F.getKey());
			assertEquals("inner2", inner2F.getKey());
			assertEquals("nestedI.drinner1", nestedDrinner1.getKey());
			assertEquals("nestedI.drinner2", nestedDrinner2.getKey());

			assertEquals(Integer.valueOf(101), inner1F.getDefaultValue());
			assertEquals(Integer.valueOf(102), inner2F.getDefaultValue());
			assertEquals(Integer.valueOf(11), nestedDrinner1.getDefaultValue());
			assertEquals(Integer.valueOf(12), nestedDrinner2.getDefaultValue());

			assertEquals(false, inner1F.hasHiddenValue());
			assertEquals(false, inner2F.hasHiddenValue());
			assertEquals(false, nestedDrinner1.hasHiddenValue());
			assertEquals(false, nestedDrinner2.hasHiddenValue());

			assertEquals(51, inner1F.getMinimum());
			assertEquals(52, inner2F.getMinimum());
			assertEquals( 1, nestedDrinner1.getMinimum());
			assertEquals( 2, nestedDrinner2.getMinimum());
		}
	}

	static class DrinnerProperties extends MyProperties
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

		final int drinner1 = value("drinner1", 11, 1);
		final int drinner2 = value("drinner2", 12, 2);

		DrinnerProperties(final Source source)
		{
			super(source);
		}

		final IntField drinner1F = (IntField)forKey("drinner1");
		final IntField drinner2F = (IntField)forKey("drinner2");

		void assertIt()
		{
			assertEquals(list(), getTests());
			assertEqualsUnmodifiable(Arrays.asList(new Properties.Field[]{
					drinner1F,
					drinner2F,
			}), getFields());

			assertEquals("drinner1", drinner1F.getKey());
			assertEquals("drinner2", drinner2F.getKey());

			assertEquals(Integer.valueOf(11), drinner1F.getDefaultValue());
			assertEquals(Integer.valueOf(12), drinner2F.getDefaultValue());

			assertEquals(false, drinner1F.hasHiddenValue());
			assertEquals(false, drinner2F.hasHiddenValue());

			assertEquals(1, drinner1F.getMinimum());
			assertEquals(2, drinner2F.getMinimum());
		}
	}

	@SuppressWarnings("static-method")
	@Test public final void testDefaults()
	{
		final java.util.Properties source = new java.util.Properties();

		final OuterProperties outer = new OuterProperties(source);
		outer.assertIt();
		assertEquals(1001, outer.outer1);
		assertEquals(1002, outer.outer2);
		assertEquals(101, outer.nestedInner1.get());
		assertEquals(102, outer.nestedInner2.get());
		assertEquals(11, outer.nestedDrinner1.get());
		assertEquals(12, outer.nestedDrinner2.get());

		final InnerProperties inner = outer.nested;
		inner.assertIt();
		assertEquals(101, inner.inner1);
		assertEquals(102, inner.inner2);
		assertEquals(11, inner.nestedDrinner1.get());
		assertEquals(12, inner.nestedDrinner2.get());

		final DrinnerProperties drinner = inner.nested;
		drinner.assertIt();
		assertEquals(11, drinner.drinner1);
		assertEquals(12, drinner.drinner2);
	}

	@SuppressWarnings("static-method")
	@Test public final void testSet()
	{
		final java.util.Properties source = new java.util.Properties();
		source.setProperty("outer1", "1009");
		source.setProperty("nestedO.inner1", "109");
		source.setProperty("nestedO.nestedI.drinner1", "19");

		final OuterProperties outer = new OuterProperties(source);
		outer.assertIt();
		assertEquals(1009, outer.outer1F.get());
		assertEquals(1002, outer.outer2F.get());
		assertEquals(109, outer.nestedInner1.get());
		assertEquals(102, outer.nestedInner2.get());
		assertEquals(19, outer.nestedDrinner1.get());
		assertEquals(12, outer.nestedDrinner2.get());

		final InnerProperties inner = outer.nested;
		inner.assertIt();
		assertEquals(109, inner.inner1);
		assertEquals(102, inner.inner2);
		assertEquals(19, inner.nestedDrinner1.get());
		assertEquals(12, inner.nestedDrinner2.get());

		final DrinnerProperties drinner = inner.nested;
		drinner.assertIt();
		assertEquals(19, drinner.drinner1);
		assertEquals(12, drinner.drinner2);
	}

	@SuppressWarnings("static-method")
	@SuppressFBWarnings("DLS_DEAD_LOCAL_STORE")
	@Test public final void testWrong()
	{
		final java.util.Properties source = new java.util.Properties();
		source.setProperty("nestedO.inner1", "109x");

		try
		{
			@SuppressWarnings("unused")
			final OuterProperties x =
				new OuterProperties(source);
			fail();
		}
		catch(final IllegalPropertiesException e)
		{
			assertEquals(
					"property nestedO.inner1 in someDescription has invalid value, expected an integer greater or equal 51, but got >109x<.",
					e.getMessage());
			assertEquals("nestedO.inner1", e.getKey());
			assertEquals("has invalid value, expected an integer greater or equal 51, but got >109x<.", e.getDetail());
			final Throwable cause = e.getCause();
			assertEquals(
					"property inner1 in someDescription (prefix nestedO.) has invalid value, expected an integer greater or equal 51, but got >109x<.",
					cause.getMessage());
			assertTrue(cause instanceof IllegalPropertiesException);
			assertEquals("inner1", ((IllegalPropertiesException)cause).getKey());
			assertEquals("has invalid value, expected an integer greater or equal 51, but got >109x<.", ((IllegalPropertiesException)cause).getDetail());
			assertTrue(cause.getCause() instanceof NumberFormatException);
		}
	}

	@SuppressWarnings("static-method")
	@SuppressFBWarnings("DLS_DEAD_LOCAL_STORE")
	@Test public final void testWrongDrinner()
	{
		final java.util.Properties source = new java.util.Properties();
		source.setProperty("nestedO.nestedI.drinner1", "19x");

		try
		{
			@SuppressWarnings("unused")
			final OuterProperties x =
				new OuterProperties(source);
			fail();
		}
		catch(final IllegalPropertiesException e)
		{
			assertEquals(
					"property nestedO.nestedI.drinner1 in someDescription has invalid value, expected an integer greater or equal 1, but got >19x<.",
					e.getMessage());
			assertEquals("nestedO.nestedI.drinner1", e.getKey());
			assertEquals("has invalid value, expected an integer greater or equal 1, but got >19x<.", e.getDetail());
			final Throwable cause = e.getCause();
			assertEquals(
					"property nestedI.drinner1 in someDescription (prefix nestedO.) has invalid value, expected an integer greater or equal 1, but got >19x<.",
					cause.getMessage());
			assertTrue(cause instanceof IllegalPropertiesException);
			assertEquals("nestedI.drinner1", ((IllegalPropertiesException)cause).getKey());
			assertEquals("has invalid value, expected an integer greater or equal 1, but got >19x<.", ((IllegalPropertiesException)cause).getDetail());
			final Throwable causeCause = cause.getCause();
			assertEquals(
					"property drinner1 in someDescription (prefix nestedO.nestedI.) has invalid value, expected an integer greater or equal 1, but got >19x<.",
					causeCause.getMessage());
			assertTrue(causeCause instanceof IllegalPropertiesException);
			assertEquals("drinner1", ((IllegalPropertiesException)causeCause).getKey());
			assertEquals("has invalid value, expected an integer greater or equal 1, but got >19x<.", ((IllegalPropertiesException)causeCause).getDetail());
			assertTrue(causeCause.getCause() instanceof NumberFormatException);
		}
	}
}
