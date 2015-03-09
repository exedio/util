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

import static com.exedio.cope.util.Sources.view;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;

import java.util.List;
import java.util.concurrent.Callable;
import org.junit.Test;

public class PropertiesNestedTestsTest
{
	@Test
	public void getTest()
	{
		final java.util.Properties source = new java.util.Properties();

		final Outer outer = new Outer(source);
		final Inner inner1 = outer.nested1;
		final Inner inner2 = outer.nested2;
		final Drinner drinner11 = inner1.nested1;
		final Drinner drinner12 = inner1.nested2;
		final Drinner drinner21 = inner2.nested1;
		final Drinner drinner22 = inner2.nested2;

		assertEquals(asList(outer.test1, outer.test2), outer.getTests());
		assertEquals(asList(inner1.test1, inner1.test2), inner1.getTests());
		assertEquals(asList(inner2.test1, inner2.test2), inner2.getTests());
		assertEquals(asList(drinner11.test1, drinner11.test2), drinner11.getTests());
		assertEquals(asList(drinner12.test1, drinner12.test2), drinner12.getTests());
		assertEquals(asList(drinner21.test1, drinner21.test2), drinner21.getTests());
		assertEquals(asList(drinner22.test1, drinner22.test2), drinner22.getTests());
	}

	static class Outer extends Properties
	{
		final Inner nested1 = value("inner1", Inner.factory());
		final Inner nested2 = value("inner2", Inner.factory());

		final Callable<?> test1 = test("outerTest1");
		final Callable<?> test2 = test("outerTest2");

		@Override
		public List<Callable<?>> getTests()
		{
			return asList(test1, test2);
		}

		Outer(final java.util.Properties source)
		{
			super(view(source, "someDescription"));
		}
	}

	static class Inner extends Properties
	{
		final Drinner nested1 = value("drinner1", Drinner.factory());
		final Drinner nested2 = value("drinner2", Drinner.factory());

		final Callable<?> test1 = test("innerTest1");
		final Callable<?> test2 = test("innerTest2");

		@Override
		public List<Callable<?>> getTests()
		{
			return asList(test1, test2);
		}

		static Factory<Inner> factory()
		{
			return new Factory<Inner>()
			{
				@Override public Inner create(final Source source)
				{
					return new Inner(source);
				}
			};
		}

		Inner(final Source source)
		{
			super(source);
		}
	}

	static class Drinner extends Properties
	{
		final Callable<?> test1 = test("drinnerTest1");
		final Callable<?> test2 = test("drinnerTest2");

		@Override
		public List<Callable<?>> getTests()
		{
			return asList(test1, test2);
		}

		static Factory<Drinner> factory()
		{
			return new Factory<Drinner>()
			{
				@Override public Drinner create(final Source source)
				{
					return new Drinner(source);
				}
			};
		}

		Drinner(final Source source)
		{
			super(source);
		}
	}

	static Callable<Callable<?>> test(final String name)
	{
		return new Callable<Callable<?>>(){
			@Override
			public Callable<?> call()
			{
				throw new RuntimeException();
			}
			@Override
			public String toString()
			{
				return name;
			}
		};
	}
}
