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

import org.junit.jupiter.api.Test;

public class PropertiesServiceWrongDefaultTest
{
	@Test void testNotSet()
	{
		assertFails(
				() -> new MyProps(new java.util.Properties()),
				IllegalPropertiesException.class,
				"property field in DESC must name a class, but was 'com.exedio.cope.util.PropertiesServiceWrongDefaultTest$MyServiceDoesNotExist'",
				ClassNotFoundException.class);
	}

	@Test void testSet()
	{
		final java.util.Properties p = new java.util.Properties();
		p.setProperty("field", MyImpl.class.getName());
		assertFails(
				() -> new MyProps(p),
				IllegalPropertiesException.class,
				"property field in DESC must name a class, but was 'com.exedio.cope.util.PropertiesServiceWrongDefaultTest$MyServiceDoesNotExist'",
				ClassNotFoundException.class);
	}


	@SuppressWarnings("EmptyClass")
	static class MyService
	{
	}
	static final class MyImpl extends MyService
	{
		MyImpl(@SuppressWarnings("unused") final String parameter)
		{
		}
	}

	static class MyProps extends Properties
	{
		final ServiceFactory<MyService, String> field =
				valueService("field", MyService.class.getName() + "DoesNotExist", MyService.class, String.class);

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
