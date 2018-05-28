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
import static java.time.Duration.ofHours;
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofNanos;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import org.junit.jupiter.api.Test;

public class PropertiesDurationTest
{
	@Test void testMinimal()
	{
		final MyProps props = new MyProps(minimal());
		props.assertIt();

		assertEquals(ofMinutes(55), props.mandatory);
		assertEquals(ofMinutes(44), props.optional);
		assertEquals(ofMinutes(45), props.max);
		assertEquals(ofMinutes(55), props.mandatoryF.getValue());
		assertEquals(ofMinutes(44), props.optionalF .getValue());
		assertEquals(ofMinutes(45), props.maxF      .getValue());
		assertTrue (props.mandatoryF.isSpecified());
		assertFalse(props.optionalF .isSpecified());
		assertFalse(props.maxF      .isSpecified());
	}

	@Test void testSet()
	{
		final java.util.Properties p = minimal();
		p.setProperty("mandatory", "PT3H33M");
		p.setProperty("optional",  "PT4H44M");
		p.setProperty("max",         "PT48M");
		final MyProps props = new MyProps(p);
		props.assertIt();

		assertEquals(ofHours(3).plus(ofMinutes(33)), props.mandatory);
		assertEquals(ofHours(4).plus(ofMinutes(44)), props.optional);
		assertEquals(ofMinutes(48), props.max);
		assertEquals(ofHours(3).plus(ofMinutes(33)), props.mandatoryF.getValue());
		assertEquals(ofHours(4).plus(ofMinutes(44)), props.optionalF .getValue());
		assertEquals(ofMinutes(48),  props.maxF.getValue());
		assertTrue (props.mandatoryF.isSpecified());
		assertTrue (props.optionalF .isSpecified());
		assertTrue (props.maxF      .isSpecified());
	}

	@Test void testSetMillis()
	{
		final java.util.Properties p = minimal();
		p.setProperty("mandatory", String.valueOf(((3*60)+33)*60_000));
		p.setProperty("optional",  String.valueOf(((4*60)+44)*60_000));
		p.setProperty("max",       String.valueOf(        48 *60_000));
		final MyProps props = new MyProps(p);
		props.assertIt();

		assertEquals(ofHours(3).plus(ofMinutes(33)), props.mandatory);
		assertEquals(ofHours(4).plus(ofMinutes(44)), props.optional);
		assertEquals(ofMinutes(48), props.max);
		assertEquals(ofHours(3).plus(ofMinutes(33)), props.mandatoryF.getValue());
		assertEquals(ofHours(4).plus(ofMinutes(44)), props.optionalF .getValue());
		assertEquals(ofMinutes(48), props.maxF.getValue());
		assertTrue (props.mandatoryF.isSpecified());
		assertTrue (props.optionalF .isSpecified());
		assertTrue (props.maxF      .isSpecified());
	}

	@Test void testSetMinimum()
	{
		final java.util.Properties p = minimal();
		p.setProperty("mandatory", "PT21M");
		p.setProperty("optional",  "PT41M");
		p.setProperty("max",       "PT42M");
		final MyProps props = new MyProps(p);
		props.assertIt();

		assertEquals(ofMinutes(21), props.mandatory);
		assertEquals(ofMinutes(41), props.optional);
		assertEquals(ofMinutes(42), props.max);
		assertEquals(ofMinutes(21), props.mandatoryF.getValue());
		assertEquals(ofMinutes(41), props.optionalF .getValue());
		assertEquals(ofMinutes(42), props.maxF      .getValue());
		assertTrue (props.mandatoryF.isSpecified());
		assertTrue (props.optionalF .isSpecified());
		assertTrue (props.maxF      .isSpecified());
	}

	@Test void testSetMinimumMillis()
	{
		final java.util.Properties p = minimal();
		p.setProperty("mandatory", String.valueOf(21*60_000));
		p.setProperty("optional",  String.valueOf(41*60_000));
		p.setProperty("max",       String.valueOf(42*60_000));
		final MyProps props = new MyProps(p);
		props.assertIt();

		assertEquals(ofMinutes(21), props.mandatory);
		assertEquals(ofMinutes(41), props.optional);
		assertEquals(ofMinutes(42), props.max);
		assertEquals(ofMinutes(21), props.mandatoryF.getValue());
		assertEquals(ofMinutes(41), props.optionalF .getValue());
		assertEquals(ofMinutes(42), props.maxF      .getValue());
		assertTrue (props.mandatoryF.isSpecified());
		assertTrue (props.optionalF .isSpecified());
		assertTrue (props.maxF      .isSpecified());
	}

