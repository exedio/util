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
import static com.exedio.cope.junit.CopeAssert.assertContainsUnmodifiable;
import static com.exedio.cope.junit.CopeAssert.assertEqualsUnmodifiable;
import static com.exedio.cope.util.Sources.view;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

@SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON") // is more compact to write in tests
public class PropertiesTest
{
	static class TestProperties extends MyProperties
	{
		final boolean boolFalse = value("boolFalse", false);
		final boolean boolTrue = value("boolTrue", true);
		final int intAny = value("intAny", 10, Integer.MIN_VALUE);
		final int int10 = value("int10", 10, 5);
		final String stringMandatory = value("stringMandatory", (String)null);
		final String stringOptional = value("stringOptional", "stringOptional.defaultValue");
		final String stringHidden = valueHidden("stringHidden", (String)null);
		final String stringHiddenOptional = valueHidden("stringHiddenOptional", "stringHiddenOptional.defaultValue");
		final Day dayMandatory = value("dayMandatory", (Day) null);
		final Day dayOptional = value("dayOptional", new Day(1009,7,13));

		TestProperties(final java.util.Properties source, final String sourceDescription)
		{
			super(view(source, sourceDescription));
		}

		final Field<?> boolFalseF = getField("boolFalse");
		final Field<?> boolTrueF  = getField("boolTrue");
		final Field<?> intAnyF = getField("intAny");
		final Field<?> int10F  = getField("int10");
		final Field<?> stringMandatoryF      = getField("stringMandatory");
		final Field<?> stringOptionalF       = getField("stringOptional");
		final Field<?> stringHiddenF         = getField("stringHidden");
		final Field<?> stringHiddenOptionalF = getField("stringHiddenOptional");
		final Field<?> dayMandatoryF = getField("dayMandatory");
		final Field<?> dayOptionalF  = getField("dayOptional");


		void assertIt()
		{
			assertEqualsUnmodifiable(asList(), getProbes());
			assertEqualsUnmodifiable(asList(new Properties.Field<?>[]{
					boolFalseF,
					boolTrueF,
					intAnyF,
					int10F,
					stringMandatoryF,
					stringOptionalF,
					stringHiddenF,
					stringHiddenOptionalF,
					dayMandatoryF,
					dayOptionalF,
			}), getFields());

			assertEquals("boolFalse", boolFalseF.getKey());
			assertEquals("boolTrue", boolTrueF.getKey());
			assertEquals("int10", int10F.getKey());
			assertEquals("stringMandatory", stringMandatoryF.getKey());
			assertEquals("stringOptional", stringOptionalF.getKey());
			assertEquals("stringHidden", stringHiddenF.getKey());
			assertEquals("stringHiddenOptional", stringHiddenOptionalF.getKey());
			assertEquals("dayMandatory", dayMandatoryF.getKey());
			assertEquals("dayOptional", dayOptionalF.getKey());

			assertEquals(Boolean.FALSE, boolFalseF.getDefaultValue());
			assertEquals(Boolean.TRUE, boolTrueF.getDefaultValue());
			assertEquals(Integer.valueOf(10), int10F.getDefaultValue());
			assertEquals(5, int10F.getMinimum());
			assertEquals(null, stringMandatoryF.getDefaultValue());
			assertEquals("stringOptional.defaultValue", stringOptionalF.getDefaultValue());
			assertEquals(null, stringHiddenF.getDefaultValue());
			assertEquals("stringHiddenOptional.defaultValue", stringHiddenOptionalF.getDefaultValue());
			assertEquals(null, dayMandatoryF.getDefaultValue());
			assertEquals(new Day(1009,7,13), dayOptionalF.getDefaultValue());

			assertEquals(false, boolFalseF.hasHiddenValue());
			assertEquals(false, boolTrueF.hasHiddenValue());
			assertEquals(false, int10F.hasHiddenValue());
			assertEquals(false, stringMandatoryF.hasHiddenValue());
			assertEquals(false, stringOptionalF.hasHiddenValue());
			assertEquals(true, stringHiddenF.hasHiddenValue());
			assertEquals(true, stringHiddenOptionalF.hasHiddenValue());
			assertEquals(false, dayMandatoryF.hasHiddenValue());
			assertEquals(false, dayOptionalF.hasHiddenValue());
		}
	}

