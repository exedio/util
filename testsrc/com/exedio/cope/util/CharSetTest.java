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
			assertEquals("inconsistent character set, character 'A' on position 1 is less than character 'Z' on position 0", e.getMessage());
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
			assertEquals("inconsistent character set, character 'A' on position 1 is less than character 'B' on position 0", e.getMessage());
		}
	}
	@SuppressWarnings("unused")
	@Test public void inconsistent3()
	{
		try
		{
			new CharSet('A', 'C', 'B', 'X');
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("inconsistent character set, the character area extending to 'C' on position 1 overlaps with the area starting with character 'B' on position 2", e.getMessage());
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
			assertEquals("inconsistent character set, character 'M' on position 3 is less than character 'N' on position 2", e.getMessage());
		}
	}
	@Test public void inconsistentOverlapping()
	{
		try
		{
			new CharSet('A', 'C', 'C', 'F');
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("inconsistent character set, the character area extending to 'C' on position 1 overlaps with the area starting with character 'C' on position 2", e.getMessage());
		}
	}
	@Test public void inconsistentNoDistance()
	{
		try
		{
			new CharSet('A', 'C', 'D', 'F');
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("inconsistent character set, no distance between character 'C' on position 1 and character 'D' on position 2", e.getMessage());
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
		assertRegexp("^[[.left-square-bracket.]-[.right-square-bracket.]]*$", new CharSet('[', ']'));
		assertRegexp("^[-,[.comma.]-.]*$", new CharSet(',', '.'));
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

	@Test public void emailInvalid()
	{
		final String invalid = "\n\t \"(),:;<>[\\]";
		assertEquals(invalid, getInvalidCharacters(CharSet.EMAIL_RESTRICTIVE, invalid));
		assertEquals(invalid, getInvalidCharacters(CharSet.EMAIL_ASCII, invalid));
		assertEquals(invalid, getInvalidCharacters(CharSet.EMAIL_INTERNATIONAL, invalid));
	}

	@Test public void emailValid()
	{
		assertEquals("!#$%&'*+/=?^`{|}~", getInvalidCharacters(CharSet.EMAIL_RESTRICTIVE, "AZaz0123456789!#$%&'*+-/=?^_`.{|}~"));
		assertEquals("", getInvalidCharacters(CharSet.EMAIL_ASCII, "AZaz0123456789!#$%&'*+-/=?^_`.{|}~"));
		assertEquals("", getInvalidCharacters(CharSet.EMAIL_INTERNATIONAL, "AZaz0123456789!#$%&'*+-/=?^_`.{|}~"));
	}

	@Test public void emailInternationalCharacters()
	{
		final String international = "\u00e4\u00f6\u00fc\u00c4\u00d6\u00dc\u00df\u20ac\u00e0\u00e8\u00e9\u00ea\u0436";
		assertEquals(international, getInvalidCharacters(CharSet.EMAIL_RESTRICTIVE, international));
		assertEquals(international, getInvalidCharacters(CharSet.EMAIL_ASCII, international));
		assertEquals("", getInvalidCharacters(CharSet.EMAIL_INTERNATIONAL, international));
	}

	@Test public void emailNonUtf16()
	{
		final String nonUtf16 = new String(Character.toChars(0x1F600));
		assertEquals(nonUtf16, getInvalidCharacters(CharSet.EMAIL_RESTRICTIVE, nonUtf16));
		assertEquals(nonUtf16, getInvalidCharacters(CharSet.EMAIL_ASCII, nonUtf16));
		assertEquals("", getInvalidCharacters(CharSet.EMAIL_INTERNATIONAL, nonUtf16));
	}

	private static String getInvalidCharacters(final CharSet charSet, final String s)
	{
		final StringBuilder sb = new StringBuilder();
		for (int i=0; i<s.length(); i++)
		{
			final char c = s.charAt(i);
			if (!charSet.contains(c))
			{
				sb.append(c);
			}
		}
		return sb.toString();
	}

	@Test public void invert()
	{
		assertEquals(null, new CharSet('\0', '\uffff').invert());
		assertEquals(new CharSet('G', '\uffff'), new CharSet('\0', 'F').invert());
		assertEquals(new CharSet('\0', 'E'), new CharSet('F', '\uffff').invert());
		assertEquals(new CharSet('\0', 'A', 'Z', '\uffff'), new CharSet('B', 'Y').invert());
		assertEquals(new CharSet('\0', '3', '5', 'A', 'Z', '\uffff'), new CharSet('4', '4', 'B', 'Y').invert());
	}

	@Test public void restrict()
	{
		assertEquals(new CharSet('a', 'z'), new CharSet('a', 'z').restrictTo7BitAscii());
		assertEquals(new CharSet('a', 'f', 'h', 'm'), new CharSet('a', 'f', 'h', 'm').restrictTo7BitAscii());
		assertEquals(new CharSet('\0', '\u007f'), new CharSet('\0', '\uffff').restrictTo7BitAscii());
		assertEquals(new CharSet('0', '9', 'A', 'Z'), new CharSet('0', '9', 'A', 'Z', '\u00e4', '\u00e4').restrictTo7BitAscii());
		assertEquals(new CharSet('0', '9', 'A', '\u007f'), new CharSet('0', '9', 'A', '\u00e4', '\u00f6', '\u00fc').restrictTo7BitAscii());
		assertEquals(CharSet.EMAIL_ASCII, CharSet.EMAIL_INTERNATIONAL.restrictTo7BitAscii());
		assertEquals(null, new CharSet('\u00e4', '\u00f6').restrictTo7BitAscii());
	}

	@Test public void invertedRegexp()
	{
		assertEquals("[-,[.NUL.]-/,{-\u007f]", new CharSet('0', 'z').getRegularExpressionForInvalid7BitChars());
		assertEquals("[[.NUL.]- ,\",(-),[.comma.],:-<,>,[.left-square-bracket.]-[.right-square-bracket.],\u007f]", CharSet.EMAIL_INTERNATIONAL.getRegularExpressionForInvalid7BitChars());
	}

	@Test public void remove()
	{
		assertEquals(new CharSet('a', 'c'), new CharSet('a', 'c').remove('d', 'f'));
		assertEquals(new CharSet('d', 'f'), new CharSet('d', 'f').remove('a', 'c'));
		assertEquals(new CharSet('a', 'a', 'c', 'c'), new CharSet('a', 'c').remove('b', 'b'));
		assertEquals(null, new CharSet('a', 'c').remove('a', 'c'));
		assertEquals(new CharSet('k', 's'), new CharSet('f', 's').remove('a', 'j'));
		assertEquals(new CharSet('a', 'e'), new CharSet('a', 'j').remove('f', 's'));
		assertEquals(new CharSet('0', '1', '8', '9'), new CharSet('0', '1', '3', '6', '8', '9').remove('3', '6'));
		assertEquals(new CharSet('0', '1', '8', '9'), new CharSet('0', '1', '3', '6', '8', '9').remove('2', '7'));
		assertEquals(new CharSet('0', '0', '9', '9'), new CharSet('0', '1', '3', '6', '8', '9').remove('1', '8'));
		assertEquals(null, new CharSet('0', '1', '3', '6', '8', '9').remove('0', '9'));
	}
}
