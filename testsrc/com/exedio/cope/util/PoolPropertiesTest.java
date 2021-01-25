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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class PoolPropertiesTest
{
	@Test void testDefault()
	{
		final PoolProperties p = PoolProperties.factory(55).create(Sources.EMPTY);
		assertEquals(0,  p.getIdleInitial());
		assertEquals(55, p.getIdleLimit());
	}
	@Test void testSet()
	{
		final PoolProperties p = PoolProperties.factory(55).create(new AssertionErrorPropertiesSource()
		{
			@Override
			public String get(final String key)
			{
				switch(key)
				{
					case "idleInitial": return "22";
					case "idleLimit":   return "33";
				}
				return super.get(key);
			}
			@Override
			public String getDescription()
			{
				return "DESC";
			}
		});
		assertEquals(22, p.getIdleInitial());
		assertEquals(33, p.getIdleLimit());
	}
}
