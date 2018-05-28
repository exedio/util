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
import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON") // is more compact to write in tests
public class PropertiesDeprecatedTest
{
	static class TestProperties extends Properties
	{
		@SuppressWarnings("deprecation")
		final Field<Boolean> boolFalse = field("boolFalse", false);
		@SuppressWarnings("deprecation")
		final Field<Boolean> boolTrue = field("boolTrue", true);
		@SuppressWarnings("deprecation")
		final Field<Integer> int10 = field("int10", 10, 5);
		@SuppressWarnings("deprecation")
		final Field<String> stringMandatory = field("stringMandatory", (String)null);
		@SuppressWarnings("deprecation")
		final Field<String> stringOptional = field("stringOptional", "stringOptional.defaultValue");
		@SuppressWarnings("deprecation")
		final Field<File> file = fieldFile("file");

		TestProperties(final java.util.Properties source)
		{
			super(view(source, "minimal"));
		}

		void assertIt()
		{
			assertEqualsUnmodifiable(asList(), getProbes());
			assertEqualsUnmodifiable(asList(new Properties.Field<?>[]{
					boolFalse,
					boolTrue,
					int10,
					stringMandatory,
					stringOptional,
					file,
			}), getFields());

			assertEquals("boolFalse", boolFalse.getKey());
			assertEquals("boolTrue", boolTrue.getKey());
			assertEquals("int10", int10.getKey());
			assertEquals("stringMandatory", stringMandatory.getKey());
			assertEquals("stringOptional", stringOptional.getKey());
			assertEquals("file", file.getKey());

			assertEquals(Boolean.FALSE, boolFalse.getDefaultValue());
			assertEquals(Boolean.TRUE, boolTrue.getDefaultValue());
			assertEquals(Integer.valueOf(10), int10.getDefaultValue());
			assertEquals(Integer.valueOf(5), int10.getMinimum());
			assertEquals(null, stringMandatory.getDefaultValue());
			assertEquals("stringOptional.defaultValue", stringOptional.getDefaultValue());
			assertEquals(null, file.getDefaultValue());

			assertEquals(false, boolFalse.hasHiddenValue());
			assertEquals(false, boolTrue.hasHiddenValue());
			assertEquals(false, int10.hasHiddenValue());
			assertEquals(false, stringMandatory.hasHiddenValue());
			assertEquals(false, stringOptional.hasHiddenValue());
			assertEquals(false, file.hasHiddenValue());
		}

		@SuppressWarnings("deprecation")
		void assertItDeprecated()
		{
			assertEqualsUnmodifiable(asList(), getTests());
		}
	}

	@Test void testIt()
	{
		final java.util.Properties pminimal = new java.util.Properties();
		pminimal.setProperty("stringMandatory", "stringMandatory.minimalValue");
		pminimal.setProperty("stringHidden", "stringHidden.minimalValue");
		pminimal.setProperty("file", "file.minimalValue");

		final TestProperties minimal = new TestProperties(pminimal);
		minimal.assertIt();
		minimal.assertItDeprecated();
	}
}
