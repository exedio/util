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
import static com.exedio.cope.util.PropertiesEnumTest.MyEnum.BETA;
import static com.exedio.cope.util.PropertiesEnumTest.MyEnum.GAMMA;
import static com.exedio.cope.util.PropertiesEnumTest.MyEnum.MANDATORY;
import static com.exedio.cope.util.PropertiesEnumTest.MyEnum.OPTIONAL;
import static com.exedio.cope.util.PropertiesTest.assertThrowsIllegalProperties;
import static com.exedio.cope.util.Sources.view;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class PropertiesEnumTest
{
	@Test void testMinimal()
	{
		final MyProps props = new MyProps(minimal());
		props.assertIt();

		assertEquals(MANDATORY, props.mandatory);
		assertEquals(OPTIONAL,  props.optional);
		assertEquals(MANDATORY, props.mandatoryF.getValue());
		assertEquals(OPTIONAL,  props.optionalF .getValue());
		assertEquals("MANDATORY", props.mandatoryF.getValueString());
		assertEquals("OPTIONAL",  props.optionalF .getValueString());
		assertTrue (props.mandatoryF.isSpecified());
		assertFalse(props.optionalF .isSpecified());
	}

	@Test void testSet()
	{
		final java.util.Properties p = minimal();
		p.setProperty("mandatory", "BETA");
		p.setProperty("optional", "GAMMA");
		final MyProps props = new MyProps(p);
		props.assertIt();

		assertEquals(BETA,  props.mandatory);
		assertEquals(GAMMA, props.optional);
		assertEquals(BETA,  props.mandatoryF.getValue());
		assertEquals(GAMMA, props.optionalF .getValue());
		assertEquals("BETA",  props.mandatoryF.getValueString());
		assertEquals("GAMMA", props.optionalF .getValueString());
		assertTrue (props.mandatoryF.isSpecified());
		assertTrue (props.optionalF .isSpecified());
	}

	@Test void testGetString()
	{
		final MyProps props = new MyProps(minimal());
		props.assertIt();

		assertEquals(BETA.name(), props.mandatoryF.getString(BETA));
		assertEquals(null,        props.mandatoryF.getString(null));
		assertFails(
				() -> props.mandatoryF.getString(55),
				ClassCastException.class,
				"Cannot cast java.lang.Integer to " + MyEnum.class.getName());
		assertFails(
				() -> props.mandatoryF.getString(OtherEnum.ALPHA),
				ClassCastException.class,
				"Cannot cast " + OtherEnum.class.getName() + " to " + MyEnum.class.getName());
	}
	enum OtherEnum { ALPHA }

	@Test void testMandatoryWrong()
	{
		assertWrong(
				"mandatory", "WRONG",
				"must be one of [MANDATORY, OPTIONAL, ALPHA, BETA, GAMMA], but was 'WRONG'");
	}

	@Test void testMandatoryUnspecified()
	{
		assertWrong(
				"mandatory", null,
				"must be specified as there is no default");
	}

	@Test void testOptionalWrong()
	{
		assertWrong(
				"optional", "WRONG",
				"must be one of [MANDATORY, OPTIONAL, ALPHA, BETA, GAMMA], but was 'WRONG'");
	}


	enum MyEnum { MANDATORY, OPTIONAL, @SuppressWarnings("unused") ALPHA, BETA, GAMMA }

	static class MyProps extends Properties
	{
		final MyEnum mandatory = value("mandatory", MyEnum.class);
		final MyEnum optional  = value("optional" , OPTIONAL);

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

			assertEquals(null,       mandatoryF.getDefaultValue());
			assertEquals(OPTIONAL,   optionalF .getDefaultValue());

			assertEquals(null,       mandatoryF.getDefaultValueString());
			assertEquals("OPTIONAL", optionalF .getDefaultValueString());

			assertEquals(null, mandatoryF.getDefaultValueFailure());
			assertEquals(null, optionalF .getDefaultValueFailure());

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

		assertThrowsIllegalProperties(
				() -> new MyProps(wrongProps),
				key, message, null);
	}

	private static java.util.Properties minimal()
	{
		final java.util.Properties result = new java.util.Properties();
		result.setProperty("mandatory", "MANDATORY");
		return result;
	}
}
