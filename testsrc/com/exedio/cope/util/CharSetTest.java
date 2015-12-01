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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import com.exedio.cope.junit.CopeAssert;
import org.junit.Test;

public class CharSetTest extends CopeAssert
{
	@SuppressWarnings("unused")
	@Test public void inconsistent1()
	{
		try
		{
			new CharSet('Z', 'A');
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("inconsistent character set, character 'A' on position 1 is less character 'Z' on position 0", e.getMessage());
		}
	}
	@SuppressWarnings("unused")
	@Test public void inconsistent2()
	{
		try
		{
			new CharSet('B', 'A');
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("inconsistent character set, character 'A' on position 1 is less character 'B' on position 0", e.getMessage());
		}
	}
	@SuppressWarnings("unused")
	@Test public void inconsistent3()
	{
		try
		{
			new CharSet('A', 'C', 'B', 'A');
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("inconsistent character set, character 'B' on position 2 is less character 'C' on position 1", e.getMessage());
		}
	}
	@SuppressWarnings("unused")
	@Test public void inconsistent4()
	{
		try
		{
			new CharSet('A', 'C', 'N', 'M');
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("inconsistent character set, character 'M' on position 3 is less character 'N' on position 2", e.getMessage());
		}
	}
	@Test public void simple()
	{
		final CharSet cs = new CharSet('C', 'C');
		assertRegexp("^[C]*$", cs);
		assertEquals(cs.toString(), "[C-C]", cs.toString());
		assertFalse(cs.contains('A'));
		assertTrue(cs.contains('C'));
		assertFalse(cs.contains('D'));

		assertEquals( 0, cs.indexOfNotContains("AC"));
		assertEquals( 1, cs.indexOfNotContains("CA"));
		assertEquals(-1, cs.indexOfNotContains("CC"));
		try
		{
			cs.indexOfNotContains(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}
	@Test public void complex()
	{
		final CharSet cs = new CharSet('C', 'C', 'M', 'O', 'm', 'o');
		assertRegexp("^[C,M-O,m-o]*$", cs);
		assertEquals(cs.toString(), "[C-C,M-O,m-o]", cs.toString());
		assertFalse(cs.contains('A'));
		assertTrue(cs.contains('C'));
		assertFalse(cs.contains('D'));
		assertFalse(cs.contains('L'));
		assertTrue(cs.contains('M'));
		assertTrue(cs.contains('O'));
		assertFalse(cs.contains('Q'));
		assertFalse(cs.contains('l'));
		assertTrue(cs.contains('m'));
		assertTrue(cs.contains('o'));
		assertFalse(cs.contains('q'));
	}
	@Test public void isSubsetOfAscii()
	{
		assertEquals(true , new CharSet('a', 'z')     .isSubsetOfAscii());

		assertEquals(true , new CharSet('a', '\u007f').isSubsetOfAscii());
		assertEquals(false, new CharSet('a', '\u0080').isSubsetOfAscii());

		assertEquals(true , new CharSet('A', 'Z', 'a', '\u007f').isSubsetOfAscii());
		assertEquals(false, new CharSet('A', 'Z', 'a', '\u0080').isSubsetOfAscii());

		assertEquals(true , new CharSet('\0', '\u007f').isSubsetOfAscii());
		assertEquals(false, new CharSet('\0', '\u0080').isSubsetOfAscii());
	}
	@Test public void equals()
	{
		assertEqualsStrict(
				new CharSet('A', 'A'),
				new CharSet('A', 'A'));
		assertEqualsStrict(
				new CharSet('A', 'X', 'a', 'x'),
				new CharSet('A', 'X', 'a', 'x'));
		assertNotEqualsStrict(
				new CharSet('A', 'A'),
				new CharSet('A', 'A', 'a', 'x'));
		assertNotEqualsStrict(
				new CharSet('A', 'X', 'a', 'x'),
				new CharSet('A', 'X', 'a', 'y'));
	}
	@Test public void regexp()
	{
		assertRegexp("^[-,a-z]*$", new CharSet('-', '-', 'a', 'z'));
		assertRegexp("^[-,(-)]*$", new CharSet('(', ')', '-', '-'));
		assertRegexp("^[-,(-),0-9]*$", new CharSet('(', ')', '-', '-', '0', '9'));
		assertRegexp("^[[.tab.]-[.newline.],[.carriage-return.],0-9]*$", new CharSet('\t', '\n', '\r', '\r', '0', '9'));
		assertRegexp("^[[.backspace.],0-9]*$", new CharSet('\b', '\b', '0', '9'));
		assertRegexp("^[[.NUL.]]*$", new CharSet('\0', '\0'));
		assertRegexp("^[[.NUL.]- ,0-9]*$", new CharSet('\0', ' ', '0', '9'));
		assertRegexp("^[0-9,[.tilde.]]*$", new CharSet('0', '9', '~', '~'));
		assertRegexp("^[-, -[.tilde.]]*$", new CharSet(' ', '~')); // TODO leading dash is unnecessary
		assertRegexp("^[ ,0-9]*$", new CharSet(' ', ' ', '0', '9'));
		assertRegexp("^[-, -\\u{fc}]*$"  , new CharSet(' ', '\u00fc')); // uuml
		assertRegexp("^[-, -\\u{abcd}]*$", new CharSet(' ', '\uabcd'));
		assertRegexp("^[-, -\\u{ffff}]*$", new CharSet(' ', '\uffff'));
	}

	private static void assertEqualsStrict(final CharSet cs1, final CharSet cs2)
	{
		assertEquals(cs1, cs2);
		assertEquals(cs2, cs1);
		assertEquals(cs1.hashCode(), cs2.hashCode());
	}

	private static void assertNotEqualsStrict(final CharSet cs1, final CharSet cs2)
	{
		assertTrue(!cs1.equals(cs2));
		assertTrue(!cs2.equals(cs1));
		assertTrue(cs1.hashCode()!=cs2.hashCode());
	}

	private static void assertRegexp(final String regularExpression, final CharSet cs)
	{
		final String actual = cs.getRegularExpression();
		assertEquals(actual, regularExpression, actual);
	}
	@Test public void serialize()
	{
		assertEquals(
			new CharSet('A', 'A'), reserialize(
			new CharSet('A', 'A'), 88));
		assertEquals(
			new CharSet('0', '9', 'A', 'Z', 'a', 'z'), reserialize(
			new CharSet('0', '9', 'A', 'Z', 'a', 'z'), 96));
	}
}
