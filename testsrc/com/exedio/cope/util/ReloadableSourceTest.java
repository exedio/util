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
import static com.exedio.cope.util.Sources.reloadable;
import static com.exedio.cope.util.Sources.view;
import static java.util.Arrays.asList;
import static java.util.Locale.ENGLISH;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.util.junit.ClockRule;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ClockRule.Extension.class)
public class ReloadableSourceTest
{
	@Test void test(final ClockRule clock)
	{
		final Properties properties = new Properties();
		properties.setProperty("keyA", "valueA");
		properties.setProperty("keyB", "valueB");
		final Source s = view(properties, "description");
		final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z (Z)", ENGLISH);

		final long reload1Millis = 514857711111l; // 1986-04-26 02:01:51.111 CEST (+0200)
		clock.override(() -> reload1Millis);
		final Source reloaded1 = reloadable(() -> s);
		assertEquals("valueA", reloaded1.get("keyA"));
		assertEquals("valueB", reloaded1.get("keyB"));
		assertEquals(null, reloaded1.get("keyX"));
		assertEquals(new HashSet<>(asList("keyA", "keyB")), reloaded1.keySet());
		assertEquals("description(" + df.format(new Date(reload1Millis)) + ")", reloaded1.getDescription());
		assertEquals("description(reloadable)", reloaded1.toString());

		final long reload2Millis = 549072422222l; // 1987-05-27 02:07:02.222 CEST (+0200)
		clock.override(() -> reload2Millis);
		final Source reloaded2 = reloaded1.reload();
		assertEquals("valueA", reloaded2.get("keyA"));
		assertEquals("valueB", reloaded2.get("keyB"));
		assertEquals(null, reloaded2.get("keyX"));
		assertEquals(new HashSet<>(asList("keyA", "keyB")), reloaded2.keySet());
		assertEquals("description(" + df.format(new Date(reload2Millis)) + " reload 1)", reloaded2.getDescription());
		assertEquals("description(reloadable)", reloaded2.toString());
	}

	@Test void testSupplierNull()
	{
		assertFails(
				() -> reloadable(null),
				NullPointerException.class,
				"Cannot invoke \"java.util.function.Supplier.get()\" " +
				"because \"sourceSupplier\" is null");
	}

	@Test void testSupplierResultNull()
	{
		assertFails(
				() -> reloadable(() -> null),
				NullPointerException.class, "target");
	}
}
