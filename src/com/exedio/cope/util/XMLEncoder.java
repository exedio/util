/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

public final class XMLEncoder
{
	private XMLEncoder()
	{
		// forbid instantiation
	}

	public static String encode(final String st)
	{
		if(st==null)
			return null;

		StringBuilder bf = null;
		final int length = st.length();
		int lastPos = 0;
		for(int pos = 0; pos<length; pos++)
		{
			final char c = st.charAt(pos);
			final String replacement;
			switch(c)
			{
				case '&': replacement = "&amp;" ; break;
				case '<': replacement = "&lt;"  ; break;
				case '>': replacement = "&gt;"  ; break;
				case '"': replacement = "&quot;"; break;
				case '\'':replacement = "&apos;"; break;
				default:
					continue;
			}
			if(bf==null)
				bf = new StringBuilder();
			if(lastPos<pos)
				bf.append(st.substring(lastPos, pos));
			bf.append(replacement);
			lastPos = pos + 1;
		}
		if(bf==null)
			return st;
		if(lastPos<length)
			bf.append(st.substring(lastPos, length));
		return bf.toString();
	}

	public static void append(final StringBuilder bf, final String st)
	{
		final int length = st.length();
		int lastPos = 0;
		for(int pos = 0; pos<length; pos++)
		{
			final char c = st.charAt(pos);
			final String replacement;
			switch(c)
			{
				case '&': replacement = "&amp;" ; break;
				case '<': replacement = "&lt;"  ; break;
				case '>': replacement = "&gt;"  ; break;
				case '"': replacement = "&quot;"; break;
				case '\'':replacement = "&apos;"; break;
				default:
					continue;
			}
			if(lastPos<pos)
				bf.append(st.substring(lastPos, pos));
			bf.append(replacement);
			lastPos = pos + 1;
		}
		if(lastPos<length)
			bf.append(st.substring(lastPos, length));
	}

	public static void append(final StringBuilder bf, final char c)
	{
		final String replacement;
		switch(c)
		{
			case '&': replacement = "&amp;" ; break;
			case '<': replacement = "&lt;"  ; break;
			case '>': replacement = "&gt;"  ; break;
			case '"': replacement = "&quot;"; break;
			case '\'':replacement = "&apos;"; break;
			default:
				bf.append(c);
				return;
		}
		bf.append(replacement);
	}
}
