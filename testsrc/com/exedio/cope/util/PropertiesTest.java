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

import java.io.File;
import java.util.Arrays;

import com.exedio.cope.junit.CopeAssert;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON") // is more compact to write in tests
public class PropertiesTest extends CopeAssert
{
	static class TestProperties extends MyProperties
	{
		final boolean boolFalse = value("boolFalse", false);
		final boolean boolTrue = value("boolTrue", true);
		final int int10 = value("int10", 10, 5);
		final String stringMandatory = value("stringMandatory", (String)null);
		final String stringOptional = value("stringOptional", "stringOptional.defaultValue");
		final String stringHidden = valueHidden("stringHidden", (String)null);
		final String stringHiddenOptional = valueHidden("stringHiddenOptional", "stringHiddenOptional.defaultValue");
		final FileField file = fieldFile("file");
		final MapField map = fieldMap("map");

		TestProperties(final java.util.Properties source, final String sourceDescription)
		{
			super(getSource(source, sourceDescription));
		}

		final BooleanField boolFalseF = (BooleanField)forKey("boolFalse");
		final BooleanField boolTrueF = (BooleanField)forKey("boolTrue");
		final IntField int10F = (IntField)forKey("int10");
		final StringField stringMandatoryF = (StringField)forKey("stringMandatory");
		final StringField stringOptionalF = (StringField)forKey("stringOptional");
		final StringField stringHiddenF = (StringField)forKey("stringHidden");
		final StringField stringHiddenOptionalF = (StringField)forKey("stringHiddenOptional");

		void assertIt()
		{
			assertEquals(list(), getTests());
			assertEqualsUnmodifiable(Arrays.asList(new Properties.Field[]{
					boolFalseF,
					boolTrueF,
					int10F,
					stringMandatoryF,
					stringOptionalF,
					stringHiddenF,
					stringHiddenOptionalF,
					file,
					map,
			}), getFields());

			assertEquals("boolFalse", boolFalseF.getKey());
			assertEquals("boolTrue", boolTrueF.getKey());
			assertEquals("int10", int10F.getKey());
			assertEquals("stringMandatory", stringMandatoryF.getKey());
			assertEquals("stringOptional", stringOptionalF.getKey());
			assertEquals("stringHidden", stringHiddenF.getKey());
			assertEquals("stringHiddenOptional", stringHiddenOptionalF.getKey());
			assertEquals("file", file.getKey());
			assertEquals("map", map.getKey());

			assertEquals(Boolean.FALSE, boolFalseF.getDefaultValue());
			assertEquals(Boolean.TRUE, boolTrueF.getDefaultValue());
			assertEquals(Integer.valueOf(10), int10F.getDefaultValue());
			assertEquals(5, int10F.getMinimum());
			assertEquals(null, stringMandatoryF.getDefaultValue());
			assertEquals("stringOptional.defaultValue", stringOptionalF.getDefaultValue());
			assertEquals(null, stringHiddenF.getDefaultValue());
			assertEquals("stringHiddenOptional.defaultValue", stringHiddenOptionalF.getDefaultValue());
			assertEquals(null, file.getDefaultValue());
			assertEquals(null, map.getDefaultValue());

			assertEquals(false, boolFalseF.hasHiddenValue());
			assertEquals(false, boolTrueF.hasHiddenValue());
			assertEquals(false, int10F.hasHiddenValue());
			assertEquals(false, stringMandatoryF.hasHiddenValue());
			assertEquals(false, stringOptionalF.hasHiddenValue());
			assertEquals(true, stringHiddenF.hasHiddenValue());
			assertEquals(true, stringHiddenOptionalF.hasHiddenValue());
			assertEquals(false, file.hasHiddenValue());
			assertEquals(false, map.hasHiddenValue());
		}
	}

