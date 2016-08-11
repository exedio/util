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
import static com.exedio.cope.junit.CopeAssert.list;
import static com.exedio.cope.util.PropertiesEnumTest.AnEnum.BETA;
import static com.exedio.cope.util.PropertiesEnumTest.AnEnum.GAMMA;
import static com.exedio.cope.util.PropertiesEnumTest.AnEnum.MANDATORY;
import static com.exedio.cope.util.PropertiesEnumTest.AnEnum.OPTIONAL;
import static com.exedio.cope.util.Sources.view;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class PropertiesEnumTest
{
	@Test public void testMinimal()
	{
		final SomeProps props = new SomeProps(minimal());
		props.assertIt();

		assertEquals(MANDATORY, props.mandatory);
		assertEquals(OPTIONAL,  props.optional);
		assertEquals("MANDATORY", props.mandatoryF.getValue());
		assertEquals("OPTIONAL",  props.optionalF .getValue());
		assertTrue (props.mandatoryF.isSpecified());
		assertFalse(props.optionalF .isSpecified());
	}

	@Test public void testSet()
	{
		final java.util.Properties p = minimal();
		p.setProperty("mandatory", "BETA");
		p.setProperty("optional", "GAMMA");
		final SomeProps props = new SomeProps(p);
		props.assertIt();

		assertEquals(BETA,  props.mandatory);
		assertEquals(GAMMA, props.optional);
		assertEquals("BETA",  props.mandatoryF.getValue());
		assertEquals("GAMMA", props.optionalF .getValue());
		assertTrue (props.mandatoryF.isSpecified());
		assertTrue (props.optionalF .isSpecified());
	}

	@Test public void testMandatoryWrong()
	{
		assertWrong(
				"mandatory", "WRONG",
				"must be one of [MANDATORY, OPTIONAL, ALPHA, BETA, GAMMA], but was 'WRONG'");
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
				"must be one of [MANDATORY, OPTIONAL, ALPHA, BETA, GAMMA], but was 'WRONG'");
	}


	enum AnEnum { MANDATORY, OPTIONAL, ALPHA, BETA, GAMMA }

	static class SomeProps extends MyProperties
	{
		final AnEnum mandatory = value("mandatory", AnEnum.class);
		final AnEnum optional  = value("optional" , AnEnum.OPTIONAL);

		SomeProps(final java.util.Properties source)
		{
			super(view(source, "sourceDescription"));
		}

		final StringField mandatoryF = (StringField)forKey("mandatory");
		final StringField optionalF  = (StringField)forKey("optional");


		void assertIt()
		{
			assertEquals(list(), getTests());
			assertEqualsUnmodifiable(asList(mandatoryF, optionalF), getFields());

			assertEquals("mandatory", mandatoryF.getKey());
			assertEquals("optional",  optionalF .getKey());

			assertEquals(null,       mandatoryF.getDefaultValue());
			assertEquals("OPTIONAL", optionalF .getDefaultValue());

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
			new SomeProps(wrongProps);
			fail();
		}
		catch(final IllegalPropertiesException e)
		{
			assertEquals(key, e.getKey());
			assertEquals(message, e.getDetail());
		}
	}

	private static final java.util.Properties minimal()
	{
		final java.util.Properties result = new java.util.Properties();
		result.setProperty("mandatory", "MANDATORY");
		return result;
	}
}