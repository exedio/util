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

/**
 * Tests that private methods cannot be overridden in subclasses somehow,
 * even when trying with inner classes.
 */
@SuppressWarnings("MethodMayBeStatic")
public class PrivateFinalTest
{
	static class Inner
	{
		private String methodExtnd()
		{
			return "InnerExtnd";
		}
		@SuppressWarnings("FinalPrivateMethod")
		private final String methodFinal()
		{
			return "InnerFinal";
		}
	}


	@Test void testInner()
	{
		assertEquals("InnerExtnd", new Inner().methodExtnd());
		assertEquals("InnerFinal", new Inner().methodFinal());
	}

	@Test void testInnerSubAnonymous()
	{
		final Inner instance = new Inner()
		{
			@SuppressWarnings({"unused", "MethodOverridesInaccessibleMethodOfSuper"})
			private String methodExtnd()
			{
				return "InnerExtndSubAnonymous";
			}
			@SuppressWarnings({"unused", "MethodOverridesInaccessibleMethodOfSuper"})
			private String methodFinal()
			{
				return "InnerFinalSubAnonymous";
			}
		};

		assertEquals("InnerExtnd", instance.methodExtnd()); // anonymous subclass does not override method
		assertEquals("InnerFinal", instance.methodFinal()); // anonymous subclass does not override method
	}


	@Test void testInnerSub()
	{
		final Drinner instance = new Drinner();
		final Inner instanceSuper = instance;

		assertEquals("InnerExtndSub", instance.methodExtnd());
		assertEquals("InnerFinalSub", instance.methodFinal());
		assertEquals("InnerExtnd", instanceSuper.methodExtnd()); // subclass does not override method
		assertEquals("InnerFinal", instanceSuper.methodFinal()); // subclass does not override method
	}
	static class Drinner extends Inner
	{
		@SuppressWarnings("MethodOverridesInaccessibleMethodOfSuper")
		private String methodExtnd()
		{
			return "InnerExtndSub";
		}
		@SuppressWarnings("MethodOverridesInaccessibleMethodOfSuper")
		private String methodFinal()
		{
			return "InnerFinalSub";
		}
	}


	@Test void testExtendsOuter()
	{
		final ExtendsOuter instance = new ExtendsOuter();
		final PrivateFinalTest instanceSuper = instance;

		assertEquals("ExtendsOuterExtnd", instance.methodExtnd());
		assertEquals("ExtendsOuterFinal", instance.methodFinal());
		assertEquals("OuterExtnd", instanceSuper.methodExtnd()); // subclass does not override method
		assertEquals("OuterFinal", instanceSuper.methodFinal()); // subclass does not override method
	}
	private String methodExtnd()
	{
		return "OuterExtnd";
	}
	@SuppressWarnings("FinalPrivateMethod")
	private final String methodFinal()
	{
		return "OuterFinal";
	}
	static class ExtendsOuter extends PrivateFinalTest
	{
		@SuppressWarnings("MethodOverridesInaccessibleMethodOfSuper")
		private String methodExtnd()
		{
			return "ExtendsOuterExtnd";
		}
		@SuppressWarnings("MethodOverridesInaccessibleMethodOfSuper")
		private String methodFinal()
		{
			return "ExtendsOuterFinal";
		}
	}
}
