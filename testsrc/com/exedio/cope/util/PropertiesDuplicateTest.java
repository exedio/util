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

import com.exedio.cope.junit.CopeAssert;

@edu.umd.cs.findbugs.annotations.SuppressWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON") // is more compact to write in tests
public class PropertiesDuplicateTest extends CopeAssert
{
	@edu.umd.cs.findbugs.annotations.SuppressWarnings("URF_UNREAD_FIELD") // is read by reflection
	static class DuplicateProperties extends Properties
	{
		final BooleanField duplicate1 = new BooleanField("duplicate", false);
		final BooleanField duplicate2 = new BooleanField("duplicate", true);

		DuplicateProperties()
		{
			super(Properties.EMPTY_SOURCE, null);
		}
	}

	@SuppressWarnings("unused")
	public void testDuplicate()
	{
		try
		{
			new DuplicateProperties();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("duplicate key 'duplicate'", e.getMessage());
		}
	}
}