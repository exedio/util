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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;

import com.exedio.cope.util.Properties.Source;

public final class Sources
{
	public static final Source view(final Properties properties, final String description)
	{
		return new Source(){
			public String get(final String key)
			{
				checkKey(key);
				return properties.getProperty(key);
			}

			public Collection<String> keySet()
			{
				final ArrayList<String> result = new ArrayList<String>();
				for(final Enumeration<?> names = properties.propertyNames(); names.hasMoreElements(); )
					result.add((String)names.nextElement());
				return Collections.unmodifiableList(result);
			}

			public String getDescription()
			{
				return description;
			}

			@Override
			public String toString()
			{
				return description;
			}
		};
	}

	public static Source cascade(final Source... sources)
	{
		return CascadeSource.cascade(sources);
	}

	/**
	 * Checks a key to be valid for calling {@link Properties.Source#get(String)}.
	 * @param key
	 */
	public static void checkKey(final String key)
	{
		if(key==null)
			throw new NullPointerException("key");
		if(key.length()==0)
			throw new IllegalArgumentException("key must not be empty");
	}

	private Sources()
	{
		// prevent instantiation
	}
}
