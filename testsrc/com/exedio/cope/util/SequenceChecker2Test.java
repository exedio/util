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
import static com.exedio.cope.util.SequenceChecker2.Result.duplicate;
import static com.exedio.cope.util.SequenceChecker2.Result.early;
import static com.exedio.cope.util.SequenceChecker2.Result.inOrder;
import static com.exedio.cope.util.SequenceChecker2.Result.late;
import static com.exedio.cope.util.SequenceChecker2.Result.outOfOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.IntConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SequenceChecker2Test
{
	SequenceChecker2 sc;
	IntConsumer lost;
	int lostValue;

	@BeforeEach final void setUp()
	{
		sc = new SequenceChecker2(5);
		lost = value ->
		{
			assertTrue(value>0);
			assertTrue(lostValue==0);
			lostValue = value;
		};
		lostValue = 0;
	}

	@Test final void testInOrder()
	{
		assertEquals(5, sc.getCapacity());
		assertIt();

		assertEquals(inOrder, sc.check(3, lost));
		assertIt(3, 3, 0, 0);

		assertEquals(inOrder, sc.check(4, lost));
		assertIt(3, 4, 0, 0);

		assertEquals(inOrder, sc.check(5, lost));
		assertIt(3, 5, 0, 0);

		assertEquals(inOrder, sc.check(6, lost));
		assertIt(3, 6, 0, 0);

		assertEquals(inOrder, sc.check(7, lost));
		assertIt(3, 7, 0, 0);

		assertEquals(inOrder, sc.check(8, lost));
		assertIt(3, 8, 0, 0);

		assertEquals(inOrder, sc.check(9, lost));
		assertIt(3, 9, 0, 0);

		assertEquals(inOrder, sc.check(10, lost));
		assertIt(3,10, 0, 0);

		assertEquals(early, sc.check(2, lost));
		assertIt(3,10, 0, 0);

		assertEquals(early, sc.check(Integer.MIN_VALUE, lost));
		assertIt(3,10, 0, 0);
	}

	@Test final void testOutOfOrder()
	{
		assertEquals(5, sc.getCapacity());
		assertIt();

		assertEquals(inOrder, sc.check(3, lost));
		assertIt(3, 3, 0, 0);

		assertEquals(inOrder, sc.check(5, lost));
		assertIt(3, 5, 0, 1);

		assertEquals(outOfOrder, sc.check(4, lost));
		assertIt(3, 5, 0, 0);

		assertEquals(inOrder, sc.check(7, lost));
		assertIt(3, 7, 0, 1);

		assertEquals(outOfOrder, sc.check(6, lost));
		assertIt(3, 7, 0, 0);

		assertEquals(inOrder, sc.check(9, lost));
		assertIt(3, 9, 0, 1);

		assertEquals(outOfOrder, sc.check(8, lost));
		assertIt(3, 9, 0, 0);

		assertEquals(inOrder, sc.check(10, lost));
		assertIt(3,10, 0, 0);
	}

	@Test final void testDuplicate()
	{
		assertEquals(5, sc.getCapacity());
		assertIt();

		assertEquals(inOrder, sc.check(3, lost));
		assertIt(3, 3, 0, 0);

		assertEquals(duplicate, sc.check(3, lost));
		assertIt(3, 3, 0, 0);

		assertEquals(inOrder, sc.check(8, lost));
		assertIt(3, 8, 0, 4);

		assertEquals(duplicate, sc.check(8, lost));
		assertIt(3, 8, 0, 4);

		assertEquals(inOrder, sc.check(15, lost));
		assertIt(3,15, 6, 4);

		assertEquals(duplicate, sc.check(15, lost));
		assertIt(3,15, 0, 4);
	}

	@Test final void testLostStep()
	{
		assertEquals(5, sc.getCapacity());
		assertIt();

		assertEquals(inOrder, sc.check(3, lost));
		assertIt(3, 3, 0, 0);

		assertEquals(inOrder, sc.check(5, lost));
		assertIt(3, 5, 0, 1);

		assertEquals(inOrder, sc.check(8, lost));
		assertIt(3, 8, 0, 3);

		assertEquals(inOrder, sc.check(9, lost));
		assertIt(3, 9, 1, 2);

		assertEquals(inOrder, sc.check(10, lost));
		assertIt(3,10, 0, 2);

		assertEquals(inOrder, sc.check(11, lost));
		assertIt(3,11, 1, 1);

		assertEquals(inOrder, sc.check(12, lost));
		assertIt(3,12, 1, 0);

		assertEquals(inOrder, sc.check(13, lost));
		assertIt(3,13, 0, 0);
	}

	@Test final void testLostJump()
	{
		assertEquals(5, sc.getCapacity());
		assertIt();

		assertEquals(inOrder, sc.check(3, lost));
		assertIt(3, 3, 0, 0);

		assertEquals(inOrder, sc.check(5, lost));
		assertIt(3, 5, 0, 1);

		assertEquals(inOrder, sc.check(8, lost));
		assertIt(3, 8, 0, 3);

		assertEquals(inOrder, sc.check(13, lost));
		assertIt(3,13, 3, 4);
	}

	@Test final void testLostJumpOver()
	{
		assertEquals(5, sc.getCapacity());
		assertIt();

		assertEquals(inOrder, sc.check(3, lost));
		assertIt(3, 3, 0, 0);

		assertEquals(inOrder, sc.check(5, lost));
		assertIt(3, 5, 0, 1);

		assertEquals(inOrder, sc.check(8, lost));
		assertIt(3, 8, 0, 3);

		assertEquals(inOrder, sc.check(16, lost));
		assertIt(3,16, 6, 4);
	}

	@Test final void testLate()
	{
		assertEquals(5, sc.getCapacity());
		assertIt();

		assertEquals(inOrder, sc.check(3, lost));
		assertIt(3, 3, 0, 0);

		assertEquals(inOrder, sc.check(13, lost));
		assertIt(3,13, 5, 4);

		assertEquals(late, sc.check(7, lost));
		assertIt(3,13, 0, 4);

		assertEquals(late, sc.check(8, lost));
		assertIt(3,13, 0, 4);

		assertEquals(outOfOrder, sc.check(9, lost));
		assertIt(3,13, 0, 3);

		assertEquals(outOfOrder, sc.check(10, lost));
		assertIt(3,13, 0, 2);
	}

	@SuppressWarnings("unused")
	@Test void testException()
	{
		assertFails(() ->
			new SequenceChecker2(0),
			IllegalArgumentException.class,
			"capacity must be greater zero, but was 0");
	}

	private void assertIt()
	{
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
		assertEquals(0, lostValue,           "countLost");
		assertEquals(0, sc.getPending(),     "getPending");
		assertEquals(0, sc.countPending(),   "countPending");
	}

	private void assertIt(
			final int firstNumber,
			final int maxNumber,
			final int countLost,
			final int countPending)
	{
		assertTrue(countPending<sc.getCapacity(), String.valueOf(countPending));
		assertEquals(firstNumber,     sc.getFirstNumber(), "firstNumber");
		assertEquals(maxNumber,       sc.getMaxNumber(),   "maxNumber");
		assertEquals(countLost,       lostValue,           "countLost");
		assertEquals(countPending,    sc.getPending(),     "getPending");
		assertEquals(countPending,    sc.countPending(),   "countPending");
		lostValue = 0;
	}
}
