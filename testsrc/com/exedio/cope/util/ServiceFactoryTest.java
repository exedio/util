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

import static com.exedio.cope.junit.EqualsAssert.assertEqualsAndHash;
import static com.exedio.cope.junit.EqualsAssert.assertNotEqualsAndHash;

import org.junit.Test;

public class ServiceFactoryTest
{
	@Test public void testEqualsHashCode() throws NoSuchMethodException
	{
		final Props props = new Props();
		final ServiceFactory<MyService, String> one =
				new ServiceFactory<>(One.class.getDeclaredConstructor(String.class), null);

		assertEqualsAndHash(one,
				new ServiceFactory<>(One.class.getDeclaredConstructor(String.class), null));

		final ServiceFactory<MyService, String> oneP =
				new ServiceFactory<>(One.class.getDeclaredConstructor(String.class), props);

		assertEqualsAndHash(oneP,
				new ServiceFactory<>(One.class.getDeclaredConstructor(String.class), props));

		assertNotEqualsAndHash(one, oneP,
				new ServiceFactory<>(Two.class.getDeclaredConstructor(String.class), null),
				new ServiceFactory<>(Two.class.getDeclaredConstructor(), null),
				new ServiceFactory<>(Three.class.getDeclaredConstructor(String.class), null),
				new ServiceFactory<>(Three.class.getDeclaredConstructor(Integer.class), null),
				new ServiceFactory<>(One.class.getDeclaredConstructor(String.class), new Props()));
	}


	@SuppressWarnings("EmptyClass")
	static class MyService
	{
	}
	static final class One extends MyService
	{
		One(@SuppressWarnings("unused") final String parameter) {}
	}
	static final class Two extends MyService
	{
		Two(@SuppressWarnings("unused") final String parameter) {}
		Two() {}
	}
	static final class Three extends MyService
	{
		Three(@SuppressWarnings("unused") final String parameter) {}
		Three(@SuppressWarnings("unused") final Integer parameter) {}
	}
	static final class Props extends Properties
	{
		Props() { super(Sources.EMPTY); }
	}
}
