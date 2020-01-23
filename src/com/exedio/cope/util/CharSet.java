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

import static java.lang.Character.isDefined;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CharSet implements Serializable
{
	// TODO deserialization should not duplicate predefined charsets
	private static final long serialVersionUID = 1l;

	public static final CharSet ALPHA = new CharSet('A', 'Z', 'a', 'z');
	public static final CharSet ALPHA_UPPER = new CharSet('A', 'Z');
	public static final CharSet ALPHA_LOWER = new CharSet('a', 'z');
	public static final CharSet ALPHA_NUMERIC = new CharSet('0', '9', 'A', 'Z', 'a', 'z');
	public static final CharSet ALPHA_UPPER_NUMERIC = new CharSet('0', '9', 'A', 'Z');
	public static final CharSet ALPHA_LOWER_NUMERIC = new CharSet('0', '9', 'a', 'z');
	public static final CharSet HEX_UPPER = new CharSet('0', '9', 'A', 'F');
	public static final CharSet HEX_LOWER = new CharSet('0', '9', 'a', 'f');
	public static final CharSet NUMERIC = new CharSet('0', '9');
	public static final CharSet DOMAIN = new CharSet('-', '.', '0', '9', 'a', 'z');
	/**
	 * allows only characters commonly used in email addresses; this is the same character set as previously available
	 * as {@code CharSet.EMAIL}; please note that valid characters like + and &amp; are not allowed in this charset
	 */
	public static final CharSet EMAIL_RESTRICTIVE = new CharSet('-', '.', '0', '9', '@', 'Z', '_', '_', 'a', 'z');
	/** allows only 7bit ASCII email characters (RFC5322/5321 without quoted strings and comments) */
	public static final CharSet EMAIL_ASCII  = new CharSet('!', '!', '#', '\'', '*', '+', '-', '9', '=', '=', '?', 'Z', '^', '~');
	/** {@link #EMAIL_ASCII} plus all characters beyond 7bit ASCII */
	public static final CharSet EMAIL_INTERNATIONAL = new CharSet('!', '!', '#', '\'', '*', '+', '-', '9', '=', '=', '?', 'Z', '^', '~', '\u0080', '\uffff');
	/** @deprecated use one of {@link #EMAIL_RESTRICTIVE}, {@link #EMAIL_ASCII}, or {@link #EMAIL_INTERNATIONAL} instead */
	@Deprecated
	public static final CharSet EMAIL = EMAIL_RESTRICTIVE;

	private final char[] set;

	public CharSet(final char from, final char to)
	{
		this(new char[]{from, to});
	}

	public CharSet(final char from1, final char to1, final char from2, final char to2)
	{
		this(new char[]{from1, to1, from2, to2});
	}

	public CharSet(final char from1, final char to1, final char from2, final char to2, final char from3, final char to3)
	{
		this(new char[]{from1, to1, from2, to2, from3, to3});
	}

	public CharSet(final char from1, final char to1, final char from2, final char to2, final char from3, final char to3, final char from4, final char to4)
	{
		this(new char[]{from1, to1, from2, to2, from3, to3, from4, to4});
	}

	public CharSet(final char from1, final char to1, final char from2, final char to2, final char from3, final char to3, final char from4, final char to4, final char from5, final char to5)
	{
		this(new char[]{from1, to1, from2, to2, from3, to3, from4, to4, from5, to5});
	}

	public CharSet(final char from1, final char to1, final char from2, final char to2, final char from3, final char to3, final char from4, final char to4, final char from5, final char to5, final char from6, final char to6)
	{
		this(new char[]{from1, to1, from2, to2, from3, to3, from4, to4, from5, to5, from6, to6});
	}

	public CharSet(final char from1, final char to1, final char from2, final char to2, final char from3, final char to3, final char from4, final char to4, final char from5, final char to5, final char from6, final char to6, final char from7, final char to7)
	{
		this(new char[]{from1, to1, from2, to2, from3, to3, from4, to4, from5, to5, from6, to6, from7, to7});
	}

	public CharSet(final char from1, final char to1, final char from2, final char to2, final char from3, final char to3, final char from4, final char to4, final char from5, final char to5, final char from6, final char to6, final char from7, final char to7, final char from8, final char to8)
	{
		this(new char[]{from1, to1, from2, to2, from3, to3, from4, to4, from5, to5, from6, to6, from7, to7, from8, to8});
	}

	private CharSet(final char... set)
	{
		assert set.length%2==0;

		char endOfLastArea = 0;
		for(int i = 0; i<set.length; i+=2)
		{
			final char areaStart = set[i];
			final char areaEnd = set[i+1];
			if (areaStart>areaEnd)
				throw new IllegalArgumentException("inconsistent character set, character '" + areaEnd + "' on position " + (i+1) + " is less than character '" + areaStart + "' on position " + i);
			if (i>0)
			{
				if (endOfLastArea>=areaStart)
					throw new IllegalArgumentException("inconsistent character set, the character area extending to '" + endOfLastArea + "' on position " + (i-1) + " overlaps with the area starting with character '" + areaStart + "' on position " + i);
				if (endOfLastArea+1==areaStart)
					throw new IllegalArgumentException("inconsistent character set, no distance between character '" + endOfLastArea + "' on position " + (i-1) + " and character '" + areaStart + "' on position " + i);
			}
			endOfLastArea = areaEnd;
		}

		this.set = set;
	}

	public boolean isSubsetOfAscii()
	{
		return set[set.length-1]<=127;
	}

	public boolean contains(final char c)
	{
		for(int i = 0; i<set.length; i+=2)
		{
			if(set[i]>c)
				continue;
			if(set[i+1]>=c)
				return true;
		}
		return false;
	}

	/**
	 * Returns the index of the first character in s,
	 * which this CharSet does not contain.
	 * Returns -1, if this CharSet contains all characters in s.
	 * <p>
	 * This method is provided for binary backwards compatibility.
	 * It is equivalent to {@link #indexOfNotContains(CharSequence)}.
	 */
	public int indexOfNotContains(final String s)
	{
		return indexOfNotContains((CharSequence)s);
	}

	/**
	 * Returns the index of the first character in s,
	 * which this CharSet does not contain.
	 * Returns -1, if this CharSet contains all characters in s.
	 */
	public int indexOfNotContains(final CharSequence s)
	{
		final int l = s.length();
		for(int i = 0; i<l; i++)
			if(!contains(s.charAt(i)))
				return i;
		return -1;
	}

	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof CharSet))
			return false;

		return Arrays.equals(set, ((CharSet)other).set);
	}

	@Override
	public int hashCode()
	{
		return Arrays.hashCode(set);
	}

	@Override
	public String toString()
	{
		final StringBuilder bf = new StringBuilder();
		bf.append('[');
		toString(bf, set[0]);
		bf.append('-');
		toString(bf, set[1]);
		for(int i = 2; i<set.length; i+=2)
		{
			bf.append(',');
			toString(bf, set[i]);
			bf.append('-');
			toString(bf, set[i+1]);
		}
		bf.append(']');

		return bf.toString();
	}

	private static void toString(final StringBuilder bf, final char c)
	{
		if(isDefined(c))
			bf.append(c);
		else
			bf.append("nd{").
				append(Integer.toHexString(c)).
				append('}');
	}

	public CharSet remove(final char from, final char to)
	{
		if (from>to) throw new IllegalArgumentException(from+">"+to);
		final List<char[]> areas = new ArrayList<>();
		for (int i=0; i<set.length; i+=2)
		{
			final char setFrom = set[i];
			final char setTo = set[i+1];
			if (from<=setFrom && to>=setTo)
			{
				// removal area includes all at this area
				// drop
				continue;
			}
			else if (to<setFrom || from>setTo)
			{
				// removal area and this area don't overlap
				areas.add(new char[]{setFrom, setTo});
				continue;
			}
			if (setFrom<=from-1)
				areas.add(new char[]{setFrom, (char)(from-1)});
			if (to+1<=setTo)
				areas.add(new char[]{(char)(to+1), setTo});
		}
		if (areas.isEmpty())
			return null;
		final char[] newSet = new char[areas.size()*2];
		int i=0;
		for (final char[] area: areas)
		{
			newSet[i++] = area[0];
			newSet[i++] = area[1];
		}
		if (i!=newSet.length) throw new RuntimeException();
		return new CharSet(newSet);
	}

	CharSet invert()
	{
		final char[] temp = new char[set.length+2];
		int invertIndex = 0;
		if (set[0]!='\0')
		{
			temp[invertIndex++] = '\0';
			temp[invertIndex++] = (char) (set[0]-1);
		}
		for(int i = 1; i<set.length-1; i+=2)
		{
			temp[invertIndex++] = (char) (set[i]+1);
			temp[invertIndex++] = (char) (set[i+1]-1);
		}
		if (set[set.length-1]!='\uFFFF')
		{
			temp[invertIndex++] = (char) (set[set.length-1]+1);
			temp[invertIndex++] = '\uFFFF';
		}
		if (invertIndex==0)
		{
			return null;
		}
		else if (invertIndex==temp.length)
		{
			return new CharSet(temp);
		}
		else
		{
			final char[] cut = Arrays.copyOf(temp, invertIndex);
			return new CharSet(cut);
		}
	}

	CharSet restrictTo7BitAscii()
	{
		return remove('\u0080', '\uFFFF');
	}

	public String getRegularExpression()
	{
		// ^[0-9,a-z,A-Z]*$
		return "^" + getRegularExpressionCharacterClass() + "*$";
	}

	private StringBuilder getRegularExpressionCharacterClass()
	{
		// [0-9,a-z,A-Z]
		final StringBuilder bf = new StringBuilder();
		bf.append("[");

		boolean prependComma = false;

		if(contains('-'))
		{
			bf.append('-');
			prependComma = true;
		}

		for(int i = 0; i<set.length; i+=2)
		{
			char from = set[i];
			char to = set[i+1];

			if(from=='-')
				from++;
			if(to=='-')
				to--;

			if(from<=to)
			{
				if(prependComma)
					bf.append(',');

				if(from==to)
					append(bf, from);
				else // from<to
				{
					append(bf, from);
					bf.append('-');
					append(bf, to);
				}

				prependComma = true;
			}
		}

		bf.append("]");
		return bf;
	}

	public String getRegularExpressionForInvalid7BitChars()
	{
		final CharSet inverted = invert();
		if (inverted==null)
			return null;
		final CharSet restricted = inverted.restrictTo7BitAscii();
		if (restricted==null)
			return null;
		return restricted.getRegularExpressionCharacterClass().toString();
	}

	@SuppressWarnings("HardcodedLineSeparator")
	private static void append(final StringBuilder bf, final char c)
	{
		switch(c)
		{
			// see https://dev.mysql.com/doc/refman/5.5/en/regexp.html
			case    0: bf.append("[.NUL.]");             break;
			case '\t': bf.append("[.tab.]");             break;
			case '\n': bf.append("[.newline.]");         break;
			case '\r': bf.append("[.carriage-return.]"); break;
			case '\b': bf.append("[.backspace.]");       break;
			case   11: bf.append("[.vertical-tab.]");    break;
			case   12: bf.append("[.form-feed.]");       break;
			case   14: bf.append("[.SO.]");              break;
			case   31: bf.append("[.US.]");              break;
			case  '~': bf.append("[.tilde.]");           break;
			case  ',': bf.append("[.comma.]");           break;
			case  '[': bf.append("[.left-square-bracket.]"); break;
			case  ']': bf.append("[.right-square-bracket.]"); break;
			default:
			{
				if(c<' ' || c>127)
					bf.append("\\u{").append(Integer.toHexString(c)).append('}');
				else
					bf.append(c);

				break;
			}
		}
	}
}
