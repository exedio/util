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

import static com.exedio.cope.util.Sources.view;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

public class PropertiesSpaceSeparatedTest
{
	@Test void testSet()
	{
		final java.util.Properties p = new java.util.Properties();
		p.setProperty("field", "alpha beta");
		final Props props = new Props(p);
		props.assertIt();

		assertEquals(List.of("alpha", "beta"), props.field);
		assertEquals(List.of("alpha", "beta"), props.fieldF.getValue());
		assertEquals("alpha beta", props.fieldF.getValueString());
		assertTrue(props.fieldF.isSpecified());
	}

	@Test void testSetSpaces()
	{
		final java.util.Properties p = new java.util.Properties();
		p.setProperty("field", " alpha  beta ");
		final Props props = new Props(p);
		props.assertIt();

		assertEquals(List.of("alpha", "beta"), props.field);
		assertEquals(List.of("alpha", "beta"), props.fieldF.getValue());
		assertEquals("alpha beta", props.fieldF.getValueString());
		assertTrue(props.fieldF.isSpecified());
	}

	@Test void testSetComma()
	{
		final java.util.Properties p = new java.util.Properties();
		p.setProperty("field", "alpha,beta");
		final Props props = new Props(p);
		props.assertIt();

		assertEquals(List.of("alpha,beta"), props.field);
		assertEquals(List.of("alpha,beta"), props.fieldF.getValue());
		assertEquals("alpha,beta", props.fieldF.getValueString());
		assertTrue(props.fieldF.isSpecified());
	}

	@Test void testSetSpaceAndComma()
	{
		final java.util.Properties p = new java.util.Properties();
		p.setProperty("field", " alpha, beta ");
		final Props props = new Props(p);
		props.assertIt();

		assertEquals(List.of("alpha,", "beta"), props.field);
		assertEquals(List.of("alpha,", "beta"), props.fieldF.getValue());
		assertEquals("alpha, beta", props.fieldF.getValueString());
		assertTrue(props.fieldF.isSpecified());
	}

	@Test void testDefault()
	{
		final java.util.Properties p = new java.util.Properties();
		final Props props = new Props(p);
		props.assertIt();

		assertEquals(List.of("alpha", "beta"), props.field);
		assertEquals(List.of("alpha", "beta"), props.fieldF.getValue());
		assertEquals("alpha beta", props.fieldF.getValueString());
		assertFalse(props.fieldF.isSpecified());
	}

	@Test void testEmpty()
	{
		final java.util.Properties p = new java.util.Properties();
		p.setProperty("field", "");
		final Props props = new Props(p);
		props.assertIt();

		assertEquals(List.of(), props.field);
		assertEquals(List.of(), props.fieldF.getValue());
		assertEquals("", props.fieldF.getValueString());
		assertTrue(props.fieldF.isSpecified());
	}

	@Test void testEmptySpace()
	{
		final java.util.Properties p = new java.util.Properties();
		p.setProperty("field", " ");
		final Props props = new Props(p);
		props.assertIt();

		assertEquals(List.of(), props.field);
		assertEquals(List.of(), props.fieldF.getValue());
		assertEquals("", props.fieldF.getValueString());
		assertTrue(props.fieldF.isSpecified());
	}

	@Test void testEmptySpaces()
	{
		final java.util.Properties p = new java.util.Properties();
		p.setProperty("field", "  ");
		final Props props = new Props(p);
		props.assertIt();

		assertEquals(List.of(), props.field);
		assertEquals(List.of(), props.fieldF.getValue());
		assertEquals("", props.fieldF.getValueString());
		assertTrue(props.fieldF.isSpecified());
	}

	@Test void testSingle()
	{
		final java.util.Properties p = new java.util.Properties();
		p.setProperty("field", "alpha");
		final Props props = new Props(p);
		props.assertIt();

		assertEquals(List.of("alpha"), props.field);
		assertEquals(List.of("alpha"), props.fieldF.getValue());
		assertEquals("alpha", props.fieldF.getValueString());
		assertTrue(props.fieldF.isSpecified());
	}

	@Test void testSingleSpace()
	{
		final java.util.Properties p = new java.util.Properties();
		p.setProperty("field", " alpha ");
		final Props props = new Props(p);
		props.assertIt();

		assertEquals(List.of("alpha"), props.field);
		assertEquals(List.of("alpha"), props.fieldF.getValue());
		assertEquals("alpha", props.fieldF.getValueString());
		assertTrue(props.fieldF.isSpecified());
	}

	@Test void testSingleSpaces()
	{
		final java.util.Properties p = new java.util.Properties();
		p.setProperty("field", "  alpha  ");
		final Props props = new Props(p);
		props.assertIt();

		assertEquals(List.of("alpha"), props.field);
		assertEquals(List.of("alpha"), props.fieldF.getValue());
		assertEquals("alpha", props.fieldF.getValueString());
		assertTrue(props.fieldF.isSpecified());
	}


	private static class Props extends Properties
	{
		final List<String> field =
				valuesSpaceSeparated("field", "alpha", "beta");

		Props(final java.util.Properties source)
		{
			super(view(source, "DESC"));
		}

		final Field<?> fieldF = getField("field");

		void assertIt()
		{
			assertEquals("field",  fieldF.getKey());
			assertEquals(List.of("alpha", "beta"), fieldF.getDefaultValue());
			assertEquals("alpha beta", fieldF.getDefaultValueString());
			assertEquals(null, fieldF.getDefaultValueFailure());
			assertEquals(false, fieldF.hasHiddenValue());
		}
	}
}
