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

		assertEquals(ONE, props.mandatoryF.getValue());
		assertEquals(TWO, props.optionalF .getValue());
		assertEquals(One.class, props.mandatory.getServiceClass());
		assertEquals(Two.class, props.optional .getServiceClass());
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

		assertEquals(NESTED, props.mandatoryF.getValue());
		assertEquals(NestedService.class, props.mandatory.getServiceClass());
		assertEquals(NESTED, props.mandatory.toString());
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
		assertEquals(THREE, props.mandatoryF.getValue());
		assertEquals(FOUR,  props.optionalF .getValue());
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
	static class WrongNestedMissingProps extends MyProperties { WrongNestedMissingProps() {super(Sources.EMPTY);} }
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
		final ServiceFactory<MyService, String> mandatory = valueService("mandatory", MyService.class, String.class);
		final ServiceFactory<MyService, String> optional  = valueService("optional" , TWO, MyService.class, String.class);

		MyProps(final java.util.Properties source)
		{
			super(view(source, "sourceDescription"));
		}

		final StringField mandatoryF = (StringField)getField("mandatory");
		final StringField optionalF  = (StringField)getField("optional");
		final StringField mandatoryNestedAF = (StringField)getField("mandatory.nestedA");
		final IntField    mandatoryNestedBF = (IntField)   getField("mandatory.nestedB");


		void assertIt()
		{
			assertEqualsUnmodifiable(asList(), getProbes());
			assertEqualsUnmodifiable(
					forPrefix("mandatory", NestedProps.class)==null
					? asList(mandatoryF, optionalF)
					: asList(mandatoryF, mandatoryNestedAF, mandatoryNestedBF, optionalF),
					getFields());

			assertEquals("mandatory", mandatoryF.getKey());
			assertEquals("optional",  optionalF .getKey());

			assertEquals(null, mandatoryF.getDefaultValue());
			assertEquals(TWO,  optionalF .getDefaultValue());

			assertFalse(mandatoryF.hasHiddenValue());
			assertFalse(optionalF .hasHiddenValue());
		}
	}

	static final class NestedProps extends MyProperties
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

	@SuppressWarnings("unused")
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

	@SuppressWarnings("unused")
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
		return result;
	}
}
