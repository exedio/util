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
import static com.exedio.cope.junit.CopeAssert.assertEqualsUnmodifiable;
import static com.exedio.cope.util.PropertiesTest.assertThrowsIllegalProperties;
import static com.exedio.cope.util.Sources.view;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class PropertiesServiceTest
{
	@Test void testMinimal()
	{
		final MyProps props = new MyProps(minimal());
		props.assertIt();

		assertEquals(One.class, props.mandatoryF.getValue());
		assertEquals(One.class, props.mandatStrF.getValue());
		assertEquals(One.class, props.mandatClsF.getValue());
		assertEquals(Two.class, props.optionalF .getValue());
		assertEquals(Two.class, props.optionClsF.getValue());
		assertEquals(One.class, props.mandatory.getServiceClass());
		assertEquals(One.class, props.mandatStr.getServiceClass());
		assertEquals(One.class, props.mandatCls.getServiceClass());
		assertEquals(Two.class, props.optional .getServiceClass());
		assertEquals(Two.class, props.optionCls.getServiceClass());
		assertEquals(ONE, props.mandatoryF.getValueString());
		assertEquals(TWO, props.optionalF .getValueString());
		assertEquals(ONE, props.mandatory.toString());
		assertEquals(TWO, props.optional .toString());
		assertTrue (props.mandatoryF.isSpecified());
		assertFalse(props.optionalF .isSpecified());

		final MyService mandatory = props.mandatory.newInstance("mandatoryParameter");
		final MyService optional  = props.optional .newInstance("optionalParameter");
		assertEquals(One.class, mandatory.getClass());
		assertEquals(Two.class, optional .getClass());
		assertEquals("mandatoryParameter", mandatory.parameter);
		assertEquals("optionalParameter",  optional .parameter);
	}

	@Test void testMinimalNested()
	{
		final java.util.Properties p = minimal();
		p.setProperty("mandatory", NESTED);
		final MyProps props = new MyProps(p);
		props.assertIt();

		assertEquals(NestedService.class, props.mandatoryF.getValue());
		assertEquals(NestedService.class, props.mandatory.getServiceClass());
		assertEquals(NESTED, props.mandatory.toString());
		assertEquals(NestedService.class.getName(), props.mandatoryF.getValueString());
		assertTrue(props.mandatoryF.isSpecified());

		final NestedService nested = (NestedService)props.mandatory.newInstance("nestedParameter");
		assertEquals("nestedParameter", nested.parameter);
		assertEquals("nestedAdefault", nested.properties.nestedA);
		assertEquals(55, nested.properties.nestedB);
	}

	@Test void testSet()
	{
		final java.util.Properties p = minimal();
		p.setProperty("mandatory", THREE);
		p.setProperty("optional",  FOUR);
		final MyProps props = new MyProps(p);
		props.assertIt();

		assertEquals(Three.class, props.mandatory.getServiceClass());
		assertEquals(Four .class, props.optional .getServiceClass());
		assertEquals(Three.class, props.mandatoryF.getValue());
		assertEquals(Four .class, props.optionalF .getValue());
		assertEquals(THREE, props.mandatoryF.getValueString());
		assertEquals(FOUR,  props.optionalF .getValueString());
		assertTrue(props.mandatoryF.isSpecified());
		assertTrue(props.optionalF .isSpecified());
	}

	@Test void testSetNested()
	{
		final java.util.Properties p = minimal();
		p.setProperty("mandatory", NESTED);
		p.setProperty("mandatory.nestedA", "nestedAset");
		p.setProperty("mandatory.nestedB", "66");
		final MyProps props = new MyProps(p);
		props.assertIt();

		final NestedService nested = (NestedService)props.mandatory.newInstance("nestedParameter");
		assertEquals("nestedParameter", nested.parameter);
		assertEquals("nestedAset", nested.properties.nestedA);
		assertEquals(66, nested.properties.nestedB);
	}

	@Test void testGetString()
	{
		final MyProps props = new MyProps(minimal());
		props.assertIt();

		assertEquals(THREE, props.mandatoryF.getString(Three.class));
		assertEquals(null,  props.mandatoryF.getString(null));
		assertFails(
				() -> props.mandatoryF.getString(55),
				ClassCastException.class,
				"Cannot cast java.lang.Integer to java.lang.Class");
	}

	@Test void testMandatoryWrong()
	{
		assertWrong(
				"mandatory", "WRONG",
				"must name a class, but was 'WRONG'",
				ClassNotFoundException.class);
	}

	@Test void testMandatoryUnspecified()
	{
		assertWrong(
				"mandatory", null,
				"must be specified as there is no default",
				null);
	}

	@Test void testOptionalWrong()
	{
		assertWrong(
				"optional", "WRONG",
				"must name a class, but was 'WRONG'",
				ClassNotFoundException.class);
	}

	@Test void testWrongAbstract()
	{
		final String name = WrongAbstract.class.getName();
		assertWrong(
				"mandatory", name,
				"must name a non-abstract class, but was " + name,
				null);
	}
	@SuppressWarnings({"EmptyClass", "AbstractClassNeverImplemented", "AbstractClassWithoutAbstractMethods"})
	abstract static class WrongAbstract {}

	@Test void testWrongSuperclass()
	{
		final String name = WrongSuperclass.class.getName();
		assertWrong(
				"mandatory", name,
				"must name a subclass of " + MyService.class.getName() + ", but was " + name,
				ClassCastException.class);
	}
	@SuppressWarnings("EmptyClass")
	static class WrongSuperclass {}

	@Test void testWrongConstructor()
	{
		final String name = WrongConstructor.class.getName();
		assertWrong(
				"mandatory", name,
				"must name a class with a constructor with parameter java.lang.String, but was " + name,
				NoSuchMethodException.class);
	}
	static class WrongConstructor extends MyService { WrongConstructor() {super(null);} }

	@Test void testWrongConstructorProperties()
	{
		final String name = WrongConstructorProperties.class.getName();
		assertWrong(
				"mandatory", name,
				"must name a class with a constructor with parameters java.lang.String," + NestedProps.class.getName() + ", but was " + name,
				NoSuchMethodException.class);
	}
	@ServiceProperties(NestedProps.class)
	static class WrongConstructorProperties extends MyService { WrongConstructorProperties() {super(null);} }

	@Test void testWrongNestedMissing()
	{
		final String name = WrongNestedMissingService.class.getName();
		assertWrong(
				"mandatory", name,
				"names a class " + name + " " +
				"annotated by @ServiceProperties(" + WrongNestedMissingProps.class.getName() + "), " +
				"which must have a constructor with parameter " + Properties.Source.class.getName(),
				NoSuchMethodException.class);
	}
	static class WrongNestedMissingProps extends Properties { WrongNestedMissingProps() {super(Sources.EMPTY);} }
	@ServiceProperties(WrongNestedMissingProps.class)
	static class WrongNestedMissingService extends MyService { WrongNestedMissingService() {super(null);} }

	@Test void testWrongNestedFails()
	{
		final String name = WrongNestedFailsService.class.getName();
		assertWrong(
				"mandatory.nestedB", "5x",
				"mandatory", name,
				"must be an integer greater or equal 1, but was '5x'",
				IllegalPropertiesException.class);
	}
	@ServiceProperties(NestedProps.class)
	static class WrongNestedFailsService extends MyService { WrongNestedFailsService() {super(null);} }


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
		final ServiceFactory<MyService, String> mandatory = valueService("mandatory",                   MyService.class, String.class);
		final ServiceFactory<MyService, String> mandatStr = valueService("mandatStr", (String)null,     MyService.class, String.class);
		final ServiceFactory<MyService, String> mandatCls = valueService("mandatCls", (Class<Two>)null, MyService.class, String.class);
		final ServiceFactory<MyService, String> optional  = valueService("optional" , TWO,              MyService.class, String.class);
		final ServiceFactory<MyService, String> optionCls = valueService("optionCls", Two.class,        MyService.class, String.class);

		MyProps(final java.util.Properties source)
		{
			super(view(source, "sourceDescription"));
		}

		final Field<?> mandatoryF = getField("mandatory");
		final Field<?> mandatStrF = getField("mandatStr");
		final Field<?> mandatClsF = getField("mandatCls");
		final Field<?> optionalF  = getField("optional");
		final Field<?> optionClsF = getField("optionCls");
		final Field<?> mandatoryNestedAF = getField("mandatory.nestedA");
		final Field<?> mandatoryNestedBF = getField("mandatory.nestedB");


		void assertIt()
		{
			assertEqualsUnmodifiable(asList(), getProbes());
			assertEqualsUnmodifiable(
					forPrefix("mandatory", NestedProps.class)==null
					? asList(mandatoryF,                                       mandatStrF, mandatClsF, optionalF, optionClsF)
					: asList(mandatoryF, mandatoryNestedAF, mandatoryNestedBF, mandatStrF, mandatClsF, optionalF, optionClsF),
					getFields());

			assertEquals("mandatory", mandatoryF.getKey());
			assertEquals("mandatStr", mandatStrF.getKey());
			assertEquals("mandatCls", mandatClsF.getKey());
			assertEquals("optional",  optionalF .getKey());
			assertEquals("optionCls", optionClsF.getKey());

			assertEquals(null, mandatoryF.getDefaultValue());
			assertEquals(null, mandatStrF.getDefaultValue());
			assertEquals(null, mandatClsF.getDefaultValue());
			assertEquals(Two.class, optionalF.getDefaultValue());
			assertEquals(Two.class, optionClsF.getDefaultValue());

			assertEquals(null, mandatoryF.getDefaultValueString());
			assertEquals(null, mandatStrF.getDefaultValueString());
			assertEquals(null, mandatClsF.getDefaultValueString());
			assertEquals(TWO,  optionalF .getDefaultValueString());
			assertEquals(TWO,  optionClsF.getDefaultValueString());

			assertEquals(null, mandatoryF.getDefaultValueFailure());
			assertEquals(null, mandatStrF.getDefaultValueFailure());
			assertEquals(null, mandatClsF.getDefaultValueFailure());
			assertEquals(null, optionalF .getDefaultValueFailure());
			assertEquals(null, optionClsF.getDefaultValueFailure());

			assertFalse(mandatoryF.hasHiddenValue());
			assertFalse(mandatStrF.hasHiddenValue());
			assertFalse(mandatClsF.hasHiddenValue());
			assertFalse(optionalF .hasHiddenValue());
			assertFalse(optionClsF.hasHiddenValue());
		}
	}

	static final class NestedProps extends Properties
	{
		final String nestedA = value("nestedA", "nestedAdefault");
		final int    nestedB = value("nestedB", 55, 1);

		private NestedProps(final Source source)
		{
			super(source);
		}
	}

	@ServiceProperties(NestedProps.class)
	static final class NestedService extends MyService
	{
		final NestedProps properties;

		NestedService(final String parameter, final NestedProps properties)
		{
			super(parameter);
			this.properties = properties;
		}
	}

	static final String NESTED = NestedService.class.getName();

	private static void assertWrong(
			final String key,
			final String value,
			final String message,
			final Class<? extends Exception> cause)
	{
		final java.util.Properties wrongProps = minimal();
		if(value!=null)
			wrongProps.setProperty(key, value);
		else
			wrongProps.remove(key);

		assertThrowsIllegalProperties(
				() -> new MyProps(wrongProps),
				key, message, cause);
	}

	private static void assertWrong(
			final String key1,
			final String value1,
			final String key2,
			final String value2,
			final String message,
			final Class<? extends Exception> cause)
	{
		final java.util.Properties wrongProps = minimal();
		if(value1!=null)
			wrongProps.setProperty(key1, value1);
		else
			wrongProps.remove(key1);

		if(value2!=null)
			wrongProps.setProperty(key2, value2);
		else
			wrongProps.remove(key2);

		assertThrowsIllegalProperties(
				() -> new MyProps(wrongProps),
				key1, message, cause);
	}

	private static java.util.Properties minimal()
	{
		final java.util.Properties result = new java.util.Properties();
		result.setProperty("mandatory", One.class.getName());
		result.setProperty("mandatStr", One.class.getName());
		result.setProperty("mandatCls", One.class.getName());
		return result;
	}
}
