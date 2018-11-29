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
import static com.exedio.cope.util.PropertiesInstance.create;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.util.Properties.Factory;
import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.util.PropertiesInstance.NotSetException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings({"NP_NULL_PARAM_DEREF_NONVIRTUAL","NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS"})
public class PropertiesInstanceTest
{
	@Test void testSet()
	{
		final PropertiesInstance<MyProps> pi = create(myFactory);
		assertFails(pi::get, NotSetException.class, null);

		final MyProps created = pi.create(source("createdValue"));
		assertEquals("createdValue", created.val);
		assertFails(pi::get, NotSetException.class, null);

		assertSame(PROPS,
				pi.set(PROPS));
		assertNotSame(PROPS, created);
		assertSame(PROPS, pi.get());

		pi.remove();
		assertFails(pi::get, NotSetException.class, null);

		pi.remove();
		assertFails(pi::get, NotSetException.class, null);

		final MyProps created2 = pi.create(source("createdValue2"));
		assertEquals("createdValue2", created2.val);
		assertFails(pi::get, NotSetException.class, null);
	}

	@Test void testSetSource()
	{
		final PropertiesInstance<MyProps> pi = create(myFactory);
		assertFails(pi::get, NotSetException.class, null);

		final MyProps created = pi.create(source("createdValue"));
		assertEquals("createdValue", created.val);
		assertFails(pi::get, NotSetException.class, null);

		final MyProps set = pi.set(source("setValue"));
		assertEquals("setValue", set.val);
		assertNotSame(created, set);
		assertSame(set, pi.get());

		pi.remove();
		assertFails(pi::get, NotSetException.class, null);
	}

	@Test void testFactoryFails()
	{
		final PropertiesInstance<MyProps> pi =
				create(s -> { throw new IllegalArgumentException("messageFactoryFails"); });
		assertFails(
				() -> pi.create(ERROR_SOURCE),
				IllegalArgumentException.class,
				"messageFactoryFails");
		assertFails(
				() -> pi.set(ERROR_SOURCE),
				IllegalArgumentException.class,
				"messageFactoryFails");
		assertFails(pi::get, NotSetException.class, null);

		assertSame(PROPS,
				pi.set(PROPS));
		assertFails(
				() -> pi.create(ERROR_SOURCE),
				IllegalArgumentException.class,
				"messageFactoryFails");
		assertFails(
				() -> pi.set(ERROR_SOURCE),
				IllegalArgumentException.class,
				"messageFactoryFails");
		assertSame(PROPS, pi.get());

		pi.remove();
		assertFails(pi::get, NotSetException.class, null);
	}

	@Test void testFactoryNull()
	{
		assertFails(
				() -> create((Factory<?>)null),
				NullPointerException.class,
				"factory");
	}

	@Test void testSetSourceNull()
	{
		final PropertiesInstance<MyProps> pi = create(myFactory);
		assertFails(
				() -> pi.set((Source)null),
				NullPointerException.class,
				"source");
	}

	@Test void testCreateSourceNull()
	{
		final PropertiesInstance<MyProps> pi = create(myFactory);
		assertFails(
				() -> pi.create((Source)null),
				NullPointerException.class,
				"source");
	}

	@Test void testNewValueNull()
	{
		final PropertiesInstance<MyProps> pi = create(myFactory);
		assertFails(
				() -> pi.set((MyProps)null),
				NullPointerException.class,
				"newValue");
	}


	static final class MyProps extends Properties
	{
		final String val = value("key", (String)null);

		MyProps(final Source source)
		{
			super(source);
		}
	}

	private static final Factory<MyProps> myFactory = MyProps::new;

	private static Source source(final String val)
	{
		return new AssertionErrorPropertiesSource()
		{
			@Override
			public String get(final String key)
			{
				return "key".equals(key) ? val : null;
			}
			@Override
			public String getDescription()
			{
				return "DESC";
			}
		};
	}

	private static final AssertionErrorPropertiesSource ERROR_SOURCE = new AssertionErrorPropertiesSource();

	private static final MyProps PROPS = new MyProps(source(PropertiesInstanceTest.class.getName()));
}
