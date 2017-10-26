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
import static java.time.Duration.ofHours;
import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.Assert.fail;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.Duration;
import org.junit.Test;

public class PropertiesDurationTest
{
	@Test public void testMinimal()
	{
		final MyProps props = new MyProps(minimal());
		props.assertIt();

		assertEquals(ofMinutes(55), props.mandatory);
		assertEquals(ofMinutes(44), props.optional);
		assertEquals("PT55M", props.mandatoryF.getValue());
		assertEquals("PT44M", props.optionalF .getValue());
		assertTrue (props.mandatoryF.isSpecified());
		assertFalse(props.optionalF .isSpecified());
	}

	@Test public void testSet()
	{
		final java.util.Properties p = minimal();
		p.setProperty("mandatory", "PT3H33M");
		p.setProperty("optional",  "PT4H44M");
		final MyProps props = new MyProps(p);
		props.assertIt();

		assertEquals(ofHours(3).plus(ofMinutes(33)), props.mandatory);
		assertEquals(ofHours(4).plus(ofMinutes(44)), props.optional);
		assertEquals("PT3H33M", props.mandatoryF.getValue());
		assertEquals("PT4H44M", props.optionalF .getValue());
		assertTrue (props.mandatoryF.isSpecified());
		assertTrue (props.optionalF .isSpecified());
	}

	@Test public void testSetMinimum()
	{
		final java.util.Properties p = minimal();
		p.setProperty("mandatory", "PT21M");
		p.setProperty("optional",  "PT41M");
		final MyProps props = new MyProps(p);
		props.assertIt();

		assertEquals(ofMinutes(21), props.mandatory);
		assertEquals(ofMinutes(41), props.optional);
		assertEquals("PT21M", props.mandatoryF.getValue());
		assertEquals("PT41M", props.optionalF .getValue());
		assertTrue (props.mandatoryF.isSpecified());
		assertTrue (props.optionalF .isSpecified());
	}

	@Test public void testMandatoryWrong()
	{
		assertWrong(
				"mandatory", "WRONG",
				"must be a duration, but was 'WRONG'");
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
				"must be a duration, but was 'WRONG'");
	}

	@Test public void testOptionalMinimum()
	{
		assertWrong(
				"optional", "PT40M59.999999999S",
				"must be a duration greater or equal PT41M, but was PT40M59.999999999S");
	}


	static class MyProps extends MyProperties
	{
		final Duration mandatory = value("mandatory", null, ofMinutes(21));
		final Duration optional  = value("optional" , ofMinutes(44), ofMinutes(41));

		MyProps(final java.util.Properties source)
		{
			super(view(source, "sourceDescription"));
		}

		final StringField mandatoryF = (StringField)forKey("mandatory");
		final StringField optionalF  = (StringField)forKey("optional");


		void assertIt()
		{
			assertEqualsUnmodifiable(asList(), getProbes());
			assertEqualsUnmodifiable(asList(mandatoryF, optionalF), getFields());

			assertEquals("mandatory", mandatoryF.getKey());
			assertEquals("optional",  optionalF .getKey());

			assertEquals(null,    mandatoryF.getDefaultValue());
			assertEquals("PT44M", optionalF .getDefaultValue());

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
		result.setProperty("mandatory", "PT55M");
		return result;
	}


	@SuppressWarnings("unused")
	@Test public void testMinimumNull()
	{
		try
		{
			new PropsMinimumNull();
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("minimum", e.getMessage());
		}
	}
	static class PropsMinimumNull extends MyProperties
	{
		@SuppressFBWarnings("NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS")
		PropsMinimumNull()
		{
			super(Sources.EMPTY);
			value("myKey", null, null);
		}
	}


	@SuppressWarnings("unused")
	@Test public void testDefaultViolatesMinimum()
	{
		try
		{
			new PropsDefaultViolatesMinimum();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"default of myKey must not be smaller than minimum of PT6M, but was PT5M59S",
					e.getMessage());
		}
	}
	static class PropsDefaultViolatesMinimum extends MyProperties
	{
		PropsDefaultViolatesMinimum()
		{
			super(Sources.EMPTY);
			value("myKey", ofMinutes(5).plus(ofSeconds(59)), ofMinutes(6));
		}
	}


	@Test public void testDefaultEqualsMinimum()
	{
		final PropsDefaultEqualsMinimum p = new PropsDefaultEqualsMinimum();
		assertEquals(ofMinutes(5), p.d);
	}
	static class PropsDefaultEqualsMinimum extends MyProperties
	{
		final Duration d = value("myKey", ofMinutes(5), ofMinutes(5));

		PropsDefaultEqualsMinimum() { super(Sources.EMPTY); }
	}
}
