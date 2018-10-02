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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON") // is more compact to write in tests
public class PropertiesContextNullTest
{
	static class TestProperties extends Properties
	{
		final String aField = value("aKey", (String)null);

		TestProperties(final java.util.Properties source)
		{
			super(view(source, "minimal"));
		}
	}

	@Test void testContext()
	{
		assertContext("${x}");
		assertContext("${eimer}");
		assertContext("${eimer}postfix");
		assertContext("prefix${eimer}");
		assertContext("prefix${eimer}postfix");
		assertContext("${eimer}infix${wasser}");
		assertContext("${eimer}infix${wasser}postfix");
		assertContext("prefix${eimer}infix${wasser}");
		assertContext("prefix${eimer}infix${wasser}postfix");
		assertContext("x$kkk");

		assertContext("${nixus}");
		assertContext("x${}y");
		assertContext("x${kkk");
	}

	private static TestProperties getContext(final String raw)
	{
		final java.util.Properties pminimal = new java.util.Properties();
		pminimal.setProperty("aKey", raw);
		return new TestProperties(pminimal);
	}

	private static void assertContext(final String raw)
	{
		assertEquals(raw, getContext(raw).aField);
	}
}
