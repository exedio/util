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
import static com.exedio.cope.util.Check.requireAtLeast;
import static com.exedio.cope.util.Check.requireGreaterZero;
import static com.exedio.cope.util.Check.requireInRange;
import static com.exedio.cope.util.Check.requireNonEmpty;
import static com.exedio.cope.util.Check.requireNonEmptyAndCopy;
import static com.exedio.cope.util.Check.requireNonNegative;
import static java.time.Duration.ofNanos;
import static java.time.Duration.ofSeconds;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.Duration;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

@SuppressWarnings("DataFlowIssue")
public class CheckTest
{
	@Test void testRequireGreaterZeroInt()
	{
		assertEquals(1, requireGreaterZero(1, "name"));
	}
	@Test void testRequireGreaterZeroIntZero()
	{
		assertFails(() ->
			requireGreaterZero(0, "name"),
			IllegalArgumentException.class,
			"name must be greater zero, but was 0");
	}
	@Test void testRequireGreaterZeroIntNegative()
	{
		assertFails(() ->
			requireGreaterZero(-1, "name"),
			IllegalArgumentException.class,
			"name must be greater zero, but was -1");
	}

	@Test void testRequireGreaterZeroLong()
	{
		assertEquals(1, requireGreaterZero(1L, "name"));
	}
	@Test void testRequireGreaterZeroLongZero()
	{
		assertFails(() ->
			requireGreaterZero(0L, "name"),
			IllegalArgumentException.class,
			"name must be greater zero, but was 0");
	}
	@Test void testRequireGreaterZeroLongNegative()
	{
		assertFails(() ->
			requireGreaterZero(-1L, "name"),
			IllegalArgumentException.class,
			"name must be greater zero, but was -1");
	}

	@Test void testRequireGreaterZeroDouble()
	{
		assertEquals(0.001, requireGreaterZero(0.001, "name"));
	}
	@Test void testRequireGreaterZeroDoubleZero()
	{
		assertFails(() ->
			requireGreaterZero(0d, "name"),
			IllegalArgumentException.class,
			"name must be greater zero, but was 0.0");
	}
	@Test void testRequireGreaterZeroDoubleNegative()
	{
		assertFails(() ->
			requireGreaterZero(-0.001, "name"),
			IllegalArgumentException.class,
			"name must be greater zero, but was -0.001");
	}

	@Test void testRequireNonNegativeInt()
	{
		assertEquals(1, requireNonNegative(1, "name"));
	}
	@Test void testRequireNonNegativeIntZero()
	{
		assertEquals(0, requireNonNegative(0, "name"));
	}
	@Test void testRequireNonNegativeIntNegative()
	{
		assertFails(() ->
			requireNonNegative(-1, "name"),
			IllegalArgumentException.class,
			"name must not be negative, but was -1");
	}

	@Test void testRequireNonNegativeLong()
	{
		assertEquals(1L, requireNonNegative(1L, "name"));
	}
	@Test void testRequireNonNegativeLongZero()
	{
		assertEquals(0L, requireNonNegative(0L, "name"));
	}
	@Test void testRequireNonNegativeLongNegative()
	{
		assertFails(() ->
			requireNonNegative(-1L, "name"),
			IllegalArgumentException.class,
			"name must not be negative, but was -1");
	}

	@Test void testRequireNonNegativeDouble()
	{
		assertEquals(0.001, requireNonNegative(0.001, "name"));
	}
	@Test void testRequireNonNegativeDoubleZero()
	{
		assertEquals(0L, requireNonNegative(0d, "name"));
	}
	@Test void testRequireNonNegativeDoubleNegative()
	{
		assertFails(() ->
			requireNonNegative(-0.001, "name"),
			IllegalArgumentException.class,
			"name must not be negative, but was -0.001");
	}

	@Test void testRequireNonNegativeDuration()
	{
		final Duration value = ofNanos(1);
		assertSame(value, requireNonNegative(value, "name"));
	}
	@Test void testRequireNonNegativeDurationZero()
	{
		assertSame(Duration.ZERO, requireNonNegative(Duration.ZERO, "name"));
	}
	@Test void testRequireNonNegativeDurationNegative()
	{
		assertFails(() ->
			requireNonNegative(ofNanos(1).negated(), "name"),
			IllegalArgumentException.class,
			"name must not be negative, but was PT-0.000000001S");
	}
	@Test void testRequireNonNegativeDurationNull()
	{
		//noinspection RedundantCast
		assertFails(() ->
			requireNonNegative((Duration)null, "name"),
			NullPointerException.class,
			"name");
	}

	@Test void testRequireAtLeastInt()
	{
		assertEquals(55, requireAtLeast(55, "name", 55));
	}
	@Test void testRequireAtLeastIntFails()
	{
		assertFails(() ->
			requireAtLeast(54, "name", 55),
			IllegalArgumentException.class,
			"name must be at least 55, but was 54");
	}

