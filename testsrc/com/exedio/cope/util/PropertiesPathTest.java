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
import static com.exedio.cope.junit.CopeAssert.assertContainsUnmodifiable;
import static com.exedio.cope.junit.CopeAssert.assertEqualsUnmodifiable;
import static com.exedio.cope.util.Sources.view;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

public class PropertiesPathTest
{
	static class TestProperties extends MyProperties
	{
		final Path file = valuePath("file");

		TestProperties(final java.util.Properties source, final String sourceDescription)
		{
			super(view(source, sourceDescription));
		}

		final Field<?> fileF = getField("file");


		void assertIt()
		{
			assertEqualsUnmodifiable(asList(), getProbes());
			assertEqualsUnmodifiable(asList(new Properties.Field<?>[]{
					fileF,
			}), getFields());

			assertEquals("file", fileF.getKey());
			assertEquals(null, fileF.getDefaultValue());
			assertEquals(null, fileF.getDefaultValueString());
			assertEquals(null, fileF.getDefaultValueFailure());
			assertEquals(false, fileF.hasHiddenValue());
		}
	}

	@Test void testIt()
	{
		final java.util.Properties pminimal = new java.util.Properties();
		pminimal.setProperty("file", "file.minimalValue");

		final TestProperties minimal = new TestProperties(pminimal, "minimal");
		minimal.assertIt();
		assertEquals("minimal", minimal.getSource());

		assertEquals(Paths.get("file.minimalValue"), minimal.file);
		assertEquals(Paths.get("file.minimalValue"), minimal.fileF.get());
		assertEquals(Paths.get("file.minimalValue"), minimal.fileF.getValue());
		assertEquals(          "file.minimalValue",  minimal.fileF.getValueString());

		assertEquals(true, minimal.fileF.isSpecified());

		minimal.ensureEquality(minimal);

		{
			final TestProperties minimal1 = new TestProperties(pminimal, "minimal2");
			assertEquals("minimal2", minimal1.getSource());
			minimal.ensureEquality(minimal1);
			minimal1.ensureEquality(minimal);
		}
		final Path file1 = Paths.get("file1");
		{
			final java.util.Properties p = copy(pminimal);
			p.setProperty("file", file1.toString());

			final TestProperties tp = new TestProperties(p, "maximal");
			assertEquals("maximal", tp.getSource());
			assertContainsUnmodifiable(tp.getOrphanedKeys());

			assertEquals(file1, tp.fileF.get());
			assertEquals(file1, tp.fileF.getValue());

			assertEquals(true, tp.fileF.isSpecified());
		}

		// File
		assertWrong(pminimal,
				"wrong.file.missing",
				"file", null,
				"property file in wrong.file.missing " +
				"must be specified as there is no default");

		final Path file2 = Paths.get("file2");
		assertInconsistent(pminimal,
				"inconsistent.file",
				"file", file2.toString(),
				"inconsistent initialization for file between minimal and inconsistent.file," +
					" expected file.minimalValue but got " + file2 + ".",
				"inconsistent initialization for file between inconsistent.file and minimal," +
					" expected " + file2 + " but got file.minimalValue.");

		final java.util.Properties fileInconsistency = copy(pminimal);
		fileInconsistency.setProperty("file", file1.toString());
		assertInconsistent(fileInconsistency,
				"inconsistent.file",
				"file", file2.toString(),
				"inconsistent initialization for file between minimal and inconsistent.file," +
					" expected " + file1 + " but got " + file2 + ".",
				"inconsistent initialization for file between inconsistent.file and minimal," +
					" expected " + file2 + " but got " + file1 + ".");
	}


	/**
	 * Asserts the property with given key and value set in the given java.util.Properties template will cause an IllegalPropertiesException
	 * with given message when a cope.util.Properties is created.
	 */
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

		final IllegalPropertiesException e = assertThrows(
				IllegalPropertiesException.class,
				() -> new TestProperties(wrongProps, sourceDescription));
		assertEquals(message, e.getMessage());
		assertEquals(key, e.getKey());
	}

	/**
	 * Asserts a valid change of the given property (key = value) will  cause an InvalidArgumentException when
	 * calling ensureEquality() on Properties based on template compared with a Properties based on template with changed value.
	 */
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
		assertFails(() ->
			templateProps.ensureEquality(inconsistentProps),
			IllegalArgumentException.class, message1);
		assertFails(() ->
			inconsistentProps.ensureEquality(templateProps),
			IllegalArgumentException.class, message2);
	}

	private static java.util.Properties copy(final java.util.Properties source)
	{
		return (java.util.Properties)source.clone();
	}
}