	@Test void testMandatoryWrong()
	{
		assertWrong(
				"mandatory", "WRONG",
				"must be a duration, but was 'WRONG'",
				DateTimeParseException.class);
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
				"must be a duration, but was 'WRONG'",
				DateTimeParseException.class);
	}

	@Test void testOptionalMinimum()
	{
		assertWrong(
				"optional", "PT40M59.999999999S",
				"must be a duration greater or equal PT41M, but was " + ofMinutes(41).minus(ofNanos(1)), null);
	}

	@Test void testOptionalMinimumMillis()
	{
		assertWrong(
				"optional", String.valueOf(41*60_000 - 1),
				"must be a duration greater or equal PT41M, but was " + ofMinutes(41).minus(ofMillis(1)), null);
	}

	@Test void testMaxMinimum()
	{
		assertWrong(
				"max", "PT41M59.999999999S",
				"must be a duration between PT42M and PT48M, but was PT41M59.999999999S", null);
	}

	@Test void testMaxMinimumMillis()
	{
		assertWrong(
				"max", String.valueOf(42*60_000 - 1),
				"must be a duration between PT42M and PT48M, but was PT41M59.999S", null);
	}

	@Test void testMaxMaximum()
	{
		assertWrong(
				"max", "PT48M0.000000001S",
				"must be a duration between PT42M and PT48M, but was PT48M0.000000001S", null);
	}

	@Test void testMaxMaximumMillis()
	{
		assertWrong(
				"max", String.valueOf(48*60_000 + 1),
				"must be a duration between PT42M and PT48M, but was PT48M0.001S", null);
	}


	static class MyProps extends MyProperties
	{
		final Duration mandatory = value("mandatory", null, ofMinutes(21));
		final Duration optional  = value("optional" , ofMinutes(44), ofMinutes(41));
		final Duration max       = value("max"      , ofMinutes(45), ofMinutes(42), ofMinutes(48));

		MyProps(final java.util.Properties source)
		{
			super(view(source, "sourceDescription"));
		}

		final Field<?> mandatoryF = getField("mandatory");
		final Field<?> optionalF  = getField("optional");
		final Field<?> maxF       = getField("max");


		void assertIt()
		{
			assertEqualsUnmodifiable(asList(), getProbes());
			assertEqualsUnmodifiable(asList(mandatoryF, optionalF, maxF), getFields());

			assertEquals("mandatory", mandatoryF.getKey());
			assertEquals("optional",  optionalF .getKey());
			assertEquals("max",       maxF      .getKey());

			assertEquals(null,    mandatoryF.getDefaultValue());
			assertEquals(ofMinutes(44), optionalF.getDefaultValue());
			assertEquals(ofMinutes(45), maxF     .getDefaultValue());

			assertFalse(mandatoryF.hasHiddenValue());
			assertFalse(optionalF .hasHiddenValue());
			assertFalse(maxF      .hasHiddenValue());
		}
	}

	@SuppressWarnings("unused")
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
		result.setProperty("mandatory", "PT55M");
		return result;
	}


	@SuppressWarnings("unused")
	@Test void testMinimumNull()
	{
		assertFails(
			PropsMinimumNull::new,
			NullPointerException.class, "minimum");
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


	@Test void testMinimumNullWithMaximum()
	{
		assertFails(
				PropsMinimumNullWithMaximum::new,
				NullPointerException.class, "minimum");
	}
	static class PropsMinimumNullWithMaximum extends MyProperties
	{
		@SuppressFBWarnings("NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS")
		PropsMinimumNullWithMaximum()
		{
			super(Sources.EMPTY);
			value("myKey", null, null, null);
		}
	}


	@Test void testMaximumNull()
	{
		assertFails(
				PropsMaximumNull::new,
				NullPointerException.class, "maximum");
	}
	static class PropsMaximumNull extends MyProperties
	{
		@SuppressFBWarnings("NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS")
		PropsMaximumNull()
		{
			super(Sources.EMPTY);
			value("myKey", null, ofNanos(1), null);
		}
	}


	@SuppressWarnings("unused")
	@Test void testDefaultViolatesMinimum()
	{
		assertFails(
			PropsDefaultViolatesMinimum::new,
			IllegalArgumentException.class,
			"default of myKey must not be smaller than minimum of PT6M, but was PT5M59.999999999S");
	}
	static class PropsDefaultViolatesMinimum extends MyProperties
	{
		PropsDefaultViolatesMinimum()
		{
			super(Sources.EMPTY);
			value("myKey", ofMinutes(6).minus(ofNanos(1)), ofMinutes(6));
		}
	}


	@Test void testDefaultViolatesMaximum()
	{
		assertFails(
				PropsDefaultViolatesMaximum::new,
				IllegalArgumentException.class,
				"default of myKey must not be greater than maximum of PT7M, but was PT7M0.000000001S");
	}
	static class PropsDefaultViolatesMaximum extends MyProperties
	{
		PropsDefaultViolatesMaximum()
		{
			super(Sources.EMPTY);
			value("myKey", ofMinutes(7).plus(ofNanos(1)), ofMinutes(6), ofMinutes(7));
		}
	}


	@Test void testDefaultEqualsMinimum()
	{
		final PropsDefaultEqualsMinimum p = new PropsDefaultEqualsMinimum();
		assertEquals(ofMinutes(5), p.d);
	}
	static class PropsDefaultEqualsMinimum extends MyProperties
	{
		final Duration d = value("myKey", ofMinutes(5), ofMinutes(5));

		PropsDefaultEqualsMinimum() { super(Sources.EMPTY); }
	}


	@Test void testDefaultEqualsMaximum()
	{
		final PropsDefaultEqualsMaximum p = new PropsDefaultEqualsMaximum();
		assertEquals(ofMinutes(5), p.d);
	}
	static class PropsDefaultEqualsMaximum extends MyProperties
	{
		final Duration d = value("myKey", ofMinutes(5), ofMinutes(5), ofMinutes(5));

		PropsDefaultEqualsMaximum() { super(Sources.EMPTY); }
	}
}
