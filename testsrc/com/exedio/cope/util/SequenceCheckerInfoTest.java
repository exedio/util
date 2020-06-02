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

import com.exedio.cope.util.SequenceChecker.Info;
import org.junit.jupiter.api.Test;

public class SequenceCheckerInfoTest
{
	@Test void test()
	{
		final Info i = new Info(11, 22, 33, 44, 55, 66);
		assertEquals(11, i.getInOrder());
		assertEquals(22, i.getOutOfOrder());
		assertEquals(33, i.getDuplicate());
		assertEquals(44, i.getLost());
		assertEquals(55, i.getLate());
		assertEquals(66, i.getPending());
	}
	@Test void testMinimum()
	{
		final Info i = new Info(0, 0, 0, 0, 0, 0);
		assertEquals(0, i.getInOrder());
		assertEquals(0, i.getOutOfOrder());
		assertEquals(0, i.getDuplicate());
		assertEquals(0, i.getLost());
		assertEquals(0, i.getLate());
		assertEquals(0, i.getPending());
	}
	@Test void testNegative()
	{
		assertFails(() -> new Info(-1, -1, -1, -1, -1, -1), IllegalArgumentException.class,    "inOrder must not be negative, but was -1");
		assertFails(() -> new Info( 0, -1, -1, -1, -1, -1), IllegalArgumentException.class, "outOfOrder must not be negative, but was -1");
		assertFails(() -> new Info( 0,  0, -1, -1, -1, -1), IllegalArgumentException.class,  "duplicate must not be negative, but was -1");
		assertFails(() -> new Info( 0,  0,  0, -1, -1, -1), IllegalArgumentException.class,       "lost must not be negative, but was -1");
		assertFails(() -> new Info( 0,  0,  0,  0, -1, -1), IllegalArgumentException.class,       "late must not be negative, but was -1");
		assertFails(() -> new Info( 0,  0,  0,  0,  0, -1), IllegalArgumentException.class,    "pending must not be negative, but was -1");
	}
}
