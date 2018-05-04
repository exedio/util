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
import static com.exedio.cope.util.Sources.view;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.util.Properties.Source;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.HashSet;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

@SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON")
public class ProxySourceTest
{
	@Test void test()
	{
		final Properties properties = new Properties();
		properties.setProperty("keyA", "valueA");
		properties.setProperty("keyB", "valueB");
		final Source target = view(properties, "description");
		final ProxyPropertiesSource proxy = new Proxy(target);

		assertSame(target, proxy.getTarget());
		assertEquals("valueA", proxy.get("keyA"));
		assertEquals("valueB", proxy.get("keyB"));
		assertEquals(null, proxy.get("keyX"));
		assertEquals(new HashSet<>(asList("keyA", "keyB")), proxy.keySet());
		assertSame(proxy, proxy.reload());
		assertEquals("description", proxy.getDescription());
		assertEquals("description", proxy.toString());
	}

	@Test void testTargetNull()
	{
		assertFails(
				() -> new Proxy(null),
				NullPointerException.class, "target");
	}

	private static final class Proxy extends ProxyPropertiesSource
	{
		Proxy(final Source target)
		{
			super(target);
		}

		@Override
		protected ProxyPropertiesSource reload(final Source reloadedTarget)
		{
			throw new AssertionFailedError();
		}
	}


	@Test void testReloadNeeded()
	{
		final Source targetReloaded = new AssertionErrorPropertiesSource();
		final Source target = new AssertionErrorPropertiesSource()
		{
			@Override public Source reload()
			{
				return targetReloaded;
			}
		};
		final ProxyPropertiesSource proxy = new ProxyReloadable(target);
		assertSame(target, proxy.getTarget());
		final ProxyReloadable proxyReloaded = (ProxyReloadable)proxy.reload();
		assertNotSame(proxy, proxyReloaded);
		assertSame(targetReloaded, proxyReloaded.getTarget());
	}

	@Test void testReloadNotNeeded()
	{
		final Source target = new AssertionErrorPropertiesSource()
		{
			@Override public Source reload()
			{
				return this;
			}
		};
		final ProxyPropertiesSource proxy = new ProxyReloadable(target);
		assertSame(target, proxy.getTarget());
		final ProxyReloadable proxyReloaded = (ProxyReloadable)proxy.reload();
		assertSame(proxy, proxyReloaded);
		assertSame(target, proxyReloaded.getTarget());
	}

	private static final class ProxyReloadable extends ProxyPropertiesSource
	{
		ProxyReloadable(final Source target)
		{
			super(target);
		}

		@Override
		protected ProxyPropertiesSource reload(final Source reloadedTarget)
		{
			assertNotSame(getTarget(), reloadedTarget);
			return new ProxyReloadable(reloadedTarget);
		}
	}
}