	@Test void testRequireAtLeastLong()
	{
		assertEquals(55L, requireAtLeast(55L, "name", 55L));
	}
	@Test void testRequireAtLeastLongFails()
	{
		assertFails(() ->
			requireAtLeast(54L, "name", 55L),
			IllegalArgumentException.class,
			"name must be at least 55, but was 54");
	}

	@Test void testRequireAtLeastDouble()
	{
		assertEquals(55.5, requireAtLeast(55.5, "name", 55.5));
	}
	@Test void testRequireAtLeastDoubleFails()
	{
		assertFails(() ->
			requireAtLeast(55.4, "name", 55.5),
			IllegalArgumentException.class,
			"name must be at least 55.5, but was 55.4");
	}

	@Test void testRequireAtLeast()
	{
		final Duration value = ofSeconds(55);
		assertSame(value, requireAtLeast(value, "name", ofSeconds(55)));
	}
	@Test void testRequireAtLeastFails()
	{
		assertFails(() ->
			requireAtLeast(ofSeconds(55).minus(ofNanos(1)), "name", ofSeconds(55)),
			IllegalArgumentException.class,
			"name must be at least PT55S, but was PT54.999999999S");
	}
	@Test void testRequireAtLeastNull()
	{
		assertFails(() ->
			requireAtLeast(null, "name", null),
			NullPointerException.class,
			"name");
	}
	@Test void testRequireAtLeastMinimumNull()
	{
		assertFails(() ->
			requireAtLeast(ofSeconds(55), "name", null),
			NullPointerException.class,
			"minimum");
	}
	@Test void testRequireInRangeInt()
	{
		assertEquals(-3, requireInRange(-3, "name", -3, 12));
		assertEquals(11, requireInRange(11, "name", -3, 12));
	}
	@Test void testRequireInRangeIntFails()
	{
		assertFails(() ->
				requireInRange(-4, "name", -3, 12),
				IllegalArgumentException.class,
				"name must be at least -3 and lower than 12, but was -4");
		assertFails(() ->
						requireInRange(12, "name", -3, 12),
				IllegalArgumentException.class,
				"name must be at least -3 and lower than 12, but was 12");
	}

	@Test void testRequireNonEmptyString()
	{
		assertSame("x", requireNonEmpty("x", "name"));
	}
	@Test void testRequireNonEmptyStringNull()
	{
		assertFails(() ->
			requireNonEmpty(null, "name"),
			NullPointerException.class,
			"name");
	}
	@Test void testRequireNonEmptyStringEmpty()
	{
		assertFails(() ->
			requireNonEmpty("", "name"),
			IllegalArgumentException.class,
			"name must not be empty");
	}

	@Test void testRequireNonEmptyAndCopyObjectsCopy()
	{
		final Object[] original = {"a", "b", "c"};
		final Object[] copy = requireNonEmptyAndCopy(original, "name");
		assertEquals(Arrays.asList("a", "b", "c"), Arrays.asList(copy));
		assertNotSame(original, copy);
	}
	@Test void testRequireNonEmptyAndCopyObjectsNull()
	{
		assertFails(() ->
			requireNonEmptyAndCopy((Object[])null, "name"),
			NullPointerException.class,
			"name");
	}
	@Test void testRequireNonEmptyAndCopyObjectsEmpty()
	{
		assertFails(() ->
			requireNonEmptyAndCopy(new Object[0], "name"),
			IllegalArgumentException.class,
			"name must not be empty");
	}
	@Test void testRequireNonEmptyAndCopyObjectsElementNull()
	{
		assertFails(() ->
			requireNonEmptyAndCopy(new Object[]{"hallo", null}, "name"),
			NullPointerException.class,
			"name[1]");
	}

	@Test void testRequireNonEmptyAndCopyStringsCopy()
	{
		final String[] original = {"a", "b", "c"};
		final String[] copy = requireNonEmptyAndCopy(original, "name");
		assertEquals(Arrays.asList("a", "b", "c"), Arrays.asList(copy));
		assertNotSame(original, copy);
	}
	@SuppressWarnings("RedundantCast")
	@Test void testRequireNonEmptyAndCopyStringsNull()
	{
		assertFails(() ->
			requireNonEmptyAndCopy((String[])null, "name"),
			NullPointerException.class,
			"name");
	}
	@Test void testRequireNonEmptyAndCopyStringsEmpty()
	{
		assertFails(() ->
			requireNonEmptyAndCopy(new String[0], "name"),
			IllegalArgumentException.class,
			"name must not be empty");
	}
	@Test void testRequireNonEmptyAndCopyStringsElementNull()
	{
		assertFails(() ->
			requireNonEmptyAndCopy(new String[]{"hallo", null}, "name"),
			NullPointerException.class,
			"name[1]");
	}
	@Test void testRequireNonEmptyAndCopyStringsElementEmpty()
	{
		assertFails(() ->
			requireNonEmptyAndCopy(new String[]{"hallo", ""}, "name"),
			IllegalArgumentException.class,
			"name[1] must not be empty");
	}
}
