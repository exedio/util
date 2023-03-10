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
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class HolderTest
{
	@Test void test()
	{
		final Holder<String> h = new Holder<>("initial");
		assertEquals("initial", h.get());

		h.override("overridden");
		assertEquals("overridden", h.get());

		h.override("overridden2");
		assertEquals("overridden2", h.get());

		h.clearOverride();
		assertEquals("initial", h.get());
	}
	@Test void testInitialNull()
	{
		assertFails(
				() -> new Holder<>(null),
				NullPointerException.class,
				"value");
	}
	@Test void testOverrideNull()
	{
		final Holder<String> h = new Holder<>("initial");
		assertEquals("initial", h.get());

		assertFails(
				() -> h.override(null),
				NullPointerException.class,
				"value");
		assertEquals("initial", h.get());
	}
	@Test void testClearInitial()
	{
		final Holder<String> h = new Holder<>("initial");
		assertEquals("initial", h.get());

		h.clearOverride();
		assertEquals("initial", h.get());
	}
}
