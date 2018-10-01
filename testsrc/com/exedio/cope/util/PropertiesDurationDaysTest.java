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
import static com.exedio.cope.util.PropertiesTest.assertThrowsIllegalProperties;
import static com.exedio.cope.util.Sources.view;
import static java.time.Duration.ofDays;
import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofNanos;
import static java.time.Duration.ofSeconds;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import org.junit.jupiter.api.Test;

public class PropertiesDurationDaysTest
{
	@Test void testMinimal()
	{
		final MyProps props = new MyProps(minimal());
		props.assertIt();

		assertEquals(ofDays(4), props.min);
		assertEquals(ofDays(5), props.max);
		assertEquals("PT96H",  props.minF.getValueString());
		assertEquals("PT120H", props.maxF.getValueString());
	}

	@Test void testSet()
	{
		final java.util.Properties p = minimal();
		p.setProperty("min", "P4DT44M");
		p.setProperty("max", "P8D");
		final MyProps props = new MyProps(p);
		props.assertIt();

		assertEquals(ofDays(4).plus(ofMinutes(44)), props.min);
		assertEquals(ofDays(8), props.max);
		assertEquals("PT96H44M",props.minF.getValueString());
		assertEquals("PT192H",  props.maxF.getValueString());
	}

	@Test void testGetString()
	{
		final MyProps props = new MyProps(minimal());
		props.assertIt();

		assertEquals("PT21M", props.minF.getString(ofMinutes(21)));
		assertEquals("PT504H",props.minF.getString(ofDays(21)));
		assertEquals(null,    props.minF.getString(null));
		assertFails(
				() -> props.minF.getString(55),
				ClassCastException.class,
				"Cannot cast java.lang.Integer to java.time.Duration");
	}

	@Test void testMinMinimum()
	{
		assertWrong(
				"min", "P2D",
				"must be a duration greater or equal PT72H, but was PT48H", null);
	}

	@Test void testMaxMinimum()
	{
		assertWrong(
				"max", "P1D",
				"must be a duration between PT48H and PT192H, but was PT24H", null);
	}

	@Test void testMaxMaximum()
	{
		assertWrong(
				"max", "P9D",
				"must be a duration between PT48H and PT192H, but was PT216H", null);
	}


	static class MyProps extends Properties
	{
		final Duration min = value("min", ofDays(4), ofDays(3));
		final Duration max = value("max", ofDays(5), ofDays(2), ofDays(8));

		MyProps(final java.util.Properties source)
		{
			super(view(source, "sourceDescription"));
		}

		final Field<?> minF = getField("min");
		final Field<?> maxF = getField("max");


		void assertIt()
		{
			assertEquals("min", minF.getKey());
			assertEquals("max", maxF.getKey());

			assertEquals(ofDays(3), minF.getMinimum());
			assertEquals(ofDays(2), maxF.getMinimum());

			assertEquals("PT72H", minF.getMinimumString());
			assertEquals("PT48H", maxF.getMinimumString());

			assertEquals(ofDays(4), minF.getDefaultValue());
			assertEquals(ofDays(5), maxF.getDefaultValue());

			assertEquals("PT96H",  minF.getDefaultValueString());
			assertEquals("PT120H", maxF.getDefaultValueString());
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
		return new java.util.Properties();
	}


	@SuppressWarnings("unused")
	@Test void testDefaultViolatesMinimum()
	{
		assertFails(
			PropsDefaultViolatesMinimum::new,
			IllegalArgumentException.class,
			"default of myKey must not be smaller than minimum of PT144H, but was PT143H59M59.999999999S");
	}
	static class PropsDefaultViolatesMinimum extends Properties
	{
		PropsDefaultViolatesMinimum()
		{
			super(Sources.EMPTY);
			value("myKey", ofDays(6).minus(ofNanos(1)), ofDays(6));
		}
	}


	@Test void testDefaultViolatesMaximum()
	{
		assertFails(
				PropsDefaultViolatesMaximum::new,
				IllegalArgumentException.class,
				"default of myKey must not be greater than maximum of PT168H, but was PT168H0.000000001S");
	}
	static class PropsDefaultViolatesMaximum extends Properties
	{
		PropsDefaultViolatesMaximum()
		{
			super(Sources.EMPTY);
			value("myKey", ofDays(7).plus(ofNanos(1)), ofDays(6), ofDays(7));
		}
	}


	@Test void testToStringPlain()
	{
		assertEquals("PT"+ "23H"+ "59M"+ "59S", ofDays( 1).minus(SECOND).toString());
		assertEquals("PT"+"-23H"+"-59M"+"-59S", ofDays(-1).plus (SECOND).toString());
		assertEquals("PT"+ "24H"+         "1S", ofDays( 1).plus (SECOND).toString());
		assertEquals("PT"+"-24H"+        "-1S", ofDays(-1).minus(SECOND).toString());
	}

	@Test void testToString()
	{
		assertIt("PT0S", Duration.ZERO);
		assertIt("P1D",  ofDays( 1));
		assertIt("P-1D", ofDays(-1));
		assertIt("P5D",  ofDays( 5));
		assertIt("P-5D", ofDays(-5));

		assertIt("P"+   "T"+ "23H"+ "59M"+ "59S", ofDays( 1).minus(SECOND));
		assertIt("P"+   "T"+"-23H"+"-59M"+"-59S", ofDays(-1).plus (SECOND));
		assertIt("P"+ "1DT"+                "1S", ofDays( 1).plus (SECOND));
		assertIt("P"+"-1DT"+               "-1S", ofDays(-1).minus(SECOND));

		assertIt("P"+ "4DT"+ "23H"+ "59M"+ "59S", ofDays( 5).minus(SECOND));
		assertIt("P"+"-4DT"+"-23H"+"-59M"+"-59S", ofDays(-5).plus (SECOND));
		assertIt("P"+ "5DT"+                "1S", ofDays( 5).plus (SECOND));
		assertIt("P"+"-5DT"+               "-1S", ofDays(-5).minus(SECOND));
	}
	private static void assertIt(final String s, final Duration d)
	{
		final Duration parsed = Duration.parse(s);
		assertAll(
				() -> assertEquals(d, parsed, "parse(" + d.toNanos() + "/" + parsed.toNanos() + ")"));
	}
	private static final Duration SECOND = ofSeconds(1);
}
