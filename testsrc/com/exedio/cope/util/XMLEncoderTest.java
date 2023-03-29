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

public class XMLEncoderTest
{
	private static void assertIt(final String expected, final String actual)
	{
		assertEquals(expected, XMLEncoder.encode(actual));
		final StringBuilder bf = new StringBuilder();
		XMLEncoder.append(bf, actual);
		assertEquals(expected, bf.toString());
		if(actual.length()==1)
		{
			bf.setLength(0);
			XMLEncoder.append(bf, actual.charAt(0));
			assertEquals(expected, bf.toString());
		}
	}

	@Test void testEncode()
	{
		assertEquals(null, XMLEncoder.encode(null));
		assertIt("", "");
		assertIt("x", "x");
		assertIt("&lt;", "<");
		assertIt("&gt;", ">");
		assertIt("&quot;", "\"");
		assertIt("&apos;", "'");
		assertIt("&amp;", "&");
		assertIt("&apos;tralla&quot;", "'tralla\"");
		assertIt("&gt;kno&amp;llo&lt;", ">kno&llo<");
		assertIt("&amp;&amp;&amp;&amp;&amp;", "&&&&&");
		assertIt("&amp;x&amp;x&amp;x&amp;x&amp;", "&x&x&x&x&");

		//noinspection DataFlowIssue
		assertFails(() ->
			XMLEncoder.append(null, null),
			NullPointerException.class, null);
		XMLEncoder.append(null, ""); // TODO should throw a NullPointerException
		assertFails(() ->
			XMLEncoder.append(null, "x"),
			NullPointerException.class, null);
		//noinspection DataFlowIssue
		assertFails(() ->
			XMLEncoder.append(new StringBuilder(), null),
			NullPointerException.class, null);
		assertFails(() ->
			XMLEncoder.append(null, 'x'),
			NullPointerException.class, null);
		assertFails(() ->
			XMLEncoder.append(null, '&'),
			NullPointerException.class, null);
	}
}
