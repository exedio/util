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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SequenceCheckerTest
{
	SequenceChecker sc;

	@BeforeEach final void setUp()
	{
		sc = new SequenceChecker(5);
	}

	@Test final void testInOrder()
	{
		assertEquals(5, sc.getCapacity());
		assertIt();

		assertEquals(false, sc.check(3));
		assertIt(3, 3, 1, 0, 0, 0, 0, 0);

		assertEquals(false, sc.check(4));
		assertIt(3, 4, 2, 0, 0, 0, 0, 0);

		assertEquals(false, sc.check(5));
		assertIt(3, 5, 3, 0, 0, 0, 0, 0);

		assertEquals(false, sc.check(6));
		assertIt(3, 6, 4, 0, 0, 0, 0, 0);

		assertEquals(false, sc.check(7));
		assertIt(3, 7, 5, 0, 0, 0, 0, 0);

		assertEquals(false, sc.check(8));
		assertIt(3, 8, 6, 0, 0, 0, 0, 0);

		assertEquals(false, sc.check(9));
		assertIt(3, 9, 7, 0, 0, 0, 0, 0);

		assertEquals(false, sc.check(10));
		assertIt(3,10, 8, 0, 0, 0, 0, 0);

		assertEquals(false, sc.check(2));
		assertIt(3,10, 8, 0, 0, 0, 0, 0);

		assertEquals(false, sc.check(Integer.MIN_VALUE));
		assertIt(3,10, 8, 0, 0, 0, 0, 0);
	}

	@Test final void testOutOfOrder()
	{
		assertEquals(5, sc.getCapacity());
		assertIt();

		assertEquals(false, sc.check(3));
		assertIt(3, 3, 1, 0, 0, 0, 0, 0);

		assertEquals(false, sc.check(5));
		assertIt(3, 5, 2, 0, 0, 0, 0, 1);

		assertEquals(false, sc.check(4));
		assertIt(3, 5, 2, 1, 0, 0, 0, 0);

		assertEquals(false, sc.check(7));
		assertIt(3, 7, 3, 1, 0, 0, 0, 1);

		assertEquals(false, sc.check(6));
		assertIt(3, 7, 3, 2, 0, 0, 0, 0);

		assertEquals(false, sc.check(9));
		assertIt(3, 9, 4, 2, 0, 0, 0, 1);

		assertEquals(false, sc.check(8));
		assertIt(3, 9, 4, 3, 0, 0, 0, 0);

		assertEquals(false, sc.check(10));
		assertIt(3,10, 5, 3, 0, 0, 0, 0);
	}

	@Test final void testDuplicate()
	{
		assertEquals(5, sc.getCapacity());
		assertIt();

		assertEquals(false, sc.check(3));
		assertIt(3, 3, 1, 0, 0, 0, 0, 0);

		assertEquals(true, sc.check(3));
		assertIt(3, 3, 1, 0, 1, 0, 0, 0);

		assertEquals(false, sc.check(8));
		assertIt(3, 8, 2, 0, 1, 0, 0, 4);

		assertEquals(true, sc.check(8));
		assertIt(3, 8, 2, 0, 2, 0, 0, 4);

		assertEquals(false, sc.check(15));
		assertIt(3,15, 3, 0, 2, 6, 0, 4);

		assertEquals(true, sc.check(15));
		assertIt(3,15, 3, 0, 3, 6, 0, 4);
	}

	@Test final void testLostStep()
	{
		assertEquals(5, sc.getCapacity());
		assertIt();

		assertEquals(false, sc.check(3));
		assertIt(3, 3, 1, 0, 0, 0, 0, 0);

		assertEquals(false, sc.check(5));
		assertIt(3, 5, 2, 0, 0, 0, 0, 1);

		assertEquals(false, sc.check(8));
		assertIt(3, 8, 3, 0, 0, 0, 0, 3);

		assertEquals(false, sc.check(9));
		assertIt(3, 9, 4, 0, 0, 1, 0, 2);

		assertEquals(false, sc.check(10));
		assertIt(3,10, 5, 0, 0, 1, 0, 2);

		assertEquals(false, sc.check(11));
		assertIt(3,11, 6, 0, 0, 2, 0, 1);

		assertEquals(false, sc.check(12));
		assertIt(3,12, 7, 0, 0, 3, 0, 0);

		assertEquals(false, sc.check(13));
		assertIt(3,13, 8, 0, 0, 3, 0, 0);
	}

	@Test final void testLostJump()
	{
		assertEquals(5, sc.getCapacity());
		assertIt();

		assertEquals(false, sc.check(3));
		assertIt(3, 3, 1, 0, 0, 0, 0, 0);

		assertEquals(false, sc.check(5));
		assertIt(3, 5, 2, 0, 0, 0, 0, 1);

		assertEquals(false, sc.check(8));
		assertIt(3, 8, 3, 0, 0, 0, 0, 3);

		assertEquals(false, sc.check(13));
		assertIt(3,13, 4, 0, 0, 3, 0, 4);
	}

	@Test final void testLostJumpOver()
	{
		assertEquals(5, sc.getCapacity());
		assertIt();

		assertEquals(false, sc.check(3));
		assertIt(3, 3, 1, 0, 0, 0, 0, 0);

		assertEquals(false, sc.check(5));
		assertIt(3, 5, 2, 0, 0, 0, 0, 1);

		assertEquals(false, sc.check(8));
		assertIt(3, 8, 3, 0, 0, 0, 0, 3);

		assertEquals(false, sc.check(16));
		assertIt(3,16, 4, 0, 0, 6, 0, 4);
	}

	@Test final void testLate()
	{
		assertEquals(5, sc.getCapacity());
		assertIt();

		assertEquals(false, sc.check(3));
		assertIt(3, 3, 1, 0, 0, 0, 0, 0);

		assertEquals(false, sc.check(13));
		assertIt(3,13, 2, 0, 0, 5, 0, 4);

		assertEquals(false, sc.check(7));
		assertIt(3,13, 2, 0, 0, 5, 1, 4);

		assertEquals(false, sc.check(8));
		assertIt(3,13, 2, 0, 0, 5, 2, 4);

		assertEquals(false, sc.check(9));
		assertIt(3,13, 2, 1, 0, 5, 2, 3);

		assertEquals(false, sc.check(10));
		assertIt(3,13, 2, 2, 0, 5, 2, 2);
	}

	@Test void testException()
	{
		assertFails(() ->
			new SequenceChecker(0),
			IllegalArgumentException.class,
			"capacity must be greater zero, but was 0");
	}

	private void assertIt()
	{
		final SequenceChecker.Info sci = sc.getInfo();
		//noinspection ResultOfMethodCallIgnored
		assertFails(() ->
			sc.getFirstNumber(),
			IllegalStateException.class,
			"did not yet check first number");
		//noinspection ResultOfMethodCallIgnored
		assertFails(() ->
			sc.getMaxNumber(),
			IllegalStateException.class,
			"did not yet check first number");
		assertEquals(0, sci.inOrder(),    "countInOrder");
		assertEquals(0, sci.outOfOrder(), "countOutOfOrder");
		assertEquals(0, sci.duplicate(),  "countDuplicate");
		assertEquals(0, sci.lost(),       "countLost");
		assertEquals(0, sci.late(),       "countLate");
		assertEquals(0, sci.pending(),    "countPending");
		assertEquals(0, sc.countPending(),   "countPending");
	}

	private void assertIt(
			final int firstNumber,
			final int maxNumber,
			final int countInOrder,
			final int countOutOfOrder,
			final int countDuplicate,
			final int countLost,
			final int countLate,
			final int countPending)
	{
		assertTrue(countPending<sc.getCapacity(), String.valueOf(countPending));
		final SequenceChecker.Info sci = sc.getInfo();
		assertEquals(firstNumber,     sc.getFirstNumber(), "firstNumber");
		assertEquals(maxNumber,       sc.getMaxNumber(),   "maxNumber");
		assertEquals(countInOrder,    sci.inOrder(),    "countInOrder");
		assertEquals(countOutOfOrder, sci.outOfOrder(), "countOutOfOrder");
		assertEquals(countDuplicate,  sci.duplicate(),  "countDuplicate");
		assertEquals(countLost,       sci.lost(),       "countLost");
		assertEquals(countLate,       sci.late(),       "countLate");
		assertEquals(countPending,    sci.pending(),    "countPending");
		assertEquals(countPending,    sc.countPending(),   "countPending");
	}
}
