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
import static com.exedio.cope.junit.CopeAssert.assertEqualsUnmodifiable;
import static com.exedio.cope.util.Sources.view;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class PropertiesNestedTest
{
	static class Outer extends MyProperties
	{
		final int outer1 = value("outer1", 1001, 501);
		final int outer2 = value("outer2", 1002, 502);
		final Inner nested = valnp("nestedO", Inner::new);
		final Field<?> nestedInner1   = getField("nestedO.inner1");
		final Field<?> nestedInner2   = getField("nestedO.inner2");
		final Field<?> nestedDrinner1 = getField("nestedO.nestedI.drinner1");
		final Field<?> nestedDrinner2 = getField("nestedO.nestedI.drinner2");

		Outer(final java.util.Properties source)
		{
			super(view(source, "someDescription"));
		}

		final Field<?> outer1F = getField("outer1");
		final Field<?> outer2F = getField("outer2");
		final PropertiesField<Inner> nestedF = forPrefix("nestedO", Inner.class);

		void assertIt()
		{
			assertEqualsUnmodifiable(asList(), getProbes());
			assertEqualsUnmodifiable(asList(new Properties.Field<?>[]{
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

	static class Inner extends MyProperties
	{
		final int inner1 = value("inner1", 101, 51);
		final int inner2 = value("inner2", 102, 52);
		final Drinner nested = valnp("nestedI", Drinner::new);
		final Field<?> nestedDrinner1 = getField("nestedI.drinner1");
		final Field<?> nestedDrinner2 = getField("nestedI.drinner2");

		Inner(final Source source)
		{
			super(source);
		}

		final Field<?> inner1F = getField("inner1");
		final Field<?> inner2F = getField("inner2");
		final PropertiesField<Drinner> nestedF = forPrefix("nestedI", Drinner.class);

		void assertIt()
		{
			assertEqualsUnmodifiable(asList(), getProbes());
			assertEqualsUnmodifiable(asList(new Properties.Field<?>[]{
					inner1F,
					inner2F,
					nestedDrinner1,
					nestedDrinner2,
			}), getFields());

			assertEquals("inner1", inner1F.getKey());
			assertEquals("inner2", inner2F.getKey());
			assertEquals("nestedI", nestedF.getKey());
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

	static class Drinner extends Properties
	{
		final int drinner1 = value("drinner1", 11, 1);
		final int drinner2 = value("drinner2", 12, 2);

		Drinner(final Source source)
		{
			super(source);
		}

		final Field<?> drinner1F = getField("drinner1");
		final Field<?> drinner2F = getField("drinner2");

		void assertIt()
		{
			assertEqualsUnmodifiable(asList(), getProbes());
			assertEqualsUnmodifiable(asList(new Properties.Field<?>[]{
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

	@Test void testDefaults()
	{
		final java.util.Properties source = new java.util.Properties();

		final Outer outer = new Outer(source);
		outer.assertIt();
		assertEquals(1001, outer.outer1);
		assertEquals(1002, outer.outer2);
		assertEquals(101, outer.nestedInner1.get());
		assertEquals(102, outer.nestedInner2.get());
		assertEquals(11, outer.nestedDrinner1.get());
		assertEquals(12, outer.nestedDrinner2.get());

		final Inner inner = outer.nested;
		inner.assertIt();
		assertEquals(101, inner.inner1);
		assertEquals(102, inner.inner2);
		assertEquals(11, inner.nestedDrinner1.get());
		assertEquals(12, inner.nestedDrinner2.get());

		final Drinner drinner = inner.nested;
		drinner.assertIt();
		assertEquals(11, drinner.drinner1);
		assertEquals(12, drinner.drinner2);

		assertEquals(false, outer.outer1F.isSpecified());
		assertEquals(false, outer.outer2F.isSpecified());
		assertEquals(false, outer.nestedInner1.isSpecified());
		assertEquals(false, outer.nestedInner2.isSpecified());
		assertEquals(false, outer.nestedDrinner1.isSpecified());
		assertEquals(false, outer.nestedDrinner2.isSpecified());
	}

	@Test void testSet()
	{
		final java.util.Properties source = new java.util.Properties();
		source.setProperty("outer1", "1009");
		source.setProperty("nestedO.inner1", "109");
		source.setProperty("nestedO.nestedI.drinner1", "19");

		final Outer outer = new Outer(source);
		outer.assertIt();
		assertEquals(1009, outer.outer1F.get());
		assertEquals(1002, outer.outer2F.get());
		assertEquals(109, outer.nestedInner1.get());
		assertEquals(102, outer.nestedInner2.get());
		assertEquals(19, outer.nestedDrinner1.get());
		assertEquals(12, outer.nestedDrinner2.get());

		final Inner inner = outer.nested;
		inner.assertIt();
		assertEquals(109, inner.inner1);
		assertEquals(102, inner.inner2);
		assertEquals(19, inner.nestedDrinner1.get());
		assertEquals(12, inner.nestedDrinner2.get());

		final Drinner drinner = inner.nested;
		drinner.assertIt();
		assertEquals(19, drinner.drinner1);
		assertEquals(12, drinner.drinner2);

		assertEquals(true,  outer.outer1F.isSpecified());
		assertEquals(false, outer.outer2F.isSpecified());
		assertEquals(true,  outer.nestedInner1.isSpecified());
		assertEquals(false, outer.nestedInner2.isSpecified());
		assertEquals(true,  outer.nestedDrinner1.isSpecified());
		assertEquals(false, outer.nestedDrinner2.isSpecified());
	}

	@Test void testWrong()
	{
		final java.util.Properties source = new java.util.Properties();
		source.setProperty("nestedO.inner1", "109x");

		final String detail = "must be an integer greater or equal 51, but was '109x'";
		final IllegalPropertiesException e = assertFails(() ->
			new Outer(source),
			IllegalPropertiesException.class,
			"property nestedO.inner1 in someDescription " + detail,
			IllegalPropertiesException.class);
		assertEquals("nestedO.inner1", e.getKey());
		assertEquals(detail, e.getDetail());
		final IllegalPropertiesException cause = (IllegalPropertiesException)e.getCause();
		assertEquals(
				"property inner1 in someDescription (prefix nestedO.) " + detail,
				cause.getMessage());
		assertEquals("inner1", cause.getKey());
		assertEquals(detail, cause.getDetail());
		assertTrue(cause.getCause() instanceof NumberFormatException);
	}

	@Test void testWrongDrinner()
	{
		final java.util.Properties source = new java.util.Properties();
		source.setProperty("nestedO.nestedI.drinner1", "19x");

		final String detail = "must be an integer greater or equal 1, but was '19x'";
		final IllegalPropertiesException e = assertFails(() ->
			new Outer(source),
			IllegalPropertiesException.class,
			"property nestedO.nestedI.drinner1 in someDescription " + detail,
			IllegalPropertiesException.class);
		assertEquals("nestedO.nestedI.drinner1", e.getKey());
		assertEquals(detail, e.getDetail());
		final IllegalPropertiesException cause = (IllegalPropertiesException)e.getCause();
		assertEquals(
				"property nestedI.drinner1 in someDescription (prefix nestedO.) " + detail,
				cause.getMessage());
		assertEquals("nestedI.drinner1", cause.getKey());
		assertEquals(detail, cause.getDetail());
		final IllegalPropertiesException causeCause = (IllegalPropertiesException)cause.getCause();
		assertEquals(
				"property drinner1 in someDescription (prefix nestedO.nestedI.) " + detail,
				causeCause.getMessage());
		assertEquals("drinner1", causeCause.getKey());
		assertEquals(detail, causeCause.getDetail());
		assertTrue(causeCause.getCause() instanceof NumberFormatException);
	}
}
