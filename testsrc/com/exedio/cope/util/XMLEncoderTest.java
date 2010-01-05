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

import junit.framework.TestCase;

public class XMLEncoderTest extends TestCase
{
	private static final void assertIt(final String expected, final String actual)
	{
		assertEquals(expected, XMLEncoder.encode(actual));
	}
	
	public void testEncode()
	{
		assertIt(null, null);
		assertIt("", "");
		assertIt("x", "x");
		assertIt("&lt;", "<");
		assertIt("&gt;", ">");
		assertIt("&quot;", "\"");
		assertIt("&apos;", "'");
		assertIt("&amp;", "&");
		assertIt("&apos;tralla&quot;", "'tralla\"");
		assertIt("&gt;kno&amp;llo&lt;", ">kno&llo<");
	}
}