	File file1;
	File file2;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		file1 = File.createTempFile(PropertiesTest.class.getName(), ".tmp");
		file2 = File.createTempFile(PropertiesTest.class.getName(), ".tmp");
	}

	@Override
	protected void tearDown() throws Exception
	{
		if(!file1.delete())
			System.err.println("could not delete " + file1);
		if(!file2.delete())
			System.err.println("could not delete " + file2);

		super.tearDown();
	}

	@SuppressWarnings("unused")
	public void testIt()
	{
		final java.util.Properties pminimal = new java.util.Properties();
		pminimal.setProperty("stringMandatory", "stringMandatory.minimalValue");
		pminimal.setProperty("stringHidden", "stringHidden.minimalValue");

		final TestProperties minimal = new TestProperties(pminimal, "minimal");
		minimal.assertIt();
		assertEquals("minimal", minimal.getSource());

		assertEquals(false, minimal.boolFalse);
		assertEquals(true, minimal.boolTrue);
		assertEquals(Boolean.FALSE, minimal.boolFalseF.getValue());
		assertEquals(Boolean.TRUE, minimal.boolTrueF.getValue());
		assertEquals(10, minimal.int10);
		assertEquals(Integer.valueOf(10), minimal.int10F.getValue());
		assertEquals("stringMandatory.minimalValue", minimal.stringMandatory);
		assertEquals("stringOptional.defaultValue", minimal.stringOptional);
		assertEquals("stringHidden.minimalValue", minimal.stringHidden);
		assertEquals("stringHiddenOptional.defaultValue", minimal.stringHiddenOptional);
		assertEquals("stringMandatory.minimalValue", minimal.stringMandatoryF.getValue());
		assertEquals("stringOptional.defaultValue", minimal.stringOptionalF.getValue());
		assertEquals("stringHidden.minimalValue", minimal.stringHiddenF.getValue());
		assertEquals("stringHiddenOptional.defaultValue", minimal.stringHiddenOptionalF.getValue());
		assertEquals(null, minimal.file.get());
		assertEquals(null, minimal.file.getValue());
		assertEquals(new java.util.Properties(), minimal.map.mapValue());
		assertEquals(new java.util.Properties(), minimal.map.getValue());
		assertEquals(null, minimal.map.getValue("explicitKey1"));

		assertEquals(false, minimal.boolFalseF.isSpecified());
		assertEquals(false, minimal.boolTrueF.isSpecified());
		assertEquals(false, minimal.int10F.isSpecified());
		assertEquals(true, minimal.stringMandatoryF.isSpecified());
		assertEquals(false, minimal.stringOptionalF.isSpecified());
		assertEquals(true, minimal.stringHiddenF.isSpecified());
		assertEquals(false, minimal.stringHiddenOptionalF.isSpecified());
		assertEquals(false, minimal.file.isSpecified());
		assertEquals(false, minimal.map.isSpecified());

		minimal.ensureEquality(minimal);

		{
			final TestProperties minimal1 = new TestProperties(pminimal, "minimal2");
			assertEquals("minimal2", minimal1.getSource());
			minimal.ensureEquality(minimal1);
			minimal1.ensureEquality(minimal);
		}
		{
			final java.util.Properties p = copy(pminimal);
			p.setProperty("boolFalse", "true");
			p.setProperty("boolTrue", "false");
			p.setProperty("int10", "20");
			p.setProperty("stringMandatory", "stringMandatory.explicitValue");
			p.setProperty("stringOptional", "stringOptional.explicitValue");
			p.setProperty("stringHidden", "stringHidden.explicitValue");
			p.setProperty("stringHiddenOptional", "stringHiddenOptional.explicitValue");
			p.setProperty("file", file1.getPath());
			p.setProperty("map.explicitKey1", "map.explicitValue1");
			p.setProperty("map.explicitKey2", "map.explicitValue2");
			final TestProperties tp = new TestProperties(p, "maximal");
			assertEquals("maximal", tp.getSource());

			assertEquals(true, tp.boolFalse);
			assertEquals(false, tp.boolTrue);
			assertEquals(Boolean.TRUE, tp.boolFalseF.getValue());
			assertEquals(Boolean.FALSE, tp.boolTrueF.getValue());
			assertEquals(20, tp.int10F.get());
			assertEquals(Integer.valueOf(20), tp.int10F.getValue());
			assertEquals("stringMandatory.explicitValue", tp.stringMandatory);
			assertEquals("stringOptional.explicitValue", tp.stringOptional);
			assertEquals("stringHidden.explicitValue", tp.stringHidden);
			assertEquals("stringHiddenOptional.explicitValue", tp.stringHiddenOptional);
			assertEquals("stringMandatory.explicitValue", tp.stringMandatoryF.getValue());
			assertEquals("stringOptional.explicitValue", tp.stringOptionalF.getValue());
			assertEquals("stringHidden.explicitValue", tp.stringHiddenF.getValue());
			assertEquals("stringHiddenOptional.explicitValue", tp.stringHiddenOptionalF.getValue());
			assertEquals(file1, tp.file.get());
			assertEquals(file1, tp.file.getValue());
			final java.util.Properties mapExpected = new java.util.Properties();
			mapExpected.setProperty("explicitKey1", "map.explicitValue1");
			mapExpected.setProperty("explicitKey2", "map.explicitValue2");
			assertEquals(mapExpected, tp.map.mapValue());
			assertEquals(mapExpected, tp.map.getValue());
			assertEquals("map.explicitValue1", tp.map.getValue("explicitKey1"));
			assertEquals("map.explicitValue2", tp.map.getValue("explicitKey2"));
			assertEquals(null, tp.map.getValue("explicitKeyNone"));

			assertEquals(true, tp.boolFalseF.isSpecified());
			assertEquals(true, tp.boolTrueF.isSpecified());
			assertEquals(true, tp.int10F.isSpecified());
			assertEquals(true, tp.stringMandatoryF.isSpecified());
			assertEquals(true, tp.stringOptionalF.isSpecified());
			assertEquals(true, tp.stringHiddenF.isSpecified());
			assertEquals(true, tp.stringHiddenOptionalF.isSpecified());
			assertEquals(true, tp.file.isSpecified());
			assertEquals(false, tp.map.isSpecified()); // TODO
		}
		{
			final java.util.Properties p = copy(pminimal);
			p.setProperty("wrongKey.zack", "somethingZack");
			final TestProperties tp = new TestProperties(p, "wrongkey");
			assertEquals("wrongkey", tp.getSource());
			tp.ensureValidity("wrongKey");
			try
			{
				tp.ensureValidity();
				fail();
			}
			catch(final IllegalArgumentException e)
			{
				assertEquals(
						"property wrongKey.zack in wrongkey is not allowed, but only one of [" +
						"boolFalse, " +
						"boolTrue, " +
						"int10, " +
						"stringMandatory, " +
						"stringOptional, " +
						"stringHidden, " +
						"stringHiddenOptional, " +
						"file] " +
						"or one starting with [map.].", e.getMessage());
			}
		}

		// boolean
		assertWrong(pminimal,
				"wrong.bool.true",
				"boolFalse", "True",
				"property boolFalse in wrong.bool.true has invalid value," +
					" expected >true< or >false<, but got >True<.");
		assertWrong(pminimal,
				"wrong.bool.false",
				"boolFalse", "falsE",
				"property boolFalse in wrong.bool.false has invalid value," +
					" expected >true< or >false<, but got >falsE<.");
		assertInconsistent(pminimal,
				"inconsistent.bool",
				"boolFalse", "true",
				"inconsistent initialization for boolFalse between minimal and inconsistent.bool," +
					" expected false but got true.",
				"inconsistent initialization for boolFalse between inconsistent.bool and minimal," +
					" expected true but got false.");

		// int
		{
			// test lowest value
			final java.util.Properties p = copy(pminimal);
			p.setProperty("int10", "5");
			final TestProperties tp = new TestProperties(p, "int.border");
			assertEquals(5, tp.int10F.get());
			assertEquals(Integer.valueOf(5), tp.int10F.getValue());
		}
		assertWrong(pminimal,
				"wrong.int.tooSmall",
				"int10", "4",
				"property int10 in wrong.int.tooSmall has invalid value," +
				" expected an integer greater or equal 5, but got 4.");
		assertWrong(pminimal,
				"wrong.int.noNumber",
				"int10", "10x",
				"property int10 in wrong.int.noNumber has invalid value," +
				" expected an integer greater or equal 5, but got >10x<.");
		assertInconsistent(pminimal,
				"inconsistent.int",
				"int10", "88",
				"inconsistent initialization for int10 between minimal and inconsistent.int," +
					" expected 10 but got 88.",
				"inconsistent initialization for int10 between inconsistent.int and minimal," +
					" expected 88 but got 10.");

		// String
		assertWrong(pminimal,
				"wrong.stringMandatory.missing",
				"stringMandatory", null,
				"property stringMandatory in wrong.stringMandatory.missing not set and no default value specified.");
		assertWrong(pminimal,
				"wrong.stringHidden.missing",
				"stringHidden", null,
				"property stringHidden in wrong.stringHidden.missing not set and no default value specified.");
		assertInconsistent(pminimal,
				"inconsistent.stringMandatory",
				"stringMandatory", "stringMandatory.inconsistentValue",
				"inconsistent initialization for stringMandatory between minimal and inconsistent.stringMandatory," +
					" expected stringMandatory.minimalValue but got stringMandatory.inconsistentValue.",
				"inconsistent initialization for stringMandatory between inconsistent.stringMandatory and minimal," +
					" expected stringMandatory.inconsistentValue but got stringMandatory.minimalValue.");
		assertInconsistent(pminimal,
				"inconsistent.stringOptional",
				"stringOptional", "stringOptional.inconsistentValue",
				"inconsistent initialization for stringOptional between minimal and inconsistent.stringOptional," +
					" expected stringOptional.defaultValue but got stringOptional.inconsistentValue.",
				"inconsistent initialization for stringOptional between inconsistent.stringOptional and minimal," +
					" expected stringOptional.inconsistentValue but got stringOptional.defaultValue.");
		assertInconsistent(pminimal,
				"inconsistent.stringHidden",
				"stringHidden", "stringHidden.inconsistentValue",
				"inconsistent initialization for stringHidden between minimal and inconsistent.stringHidden.",
				"inconsistent initialization for stringHidden between inconsistent.stringHidden and minimal.");

		// File
		assertInconsistent(pminimal,
				"inconsistent.file",
				"file", file2.getPath(),
				"inconsistent initialization for file between minimal and inconsistent.file," +
					" expected null but got " + file2.getPath() + ".",
				"inconsistent initialization for file between inconsistent.file and minimal," +
					" expected " + file2.getPath() + " but got null.");

		final java.util.Properties fileInconsistency = copy(pminimal);
		fileInconsistency.setProperty("file", file1.getPath());
		assertInconsistent(fileInconsistency,
				"inconsistent.file",
				"file", file2.getPath(),
				"inconsistent initialization for file between minimal and inconsistent.file," +
					" expected " + file1.getPath() + " but got " + file2.getPath() + ".",
				"inconsistent initialization for file between inconsistent.file and minimal," +
					" expected " + file2.getPath() + " but got " + file1.getPath() + ".");

		// Map
		assertInconsistent(pminimal,
				"inconsistent.map",
				"map.inconsistentKey", "map.inconsistentValue",
				"inconsistent initialization for map between minimal and inconsistent.map," +
					" expected {} but got {inconsistentKey=map.inconsistentValue}.",
				"inconsistent initialization for map between inconsistent.map and minimal," +
					" expected {inconsistentKey=map.inconsistentValue} but got {}.");

		try
		{
			new Properties((Properties.Source)null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	@SuppressWarnings("unused")
	private static void assertWrong(
			final java.util.Properties template,
			final String sourceDescription,
			final String key,
			final String value,
			final String message)
	{
		final java.util.Properties wrongProps = copy(template);
		if(value!=null)
			wrongProps.setProperty(key, value);
		else
			wrongProps.remove(key);

		try
		{
			new TestProperties(wrongProps, sourceDescription);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(message, e.getMessage());
		}
	}

	private static void assertInconsistent(
			final java.util.Properties template,
			final String sourceDescription,
			final String key,
			final String value,
			final String message1, final String message2)
	{
		final TestProperties templateProps = new TestProperties(template, "minimal");
		templateProps.assertIt();

		final java.util.Properties p = copy(template);
		p.setProperty(key, value);
		final TestProperties inconsistentProps = new TestProperties(p, sourceDescription);
		try
		{
			templateProps.ensureEquality(inconsistentProps);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(message1, 	e.getMessage());
		}
		try
		{
			inconsistentProps.ensureEquality(templateProps);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(message2, 	e.getMessage());
		}
	}

	private static final java.util.Properties copy(final java.util.Properties source)
	{
		return (java.util.Properties)source.clone();
	}
}
