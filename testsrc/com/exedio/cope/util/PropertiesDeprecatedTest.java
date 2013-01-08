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

import java.util.Arrays;

import com.exedio.cope.junit.CopeAssert;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON") // is more compact to write in tests
public class PropertiesDeprecatedTest extends CopeAssert
{
	static class TestProperties extends Properties
	{
		@SuppressWarnings("deprecation")
		final BooleanField boolFalse = new BooleanField("boolFalse", false);
		@SuppressWarnings("deprecation")
		final BooleanField boolTrue = new BooleanField("boolTrue", true);
		@SuppressWarnings("deprecation")
		final IntField int10 = new IntField("int10", 10, 5);
		@SuppressWarnings("deprecation")
		final StringField stringMandatory = new StringField("stringMandatory");
		@SuppressWarnings("deprecation")
		final StringField stringOptional = new StringField("stringOptional", "stringOptional.defaultValue");
		@SuppressWarnings("deprecation")
		final StringField stringHidden = new StringField("stringHidden", true);
		@SuppressWarnings("deprecation")
		final FileField file = new FileField("file");
		@SuppressWarnings("deprecation")
		final MapField map = new MapField("map");

		TestProperties(final java.util.Properties source, final String sourceDescription)
		{
			super(getSource(source, sourceDescription));
		}

		void assertIt()
		{
			assertEquals(list(), getTests());
			assertEqualsUnmodifiable(Arrays.asList(new Properties.Field[]{
					boolFalse,
					boolTrue,
					int10,
					stringMandatory,
					stringOptional,
					stringHidden,
					file,
					map,
			}), getFields());

			assertEquals("boolFalse", boolFalse.getKey());
			assertEquals("boolTrue", boolTrue.getKey());
			assertEquals("int10", int10.getKey());
			assertEquals("stringMandatory", stringMandatory.getKey());
			assertEquals("stringOptional", stringOptional.getKey());
			assertEquals("stringHidden", stringHidden.getKey());
			assertEquals("file", file.getKey());
			assertEquals("map", map.getKey());

			assertEquals(Boolean.FALSE, boolFalse.getDefaultValue());
			assertEquals(Boolean.TRUE, boolTrue.getDefaultValue());
			assertEquals(Integer.valueOf(10), int10.getDefaultValue());
			assertEquals(5, int10.getMinimum());
			assertEquals(null, stringMandatory.getDefaultValue());
			assertEquals("stringOptional.defaultValue", stringOptional.getDefaultValue());
			assertEquals(null, stringHidden.getDefaultValue());
			assertEquals(null, file.getDefaultValue());
			assertEquals(null, map.getDefaultValue());

			assertEquals(false, boolFalse.hasHiddenValue());
			assertEquals(false, boolTrue.hasHiddenValue());
			assertEquals(false, int10.hasHiddenValue());
			assertEquals(false, stringMandatory.hasHiddenValue());
			assertEquals(false, stringOptional.hasHiddenValue());
			assertEquals(true, stringHidden.hasHiddenValue());
			assertEquals(false, file.hasHiddenValue());
			assertEquals(false, map.hasHiddenValue());
		}
	}

	public void testIt()
	{
		final java.util.Properties pminimal = new java.util.Properties();
		pminimal.setProperty("stringMandatory", "stringMandatory.minimalValue");
		pminimal.setProperty("stringHidden", "stringHidden.minimalValue");

		final TestProperties minimal = new TestProperties(pminimal, "minimal");
		minimal.assertIt();
	}
}
