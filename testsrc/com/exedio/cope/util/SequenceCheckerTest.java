/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.junit.CopeAssert;

public class SequenceCheckerTest extends CopeAssert
{
	SequenceChecker sc;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		sc = new SequenceChecker(5);
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		sc = null;
		super.tearDown();
	}
	
	public void testInOrder()
	{
		assertEquals(5, sc.getLength());
		assertIt();
		
		sc.check(3);
		assertIt(3, 3, 1, 0, 0, 0, 0);
		
		sc.check(4);
		assertIt(3, 4, 2, 0, 0, 0, 0);
		
		sc.check(5);
		assertIt(3, 5, 3, 0, 0, 0, 0);
		
		sc.check(6);
		assertIt(3, 6, 4, 0, 0, 0, 0);
		
		sc.check(7);
		assertIt(3, 7, 5, 0, 0, 0, 0);
		
		sc.check(8);
		assertIt(3, 8, 6, 0, 0, 0, 0);
		
		sc.check(9);
		assertIt(3, 9, 7, 0, 0, 0, 0);
		
		sc.check(10);
		assertIt(3,10, 8, 0, 0, 0, 0);
		
		sc.check(2);
		assertIt(3,10, 8, 0, 0, 0, 0);
		
		sc.check(Integer.MIN_VALUE);
		assertIt(3,10, 8, 0, 0, 0, 0);
	}
	
	public void testOutOfOrder()
	{
		assertEquals(5, sc.getLength());
		assertIt();
		
		sc.check(3);
		assertIt(3, 3, 1, 0, 0, 0, 0);
		
		sc.check(5);
		assertIt(3, 5, 2, 0, 0, 0, 0);
		
		sc.check(4);
		assertIt(3, 5, 2, 1, 0, 0, 0);
		
		sc.check(7);
		assertIt(3, 7, 3, 1, 0, 0, 0);
		
		sc.check(6);
		assertIt(3, 7, 3, 2, 0, 0, 0);
		
		sc.check(9);
		assertIt(3, 9, 4, 2, 0, 0, 0);
		
		sc.check(8);
		assertIt(3, 9, 4, 3, 0, 0, 0);
		
		sc.check(10);
		assertIt(3,10, 5, 3, 0, 0, 0);
	}
	
	public void testDuplicate()
	{
		assertEquals(5, sc.getLength());
		assertIt();
		
		sc.check(3);
		assertIt(3, 3, 1, 0, 0, 0, 0);
		
		sc.check(3);
		assertIt(3, 3, 1, 0, 1, 0, 0);
		
		sc.check(8);
		assertIt(3, 8, 2, 0, 1, 0, 0);
		
		sc.check(8);
		assertIt(3, 8, 2, 0, 2, 0, 0);
		
		sc.check(15);
		assertIt(3,15, 3, 0, 2, 6, 0);
		
		sc.check(15);
		assertIt(3,15, 3, 0, 3, 6, 0);
	}
	
	public void testLostStep()
	{
		assertEquals(5, sc.getLength());
		assertIt();
		
		sc.check(3);
		assertIt(3, 3, 1, 0, 0, 0, 0);
		
		sc.check(5);
		assertIt(3, 5, 2, 0, 0, 0, 0);
		
		sc.check(8);
		assertIt(3, 8, 3, 0, 0, 0, 0);
		
		sc.check(9);
		assertIt(3, 9, 4, 0, 0, 1, 0);
		
		sc.check(10);
		assertIt(3,10, 5, 0, 0, 1, 0);
		
		sc.check(11);
		assertIt(3,11, 6, 0, 0, 2, 0);
		
		sc.check(12);
		assertIt(3,12, 7, 0, 0, 3, 0);
		
		sc.check(13);
		assertIt(3,13, 8, 0, 0, 3, 0);
	}
	
	public void testLostJump()
	{
		assertEquals(5, sc.getLength());
		assertIt();
		
		sc.check(3);
		assertIt(3, 3, 1, 0, 0, 0, 0);
		
		sc.check(5);
		assertIt(3, 5, 2, 0, 0, 0, 0);
		
		sc.check(8);
		assertIt(3, 8, 3, 0, 0, 0, 0);
		
		sc.check(13);
		assertIt(3,13, 4, 0, 0, 3, 0);
	}
	
	public void testLostJumpOver()
	{
		assertEquals(5, sc.getLength());
		assertIt();
		
		sc.check(3);
		assertIt(3, 3, 1, 0, 0, 0, 0);
		
		sc.check(5);
		assertIt(3, 5, 2, 0, 0, 0, 0);
		
		sc.check(8);
		assertIt(3, 8, 3, 0, 0, 0, 0);
		
		sc.check(16);
		assertIt(3,16, 4, 0, 0, 6, 0);
	}
	
	public void testLate()
	{
		assertEquals(5, sc.getLength());
		assertIt();
		
		sc.check(3);
		assertIt(3, 3, 1, 0, 0, 0, 0);
		
		sc.check(13);
		assertIt(3,13, 2, 0, 0, 5, 0);
		
		sc.check(7);
		assertIt(3,13, 2, 0, 0, 5, 1);
		
		sc.check(8);
		assertIt(3,13, 2, 0, 0, 5, 2);
		
		sc.check(9);
		assertIt(3,13, 2, 1, 0, 5, 2);
		
		sc.check(10);
		assertIt(3,13, 2, 2, 0, 5, 2);
	}
	
	private void assertIt()
	{
		try
		{
			sc.getFirstNumber();
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("did not yet check first number", e.getMessage());
		}
		try
		{
			sc.getMaxNumber();
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("did not yet check first number", e.getMessage());
		}
		assertEquals("countInOrder",    0, sc.getCountInOrder());
		assertEquals("countOutOfOrder", 0, sc.getCountOutOfOrder());
		assertEquals("countDuplicate",  0, sc.getCountDuplicate());
		assertEquals("countLost",       0, sc.getCountLost());
		assertEquals("countLate",       0, sc.getCountLate());
	}
	
	private void assertIt(
			final int firstNumber,
			final int maxNumber,
			final int countInOrder,
			final int countOutOfOrder,
			final int countDuplicate,
			final int countLost,
			final int countLate)
	{
		assertEquals("firstNumber",     firstNumber,     sc.getFirstNumber());
		assertEquals("maxNumber",       maxNumber,       sc.getMaxNumber());
		assertEquals("countInOrder",    countInOrder,    sc.getCountInOrder());
		assertEquals("countOutOfOrder", countOutOfOrder, sc.getCountOutOfOrder());
		assertEquals("countDuplicate",  countDuplicate,  sc.getCountDuplicate());
		assertEquals("countLost",       countLost,       sc.getCountLost());
		assertEquals("countLate",       countLate,       sc.getCountLate());
	}
}
