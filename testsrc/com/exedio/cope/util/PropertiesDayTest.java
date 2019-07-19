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

import static com.exedio.cope.junit.CopeAssert.assertEqualsUnmodifiable;
import static com.exedio.cope.util.PropertiesTest.assertThrowsIllegalProperties;
import static com.exedio.cope.util.Sources.view;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class PropertiesDayTest
{
	@Test void testMinimal()
	{
		final MyProps props = new MyProps(minimal());
		props.assertIt();

		assertEquals(new Day(1000, 8, 31), props.mandatory);
		assertEquals(new Day(1000, 8, 31), props.mandatoryF.getValue());
		assertEquals(new Day(1000, 8, 31), props.mandatoryF.get());
		assertEquals(new Day(1009, 7, 13), props.optional);
		assertEquals(new Day(1009, 7, 13), props.optionalF.getValue());
		assertEquals(new Day(1009, 7, 13), props.optionalF.get());
		assertEquals("1000/8/31", props.mandatoryF.getValueString());
		assertEquals("1009/7/13", props.optionalF .getValueString());
		assertEquals(true,  props.mandatoryF.isSpecified());
		assertEquals(false, props.optionalF .isSpecified());
	}

	@Test void testSet()
	{
		final java.util.Properties p = minimal();
		p.setProperty("mandatory", "2000-11-4");
		p.setProperty("optional", "2001-3-31");
		final MyProps props = new MyProps(p);
		props.assertIt();

		assertEquals(new Day(2000, 11,  4), props.mandatory);
		assertEquals(new Day(2000, 11,  4), props.mandatoryF.getValue());
		assertEquals(new Day(2000, 11,  4), props.mandatoryF.get());
		assertEquals(new Day(2001,  3, 31), props.optional);
		assertEquals(new Day(2001,  3, 31), props.optionalF.getValue());
		assertEquals(new Day(2001,  3, 31), props.optionalF.get());
		assertEquals("2000/11/4", props.mandatoryF.getValueString());
		assertEquals("2001/3/31", props.optionalF .getValueString());
		assertEquals(true, props.mandatoryF.isSpecified());
		assertEquals(true, props.optionalF .isSpecified());
	}

	@Test void testMandatoryUnspecified()
	{
		assertWrong(
				"mandatory", null,
				"must be specified as there is no default", null);
	}

	@Test void testMandatoryWrong()
	{
		assertWrong(
				"mandatory", "no-date-set",
				"must be a day formatted as yyyy-mm-dd, but was 'no-date-set'",
				IllegalArgumentException.class);
	}

	@Test void testOptionalWrong()
	{
		assertWrong(
				"optional", "no-date-set-opt",
				"must be a day formatted as yyyy-mm-dd, but was 'no-date-set-opt'",
				IllegalArgumentException.class);
	}

	@Test void testMandatoryWrongYear()
	{
		assertWrong(
				"mandatory", "200-04-04",
				"must be a day formatted as yyyy-mm-dd, but was '200-04-04'",
				IllegalArgumentException.class);
	}

	@Test void testMandatoryWrongMonth()
	{
		assertWrong(
				"mandatory", "2000-40-04",
				"must be a day formatted as yyyy-mm-dd, but was '2000-40-04'",
				IllegalArgumentException.class);
	}

	@Test void testMandatoryWrongDay()
	{
		assertWrong(
				"mandatory", "2000-04-40",
				"must be a day formatted as yyyy-mm-dd, but was '2000-04-40'",
				IllegalArgumentException.class);
	}


	static class MyProps extends Properties
	{
		final Day mandatory = value("mandatory", (Day)null);
		final Day optional  = value("optional", new Day(1009, 7, 13));

		MyProps(final java.util.Properties source)
		{
			super(view(source, "sourceDescription"));
		}

		final Field<?> mandatoryF = getField("mandatory");
		final Field<?> optionalF  = getField("optional");


		void assertIt()
		{
			assertEqualsUnmodifiable(asList(), getProbes());
			assertEqualsUnmodifiable(asList(mandatoryF, optionalF), getFields());

			assertEquals("mandatory", mandatoryF.getKey());
			assertEquals("optional",  optionalF .getKey());

			assertEquals(null,                 mandatoryF.getDefaultValue());
			assertEquals(new Day(1009, 7, 13), optionalF .getDefaultValue());

			assertEquals(null,       mandatoryF.getDefaultValueString());
			assertEquals("1009/7/13", optionalF.getDefaultValueString());

			assertEquals(null, mandatoryF.getDefaultValueFailure());
			assertEquals(null,  optionalF.getDefaultValueFailure());

			assertEquals(false, mandatoryF.hasHiddenValue());
			assertEquals(false,  optionalF.hasHiddenValue());
		}
	}

	private static void assertWrong(
			final String key,
			final String value,
			final String message,
			final Class<? extends Throwable> cause)
	{
		final java.util.Properties wrongProps = minimal();
		if(value!=null)
			wrongProps.setProperty(key, value);
		else
			wrongProps.remove(key);

		assertThrowsIllegalProperties(
				() -> new MyProps(wrongProps),
				key, message, cause);
	}

	private static java.util.Properties minimal()
	{
		final java.util.Properties result = new java.util.Properties();
		result.setProperty("mandatory", "1000-08-31");
		return result;
	}
}
