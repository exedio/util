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
import static com.exedio.cope.util.Sources.view;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class PropertiesMessageDigestWrongDefaultTest
{
	@Test void testNotSet()
	{
		assertFails(
				() -> new MyProps(new java.util.Properties()),
				IllegalPropertiesException.class,
				"property field in DESC must specify a digest, but was 'WRONG'",
				IllegalAlgorithmException.class);
	}

	@Test void testSet()
	{
		final java.util.Properties p = new java.util.Properties();
		p.setProperty("field", "SHA-512");
		final MyProps props = new MyProps(p);
		props.assertIt();

		assertEquals(SHA_512, props.fieldF.getValue());
		assertEquals(SHA_512, props.field);
		assertTrue(props.fieldF.isSpecified());
	}


	private static final MessageDigestFactory SHA_512 = new MessageDigestFactory("SHA-512");

	static class MyProps extends Properties
	{
		final MessageDigestFactory field =
				valueMessageDigest("field", "WRONG");

		MyProps(final java.util.Properties source)
		{
			super(view(source, "DESC"));
		}

		final Field<?> fieldF = getField("field");

		void assertIt()
		{
			assertEquals("field",  fieldF.getKey());
			assertEquals(null, fieldF.getDefaultValue());
		}
	}
}
