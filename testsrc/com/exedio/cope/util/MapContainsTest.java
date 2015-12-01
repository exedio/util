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

import static junit.framework.Assert.assertEquals;

import com.exedio.cope.junit.CopeAssert;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.junit.Test;

/**
 * Characterization test for EnumMap and containsKey.
 */
public class MapContainsTest extends CopeAssert
{
	@Test public void hashMap()
	{
		assertMap(
				new HashMap<String, String>(),
				"alpha", "beta", "nullKey", "notAdded");
	}
	@Test public void treeMap()
	{
		assertMap(
				new TreeMap<String, String>(),
				"alpha", "beta", "nullKey", "notAdded");
	}
	@Test public void hashMapOfEnum()
	{
		assertMap(
				new HashMap<KeyEnum, String>(),
				KeyEnum.alpha, KeyEnum.beta, KeyEnum.nullKey, KeyEnum.notAdded);
	}
	@Test public void enumMap()
	{
		assertMap(
				new EnumMap<KeyEnum,String>(KeyEnum.class),
				KeyEnum.alpha, KeyEnum.beta, KeyEnum.nullKey, KeyEnum.notAdded);
	}

	private static <K> void assertMap(
			final Map<K,String> map,
			final K alpha, final K beta,
			final K notAdded, final K nullKey)
	{
		map.put(alpha,   "alphaV");
		map.put(beta,    "betaV");
		map.put(nullKey, null);

		assertEquals("alphaV", map.get(alpha));
		assertEquals("betaV",  map.get(beta));
		assertEquals(null,     map.get(nullKey));
		assertEquals(null,     map.get(notAdded));

		assertEquals(true,  map.containsKey(alpha));
		assertEquals(true,  map.containsKey(beta));
		assertEquals(true,  map.containsKey(nullKey));
		assertEquals(false, map.containsKey(notAdded));
	}

	enum KeyEnum{ alpha, beta, nullKey, notAdded }
}