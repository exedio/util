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

import static com.exedio.cope.junit.CopeAssert.assertEqualsUnmodifiable;
import static com.exedio.cope.util.Sources.view;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class PropertiesServiceTest
{
	@Test public void testMinimal()
	{
		final MyProps props = new MyProps(minimal());
		props.assertIt();

		assertEquals(ONE, props.mandatoryF.getValue());
		assertEquals(TWO,  props.optionalF .getValue());
		assertEquals(One.class, props.mandatory.getServiceClass());
		assertEquals(Two.class,  props.optional .getServiceClass());
		assertEquals(ONE, props.mandatory.toString());
		assertEquals(TWO,  props.optional .toString());
		assertTrue (props.mandatoryF.isSpecified());
		assertFalse(props.optionalF .isSpecified());

		final MyService mandatory = props.mandatory.newInstance("mandatoryParameter");
		final MyService optional  = props.optional .newInstance("optionalParameter");
		assertEquals(One.class, mandatory.getClass());
		assertEquals(Two.class,  optional. getClass());
		assertEquals("mandatoryParameter", mandatory.parameter);
		assertEquals("optionalParameter",  optional .parameter);
	}

	@Test public void testSet()
	{
		final java.util.Properties p = minimal();
		p.setProperty("mandatory", THREE);
		p.setProperty("optional",  FOUR);
		final MyProps props = new MyProps(p);
		props.assertIt();

		assertEquals(Three.class, props.mandatory.getServiceClass());
		assertEquals(Four .class, props.optional .getServiceClass());
		assertEquals(THREE, props.mandatoryF.getValue());
		assertEquals(FOUR,  props.optionalF .getValue());
		assertTrue (props.mandatoryF.isSpecified());
		assertTrue (props.optionalF .isSpecified());
	}

	@Test public void testMandatoryWrong()
	{
		assertWrong(
				"mandatory", "WRONG",
				"must name a class, but was 'WRONG'");
	}

	@Test public void testMandatoryUnspecified()
	{
		assertWrong(
				"mandatory", null,
				"must be specified as there is no default");
	}

	@Test public void testOptionalWrong()
	{
		assertWrong(
				"optional", "WRONG",
				"must name a class, but was 'WRONG'");
	}

	@Test public void testWrongAbstract()
	{
		final String name = WrongAbstract.class.getName();
		assertWrong(
				"mandatory", name,
				"must name a non-abstract class, but was " + name);
	}
	@SuppressWarnings({"EmptyClass", "AbstractClassNeverImplemented", "AbstractClassWithoutAbstractMethods"})
	abstract static class WrongAbstract {}

	@Test public void testWrongSuperclass()
	{
		final String name = WrongSuperclass.class.getName();
		assertWrong(
				"mandatory", name,
				"must name a subclass of " + MyService.class.getName() + ", but was " + name);
	}
	@SuppressWarnings("EmptyClass")
	static class WrongSuperclass {}

	@Test public void testWrongConstructor()
	{
		final String name = WrongConstructor.class.getName();
		assertWrong(
				"mandatory", name,
				"must name a class with a constructor with parameter java.lang.String, but was " + name);
	}
	static class WrongConstructor extends MyService { WrongConstructor() {super(null);} }


	static class MyService
	{
		final String parameter;

		MyService(final String parameter)
		{
			this.parameter = parameter;
		}
	}
	static final class One   extends MyService { One  (final String parameter) {super(parameter);} }
	static final class Two   extends MyService { Two  (final String parameter) {super(parameter);} }
	static final class Three extends MyService { Three(final String parameter) {super(parameter);} }
	static final class Four  extends MyService { Four (final String parameter) {super(parameter);} }

	static final String ONE   = One  .class.getName();
	static final String TWO   = Two  .class.getName();
	static final String THREE = Three.class.getName();
	static final String FOUR  = Four .class.getName();

	static class MyProps extends MyProperties
	{
		final ServiceFactory<MyService, String> mandatory = valueService("mandatory", MyService.class, String.class);
		final ServiceFactory<MyService, String> optional  = valueService("optional" , TWO, MyService.class, String.class);

		MyProps(final java.util.Properties source)
		{
			super(view(source, "sourceDescription"));
		}

		final StringField mandatoryF = (StringField)forKey("mandatory");
		final StringField optionalF  = (StringField)forKey("optional");


		void assertIt()
		{
			assertEqualsUnmodifiable(asList(), getProbes());
			assertEqualsUnmodifiable(asList(mandatoryF, optionalF), getFields());

			assertEquals("mandatory", mandatoryF.getKey());
			assertEquals("optional",  optionalF .getKey());

			assertEquals(null,     mandatoryF.getDefaultValue());
			assertEquals(TWO, optionalF .getDefaultValue());

			assertFalse(mandatoryF.hasHiddenValue());
			assertFalse(optionalF .hasHiddenValue());
		}
	}

	@SuppressWarnings("unused")
	private static void assertWrong(
			final String key,
			final String value,
			final String message)
	{
		final java.util.Properties wrongProps = minimal();
		if(value!=null)
			wrongProps.setProperty(key, value);
		else
			wrongProps.remove(key);

		try
		{
			new MyProps(wrongProps);
			fail();
		}
		catch(final IllegalPropertiesException e)
		{
			assertEquals(key, e.getKey());
			assertEquals(message, e.getDetail());
		}
	}

	private static java.util.Properties minimal()
	{
		final java.util.Properties result = new java.util.Properties();
		result.setProperty("mandatory", One.class.getName());
		return result;
	}
}
