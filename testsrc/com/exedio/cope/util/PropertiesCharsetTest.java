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
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_16;
import static java.nio.charset.StandardCharsets.UTF_16BE;
import static java.nio.charset.StandardCharsets.UTF_16LE;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import org.junit.jupiter.api.Test;

public class PropertiesCharsetTest
{
	@Test void testMinimal()
	{
		final MyProps props = new MyProps(minimal());
		props.assertIt();

		assertEquals(UTF_16,   props.mandatory);
		assertEquals(US_ASCII, props.optional);
		assertEquals(UTF_16,   props.mandatoryF.getValue());
		assertEquals(US_ASCII, props.optionalF .getValue());
		assertTrue (props.mandatoryF.isSpecified());
		assertFalse(props.optionalF .isSpecified());
	}

	@Test void testSet()
	{
		final java.util.Properties p = minimal();
		p.setProperty("mandatory", UTF_16LE.name());
		p.setProperty("optional",  UTF_16BE.name());
		final MyProps props = new MyProps(p);
		props.assertIt();

		assertEquals(UTF_16LE, props.mandatory);
		assertEquals(UTF_16BE, props.optional);
		assertEquals(UTF_16LE, props.mandatoryF.getValue());
		assertEquals(UTF_16BE, props.optionalF .getValue());
		assertTrue (props.mandatoryF.isSpecified());
		assertTrue (props.optionalF .isSpecified());
	}

	@Test void testMandatoryWrong()
	{
		assertWrong(
				"mandatory", "WRONG",
				"must be one of Charset.availableCharsets(), but was 'WRONG'",
				UnsupportedCharsetException.class);
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
				"must be one of Charset.availableCharsets(), but was 'WRONG'",
				UnsupportedCharsetException.class);
	}


	static class MyProps extends MyProperties
	{
		final Charset mandatory = value("mandatory", (Charset)null);
		final Charset optional  = value("optional" , US_ASCII);

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

			assertEquals(null, mandatoryF.getMinimum());
			assertEquals(null, optionalF .getMinimum());

			assertEquals(null,       mandatoryF.getDefaultValue());
			assertEquals(US_ASCII,   optionalF .getDefaultValue());

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
		result.setProperty("mandatory", UTF_16.name());
		return result;
	}
}
