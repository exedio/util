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

package com.exedio.cope.util.servlet;

import static com.exedio.cope.junit.Assert.assertFails;
import static com.exedio.cope.util.servlet.ServletSource.create;

import org.junit.jupiter.api.Test;

public class ServletSourceFileCollisionTest
{
	@Test void test()
	{
		final TestContext context =
				new TestContext("/testContextPath", "testContextPath.");
		assertFails(
				() -> create(context),
				IllegalArgumentException.class,
				"Both " +
				"testContextPath.com.exedio.cope.util.servlet.ServletSource.propertiesFile=valueNew " +
				"and " +
				"testContextPath.com.exedio.cope.servletutil.ServletSource.propertiesFile=valueOld " +
				"are set");
	}

	private static class TestContext extends AssertionFailedServletContext
	{
		private final String contextPath;
		private final String prefix;

		TestContext(final String contextPath, final String prefix)
		{
			this.contextPath = contextPath;
			this.prefix = prefix;
		}

		@Override
		public String getInitParameter(final String name)
		{
			if((prefix + "com.exedio.cope.servletutil.ServletSource.propertiesFile").equals(name))
				return "valueOld";
			else if((prefix + "com.exedio.cope.util.servlet.ServletSource.propertiesFile").equals(name))
				return "valueNew";
			else
				throw new IllegalArgumentException(name);
		}

		@Override
		public String getContextPath()
		{
			return contextPath;
		}
	}
}