	@Test void testIt()
	{
		final java.util.Properties pminimal = new java.util.Properties();
		pminimal.setProperty("stringMandatory", "stringMandatory.minimalValue");
		pminimal.setProperty("stringHidden", "stringHidden.minimalValue");
		pminimal.setProperty("dayMandatory", "1000-08-31");

		final TestProperties minimal = new TestProperties(pminimal, "minimal");
		minimal.assertIt();
		assertEquals("minimal", minimal.getSource());

		assertEquals(false, minimal.boolFalse);
		assertEquals(true, minimal.boolTrue);
		assertEquals(Boolean.FALSE, minimal.boolFalseF.getValue());
		assertEquals(Boolean.TRUE, minimal.boolTrueF.getValue());
		assertEquals(10, minimal.intAny);
		assertEquals(10, minimal.int10);
		assertEquals(Integer.valueOf(10), minimal.int10F.getValue());
		assertEquals("stringMandatory.minimalValue", minimal.stringMandatory);
		assertEquals("stringOptional.defaultValue", minimal.stringOptional);
		assertEquals("stringHidden.minimalValue", minimal.stringHidden);
		assertEquals("stringHiddenOptional.defaultValue", minimal.stringHiddenOptional);
		assertEquals("stringMandatory.minimalValue", minimal.stringMandatoryF.getValue());
		assertEquals("stringOptional.defaultValue", minimal.stringOptionalF.getValue());
		assertEquals("stringHidden.minimalValue", minimal.stringHiddenF.getValue());
		assertEquals("stringHiddenOptional.defaultValue", minimal.stringHiddenOptionalF.getValue());
		assertEquals(new Day(1000,8,31), minimal.dayMandatory);
		assertEquals(new Day(1009,7,13), minimal.dayOptional);
		assertEquals(new Day(1000,8,31), minimal.dayMandatoryF.getValue());
		assertEquals(new Day(1000,8,31), minimal.dayMandatoryF.get());
		assertEquals(new Day(1009,7,13), minimal.dayOptionalF.getValue());
		assertEquals(new Day(1009,7,13), minimal.dayOptionalF.get());

		assertEquals(false, minimal.boolFalseF.isSpecified());
		assertEquals(false, minimal.boolTrueF.isSpecified());
		assertEquals(false, minimal.int10F.isSpecified());
		assertEquals(true, minimal.stringMandatoryF.isSpecified());
		assertEquals(false, minimal.stringOptionalF.isSpecified());
		assertEquals(true, minimal.stringHiddenF.isSpecified());
		assertEquals(false, minimal.stringHiddenOptionalF.isSpecified());
		assertEquals(true, minimal.dayMandatoryF.isSpecified());
		assertEquals(false, minimal.dayOptionalF.isSpecified());

		minimal.ensureEquality(minimal);

		assertFails(
				() -> minimal.getField(null),
				NullPointerException.class, "key");
		assertFails(
				() -> minimal.getField(""),
				IllegalArgumentException.class, "key must not be empty");
		assertNull(minimal.getField("x"));

		{
			final TestProperties minimal1 = new TestProperties(pminimal, "minimal2");
			assertEquals("minimal2", minimal1.getSource());
			minimal.ensureEquality(minimal1);
			minimal1.ensureEquality(minimal);
		}
		{
			final java.util.Properties p = copy(pminimal);
			p.setProperty("boolFalse", "true");
			p.setProperty("boolTrue", "false");
			p.setProperty("int10", "20");
			p.setProperty("stringMandatory", "stringMandatory.explicitValue");
			p.setProperty("stringOptional", "stringOptional.explicitValue");
			p.setProperty("stringHidden", "stringHidden.explicitValue");
			p.setProperty("stringHiddenOptional", "stringHiddenOptional.explicitValue");
			p.setProperty("dayMandatory", "2000-11-4");
			p.setProperty("dayOptional", "2001-3-31");

			final TestProperties tp = new TestProperties(p, "maximal");
			assertEquals("maximal", tp.getSource());
			assertContainsUnmodifiable(tp.getOrphanedKeys());

			assertEquals(true, tp.boolFalse);
			assertEquals(false, tp.boolTrue);
			assertEquals(Boolean.TRUE, tp.boolFalseF.getValue());
			assertEquals(Boolean.FALSE, tp.boolTrueF.getValue());
			assertEquals(20, tp.int10F.get());
			assertEquals(Integer.valueOf(20), tp.int10F.getValue());
			assertEquals("stringMandatory.explicitValue", tp.stringMandatory);
			assertEquals("stringOptional.explicitValue", tp.stringOptional);
			assertEquals("stringHidden.explicitValue", tp.stringHidden);
			assertEquals("stringHiddenOptional.explicitValue", tp.stringHiddenOptional);
			assertEquals("stringMandatory.explicitValue", tp.stringMandatoryF.getValue());
			assertEquals("stringOptional.explicitValue", tp.stringOptionalF.getValue());
			assertEquals("stringHidden.explicitValue", tp.stringHiddenF.getValue());
			assertEquals("stringHiddenOptional.explicitValue", tp.stringHiddenOptionalF.getValue());
			assertEquals(new Day(2000,11,4), tp.dayMandatory);
			assertEquals(new Day(2001,3,31), tp.dayOptional);
			assertEquals(new Day(2000,11,4), tp.dayMandatoryF.getValue());
			assertEquals(new Day(2000,11,4), tp.dayMandatoryF.get());
			assertEquals(new Day(2001,3,31), tp.dayOptionalF.getValue());
			assertEquals(new Day(2001,3,31), tp.dayOptionalF.get());

			assertEquals(true, tp.boolFalseF.isSpecified());
			assertEquals(true, tp.boolTrueF.isSpecified());
			assertEquals(true, tp.int10F.isSpecified());
			assertEquals(true, tp.stringMandatoryF.isSpecified());
			assertEquals(true, tp.stringOptionalF.isSpecified());
			assertEquals(true, tp.stringHiddenF.isSpecified());
			assertEquals(true, tp.stringHiddenOptionalF.isSpecified());
			assertEquals(true, tp.dayMandatoryF.isSpecified());
			assertEquals(true, tp.dayOptionalF.isSpecified());
		}
		{
			final java.util.Properties p = copy(pminimal);
			p.setProperty("wrongKey.zack", "somethingZack");
			final TestProperties tp = new TestProperties(p, "wrongkey");
			assertEquals("wrongkey", tp.getSource());
			assertContainsUnmodifiable("wrongKey.zack", tp.getOrphanedKeys());
			tp.ensureValidity("wrongKey");
			assertFails(
				tp::ensureValidity,
				IllegalArgumentException.class,
				"property wrongKey.zack in wrongkey is not allowed, but only one of [" +
				"boolFalse, " +
				"boolTrue, " +
				"intAny, " +
				"int10, " +
				"stringMandatory, " +
				"stringOptional, " +
				"stringHidden, " +
				"stringHiddenOptional, " +
				"dayMandatory, " +
				"dayOptional].");
			assertFails(() ->
				tp.ensureValidity("otherKey"),
				IllegalArgumentException.class,
				"property wrongKey.zack in wrongkey is not allowed, but only one of [" +
				"boolFalse, " +
				"boolTrue, " +
				"intAny, " +
				"int10, " +
				"stringMandatory, " +
				"stringOptional, " +
				"stringHidden, " +
				"stringHiddenOptional, " +
				"dayMandatory, " +
				"dayOptional] " +
				"or one starting with [otherKey].");
		}

		// boolean
		assertWrong(pminimal,
				"wrong.bool.true",
				"boolFalse", "True",
				"property boolFalse in wrong.bool.true " +
				"must be either 'true' or 'false', but was 'True'");
		assertWrong(pminimal,
				"wrong.bool.false",
				"boolFalse", "falsE",
				"property boolFalse in wrong.bool.false " +
				"must be either 'true' or 'false', but was 'falsE'");
		assertInconsistent(pminimal,
				"inconsistent.bool",
				"boolFalse", "true",
				"inconsistent initialization for boolFalse between minimal and inconsistent.bool," +
					" expected false but got true.",
				"inconsistent initialization for boolFalse between inconsistent.bool and minimal," +
					" expected true but got false.");

		// int
		{
			// test lowest value
			final java.util.Properties p = copy(pminimal);
			p.setProperty("int10", "5");
			final TestProperties tp = new TestProperties(p, "int.border");
			assertEquals(5, tp.int10F.get());
			assertEquals(Integer.valueOf(5), tp.int10F.getValue());
		}
		assertWrong(pminimal,
				"wrong.intAny.noNumber",
				"intAny", "" + (Integer.MAX_VALUE + 1l),
				"property intAny in wrong.intAny.noNumber " +
				"must be an integer, but was '" + (Integer.MAX_VALUE + 1l) + "'");
		assertWrong(pminimal,
				"wrong.int.tooSmall",
				"int10", "4",
				"property int10 in wrong.int.tooSmall " +
				"must be an integer greater or equal 5, but was 4");
		assertWrong(pminimal,
				"wrong.int.noNumber",
				"int10", "10x",
				"property int10 in wrong.int.noNumber " +
				"must be an integer greater or equal 5, but was '10x'");
		assertInconsistent(pminimal,
				"inconsistent.int",
				"int10", "88",
				"inconsistent initialization for int10 between minimal and inconsistent.int," +
					" expected 10 but got 88.",
				"inconsistent initialization for int10 between inconsistent.int and minimal," +
					" expected 88 but got 10.");

		// String
		assertWrong(pminimal,
				"wrong.stringMandatory.missing",
				"stringMandatory", null,
				"property stringMandatory in wrong.stringMandatory.missing must be specified as there is no default");
		assertWrong(pminimal,
				"wrong.stringHidden.missing",
				"stringHidden", null,
				"property stringHidden in wrong.stringHidden.missing must be specified as there is no default");
		assertInconsistent(pminimal,
				"inconsistent.stringMandatory",
				"stringMandatory", "stringMandatory.inconsistentValue",
				"inconsistent initialization for stringMandatory between minimal and inconsistent.stringMandatory," +
					" expected stringMandatory.minimalValue but got stringMandatory.inconsistentValue.",
				"inconsistent initialization for stringMandatory between inconsistent.stringMandatory and minimal," +
					" expected stringMandatory.inconsistentValue but got stringMandatory.minimalValue.");
		assertInconsistent(pminimal,
				"inconsistent.stringOptional",
				"stringOptional", "stringOptional.inconsistentValue",
				"inconsistent initialization for stringOptional between minimal and inconsistent.stringOptional," +
					" expected stringOptional.defaultValue but got stringOptional.inconsistentValue.",
				"inconsistent initialization for stringOptional between inconsistent.stringOptional and minimal," +
					" expected stringOptional.inconsistentValue but got stringOptional.defaultValue.");
		assertInconsistent(pminimal,
				"inconsistent.stringHidden",
				"stringHidden", "stringHidden.inconsistentValue",
				"inconsistent initialization for stringHidden between minimal and inconsistent.stringHidden.",
				"inconsistent initialization for stringHidden between inconsistent.stringHidden and minimal.");

		// Day
		assertWrong(pminimal,
				"wrong.dayMandatory.missing",
				"dayMandatory", null,
				"property dayMandatory in wrong.dayMandatory.missing " +
				"must be specified as there is no default");
		assertWrong(pminimal,
				"wrong.dayMandatory.invalidFormat",
				"dayMandatory", "no-date-set",
				"property dayMandatory in wrong.dayMandatory.invalidFormat " +
				"must be a day formatted as yyyy-mm-dd, but was 'no-date-set'");
		assertWrong(pminimal,
				"wrong.dayMandatory.invalidYear",
				"dayMandatory", "200-04-04",
				"property dayMandatory in wrong.dayMandatory.invalidYear " +
				"must be a day formatted as yyyy-mm-dd, but was '200-04-04'");
		assertWrong(pminimal,
				"wrong.dayMandatory.invalidMonth",
				"dayMandatory", "2000-40-04",
				"property dayMandatory in wrong.dayMandatory.invalidMonth " +
				"must be a day formatted as yyyy-mm-dd, but was '2000-40-04'");
		assertWrong(pminimal,
				"wrong.dayMandatory.invalidDay",
				"dayMandatory", "2000-04-40",
				"property dayMandatory in wrong.dayMandatory.invalidDay " +
				"must be a day formatted as yyyy-mm-dd, but was '2000-04-40'");
		assertInconsistent(pminimal,
				"inconsistent.dayMandatory",
				"dayMandatory", "3000-01-01",
				"inconsistent initialization for dayMandatory between minimal and inconsistent.dayMandatory," +
					" expected 1000/8/31 but got 3000/1/1.",
				"inconsistent initialization for dayMandatory between inconsistent.dayMandatory and minimal," +
					" expected 3000/1/1 but got 1000/8/31.");
		assertInconsistent(pminimal,
				"inconsistent.dayOptional",
				"dayOptional", "3001-02-01",
				"inconsistent initialization for dayOptional between minimal and inconsistent.dayOptional," +
					" expected 1009/7/13 but got 3001/2/1.",
				"inconsistent initialization for dayOptional between inconsistent.dayOptional and minimal," +
					" expected 3001/2/1 but got 1009/7/13.");
	}

