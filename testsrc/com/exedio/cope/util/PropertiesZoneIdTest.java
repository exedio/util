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
import static com.exedio.cope.util.Sources.view;
import static java.time.ZoneId.of;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.ZoneId;
import org.junit.Test;

public class PropertiesZoneIdTest
{
	@Test public void testMinimal()
	{
		final MyProps props = new MyProps(minimal());
		props.assertIt();

		assertEquals(of("Europe/Berlin"), props.mandatory);
		assertEquals(of("Europe/Moscow"), props.optional);
		assertEquals("Europe/Berlin", props.mandatoryF.getValue());
		assertEquals("Europe/Moscow", props.optionalF .getValue());
		assertTrue (props.mandatoryF.isSpecified());
		assertFalse(props.optionalF .isSpecified());
	}

	@Test public void testSet()
	{
		final java.util.Properties p = minimal();
		p.setProperty("mandatory", "Canada/Eastern");
		p.setProperty("optional",  "Canada/Atlantic");
		final MyProps props = new MyProps(p);
		props.assertIt();

		assertEquals(of("Canada/Eastern"),  props.mandatory);
		assertEquals(of("Canada/Atlantic"), props.optional);
		assertEquals("Canada/Eastern",  props.mandatoryF.getValue());
		assertEquals("Canada/Atlantic", props.optionalF .getValue());
		assertTrue (props.mandatoryF.isSpecified());
		assertTrue (props.optionalF .isSpecified());
	}

	@Test public void testMandatoryWrong()
	{
		assertWrong(
				"mandatory", "WRONG",
				"must be one of ZoneId.getAvailableZoneIds(), but was 'WRONG'");
	}

	@Test public void testMandatoryUnspecified()
	{
		assertWrong(
				"mandatory", null,
				"must be specified as there is no default");
	}

	@Test public void testOptionalWrong()
	{
		assertWrong(
				"optional", "WRONG",
				"must be one of ZoneId.getAvailableZoneIds(), but was 'WRONG'");
	}


	static class MyProps extends MyProperties
	{
		final ZoneId mandatory = value("mandatory", (ZoneId)null);
		final ZoneId optional  = value("optional" , ZoneId.of("Europe/Moscow"));

		MyProps(final java.util.Properties source)
		{
			super(view(source, "sourceDescription"));
		}

		final StringField mandatoryF = (StringField)forKey("mandatory");
		final StringField optionalF  = (StringField)forKey("optional");


		void assertIt()
		{
			assertEqualsUnmodifiable(asList(), getTests());
			assertEqualsUnmodifiable(asList(mandatoryF, optionalF), getFields());

			assertEquals("mandatory", mandatoryF.getKey());
			assertEquals("optional",  optionalF .getKey());

			assertEquals(null,            mandatoryF.getDefaultValue());
			assertEquals("Europe/Moscow", optionalF .getDefaultValue());

			assertFalse(mandatoryF.hasHiddenValue());
			assertFalse(optionalF .hasHiddenValue());
		}
	}

	@SuppressWarnings("unused")
	private static void assertWrong(
			final String key,
			final String value,
			final String message)
	{
		final java.util.Properties wrongProps = minimal();
		if(value!=null)
			wrongProps.setProperty(key, value);
		else
			wrongProps.remove(key);

		try
		{
			new MyProps(wrongProps);
			fail();
		}
		catch(final IllegalPropertiesException e)
		{
			assertEquals(key, e.getKey());
			assertEquals(message, e.getDetail());
		}
	}

	private static java.util.Properties minimal()
	{
		final java.util.Properties result = new java.util.Properties();
		result.setProperty("mandatory", "Europe/Berlin");
		return result;
	}
}
