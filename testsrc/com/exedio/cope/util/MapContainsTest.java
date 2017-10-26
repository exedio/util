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

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.junit.jupiter.api.Test;

/**
 * Characterization test for EnumMap and containsKey.
 */
public class MapContainsTest
{
	@Test void hashMap()
	{
		assertMap(
				new HashMap<>(),
				"alpha", "beta", "nullKey", "notAdded");
	}
	@Test void treeMap()
	{
		assertMap(
				new TreeMap<>(),
				"alpha", "beta", "nullKey", "notAdded");
	}
	@SuppressWarnings("MapReplaceableByEnumMap")
	@Test void hashMapOfEnum()
	{
		assertMap(
				new HashMap<>(),
				KeyEnum.alpha, KeyEnum.beta, KeyEnum.nullKey, KeyEnum.notAdded);
	}
	@Test void enumMap()
	{
		assertMap(
				new EnumMap<>(KeyEnum.class),
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
