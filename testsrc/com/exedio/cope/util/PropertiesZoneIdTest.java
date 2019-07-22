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
import static com.exedio.cope.util.PropertiesTest.assertThrowsIllegalProperties;
import static com.exedio.cope.util.Sources.view;
import static java.time.ZoneId.of;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZoneId;
import java.time.zone.ZoneRulesException;
import org.junit.jupiter.api.Test;

public class PropertiesZoneIdTest
{
	@Test void testMinimal()
	{
		final MyProps props = new MyProps(minimal());
		props.assertIt();

		assertEquals(of("Europe/Berlin"), props.mandatory);
		assertEquals(of("Europe/Moscow"), props.optional);
		assertEquals(of("Europe/Berlin"), props.mandatoryF.getValue());
		assertEquals(of("Europe/Moscow"), props.optionalF .getValue());
		assertEquals(   "Europe/Berlin",  props.mandatoryF.getValueString());
		assertEquals(   "Europe/Moscow",  props.optionalF .getValueString());
		assertTrue (props.mandatoryF.isSpecified());
		assertFalse(props.optionalF .isSpecified());
	}

	@Test void testSet()
	{
		final java.util.Properties p = minimal();
		p.setProperty("mandatory", "Canada/Eastern");
		p.setProperty("optional",  "Canada/Atlantic");
		final MyProps props = new MyProps(p);
		props.assertIt();

		assertEquals(of("Canada/Eastern"),  props.mandatory);
		assertEquals(of("Canada/Atlantic"), props.optional);
		assertEquals(of("Canada/Eastern"),  props.mandatoryF.getValue());
		assertEquals(of("Canada/Atlantic"), props.optionalF .getValue());
		assertEquals(   "Canada/Eastern",   props.mandatoryF.getValueString());
		assertEquals(   "Canada/Atlantic",  props.optionalF .getValueString());
		assertTrue (props.mandatoryF.isSpecified());
		assertTrue (props.optionalF .isSpecified());
	}

	@Test void testGetString()
	{
		final MyProps props = new MyProps(minimal());
		props.assertIt();

		assertEquals("Canada/Eastern", props.mandatoryF.getString(of("Canada/Eastern")));
		assertEquals(null,             props.mandatoryF.getString(null));
		assertFails(
				() -> props.mandatoryF.getString(55),
				ClassCastException.class,
				"Cannot cast java.lang.Integer to java.time.ZoneId");
	}

	@Test void testMandatoryWrong()
	{
		assertWrong(
				"mandatory", "WRONG",
				"must be one of ZoneId.getAvailableZoneIds(), but was 'WRONG'",
				ZoneRulesException.class);
	}

	@Test void testMandatoryUnspecified()
	{
		assertWrong(
				"mandatory", null,
				"must be specified as there is no default", null);
	}

	@Test void testOptionalWrong()
	{
		assertWrong(
				"optional", "WRONG",
				"must be one of ZoneId.getAvailableZoneIds(), but was 'WRONG'",
				ZoneRulesException.class);
	}


	static class MyProps extends Properties
	{
		final ZoneId mandatory = value("mandatory", (ZoneId)null);
		final ZoneId optional  = value("optional" , of("Europe/Moscow"));

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

			assertEquals(null,            mandatoryF.getDefaultValue());
			assertEquals(of("Europe/Moscow"), optionalF.getDefaultValue());

			assertEquals(null,            mandatoryF.getDefaultValueString());
			assertEquals("Europe/Moscow", optionalF .getDefaultValueString());

			assertEquals(null, mandatoryF.getDefaultValueFailure());
			assertEquals(null, optionalF .getDefaultValueFailure());

			assertFalse(mandatoryF.hasHiddenValue());
			assertFalse(optionalF .hasHiddenValue());
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
		result.setProperty("mandatory", "Europe/Berlin");
		return result;
	}
}
