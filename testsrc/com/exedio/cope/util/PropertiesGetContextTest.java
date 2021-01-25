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
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class PropertiesGetContextTest
{
	static class TestProperties extends Properties
	{
		final String stringMandatory = value("stringMandatory", (String)null);

		@SuppressWarnings("deprecation") // OK: testing deprecated API
		TestProperties(final java.util.Properties source, final String sourceDescription, final Source context)
		{
			super(getSource(source, sourceDescription), context);
		}
	}

	@Test void testContextFails()
	{
		final java.util.Properties pcontext = new java.util.Properties();
		final Properties.Source context = new AssertionErrorPropertiesSource();
		assertFails(
				() -> new TestProperties(pcontext, "context", context),
				IllegalArgumentException.class,
				"context no longer supported");
	}

	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testContextNull()
	{
		final java.util.Properties pcontext = new java.util.Properties();
		pcontext.setProperty("stringMandatory", "stringMandatory.minimalValue");
		final TestProperties context = new TestProperties(pcontext, "context", null);
		assertEquals("stringMandatory.minimalValue", context.stringMandatory);

		assertFails(() ->
			context.getContext(null),
			NullPointerException.class, "key");

		assertFails(() ->
			context.getContext("n"),
			IllegalStateException.class,
			"context no longer supported");

		assertFails(
			context::getContext,
			IllegalStateException.class,
			"context no longer supported");
	}
}