	@SuppressWarnings("unused")
	@Test void testSourceNull()
	{
		assertFails(() ->
			new Properties(null),
			NullPointerException.class, null);
	}


	/**
	 * Asserts the property with given key and value set in the given java.util.Properties template will cause an IllegalPropertiesException
	 * with given message when a cope.util.Properties is created.
	 */
	@SuppressWarnings("unused")
	private static void assertWrong(
			final java.util.Properties template,
			final String sourceDescription,
			final String key,
			final String value,
			final String message)
	{
		final java.util.Properties wrongProps = copy(template);
		if(value!=null)
			wrongProps.setProperty(key, value);
		else
			wrongProps.remove(key);

		final IllegalPropertiesException e = assertThrows(
				IllegalPropertiesException.class,
				() -> new TestProperties(wrongProps, sourceDescription));
		assertEquals(message, e.getMessage());
		assertEquals(key, e.getKey());
	}

	/**
	 * Asserts a valid change of the given property (key = value) will  cause an InvalidArgumentException when
	 * calling ensureEquality() on Properties based on template compared with a Properties based on template with changed value.
	 */
	private static void assertInconsistent(
			final java.util.Properties template,
			final String sourceDescription,
			final String key,
			final String value,
			final String message1, final String message2)
	{
		final TestProperties templateProps = new TestProperties(template, "minimal");
		templateProps.assertIt();

		final java.util.Properties p = copy(template);
		p.setProperty(key, value);
		final TestProperties inconsistentProps = new TestProperties(p, sourceDescription);
		assertFails(() ->
			templateProps.ensureEquality(inconsistentProps),
			IllegalArgumentException.class, message1);
		assertFails(() ->
			inconsistentProps.ensureEquality(templateProps),
			IllegalArgumentException.class, message2);
	}

	private static java.util.Properties copy(final java.util.Properties source)
	{
		return (java.util.Properties)source.clone();
	}

	static void assertThrowsIllegalProperties(
			final Executable executable,
			final String expectedKey,
			final String expectedDetail,
			final Class<? extends Throwable> expectedCause)
	{
		final IllegalPropertiesException result =
				assertThrows(IllegalPropertiesException.class, executable);
		assertSame(IllegalPropertiesException.class, result.getClass());
		assertEquals(expectedKey, result.getKey());
		assertEquals(expectedDetail, result.getDetail());
		final Throwable actualCause = result.getCause();
		assertEquals(expectedCause, actualCause!=null ? actualCause.getClass() : null);
	}
}
