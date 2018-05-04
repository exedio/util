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
import static com.exedio.cope.junit.CopeAssert.assertUnmodifiable;
import static com.exedio.cope.util.Sources.EMPTY;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

public class EmptySourceTest
{
	@Test void testIt()
	{
		assertFails(() ->
			EMPTY.get(null),
			NullPointerException.class, "key");
		assertFails(() ->
			EMPTY.get(""),
			IllegalArgumentException.class, "key must not be empty");
		assertEquals(null, EMPTY.get("xxx"));
		assertUnmodifiable(EMPTY.keySet());
		assertEquals(asList(), EMPTY.keySet());
		assertSame(EMPTY, EMPTY.reload());
		assertEquals("empty", EMPTY.getDescription());
		assertEquals("EmptySource", EMPTY.toString());
	}

	@Deprecated // OK: testing deprecated API
	@Test void testDeprecated()
	{
		assertSame(Properties.EMPTY_SOURCE, EMPTY);
	}
}
