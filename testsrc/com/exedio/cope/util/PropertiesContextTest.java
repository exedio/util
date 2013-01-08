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
import java.util.Collection;
import java.util.Collections;

import com.exedio.cope.junit.CopeAssert;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON") // is more compact to write in tests
public class PropertiesContextTest extends CopeAssert
{
	static class TestProperties extends MyProperties
	{
		final String aField = value("aKey", (String)null);

		@Deprecated
		TestProperties(final java.util.Properties source, final String sourceDescription, final Source context)
		{
			super(getSource(source, sourceDescription), context);
		}
	}

	public void testContext()
	{
		assertContext("y", "${x}");
		assertContext("bucket", "${eimer}");
		assertContext("bucketpostfix", "${eimer}postfix");
		assertContext("prefixbucket", "prefix${eimer}");
		assertContext("prefixbucketpostfix", "prefix${eimer}postfix");
		assertContext("bucketinfixwater", "${eimer}infix${wasser}");
		assertContext("bucketinfixwaterpostfix", "${eimer}infix${wasser}postfix");
		assertContext("prefixbucketinfixwater", "prefix${eimer}infix${wasser}");
		assertContext("prefixbucketinfixwaterpostfix", "prefix${eimer}infix${wasser}postfix");
		assertContext("x$kkk", "x$kkk");

		try
		{
			getContext("${nixus}");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("key 'nixus' not defined by context TestContextDescription", e.getMessage());
		}
		try
		{
			getContext("x${}y");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("${} not allowed in x${}y", e.getMessage());
		}
		try
		{
			getContext("x${kkk");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("missing '}' in x${kkk", e.getMessage());
		}
	}

	private static final TestProperties getContext(final String raw)
	{
		final java.util.Properties pminimal = new java.util.Properties();
		pminimal.setProperty("aKey", raw);
		final TestProperties minimal = new TestProperties(pminimal, "minimal", new Properties.Source(){

			public String get(final String key)
			{
				if("x".equals(key))
					return "y";
				else if("eimer".equals(key))
					return "bucket";
				else if("wasser".equals(key))
					return "water";
				else if("nixus".equals(key))
					return null;
				else
					throw new RuntimeException(key);
			}

			public Collection<String> keySet()
			{
				return Collections.unmodifiableList(Arrays.asList("x", "eimer", "wasser"));
			}

			public String getDescription()
			{
				return "TestContextDescription";
			}

			@Override
			public String toString()
			{
				return "TestContextToString";
			}
		});

		return minimal;
	}

	private static final void assertContext(final String replaced, final String raw)
	{
		assertEquals(replaced, getContext(raw).aField);
	}
}
