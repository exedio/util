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

import com.exedio.cope.util.Properties.Source;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;

public final class Sources
{
	public static final Source view(final Properties properties, final String description)
	{
		return new Source(){
			@Override
			public String get(final String key)
			{
				checkKey(key);
				return properties.getProperty(key);
			}

			@Override
			public Collection<String> keySet()
			{
				final ArrayList<String> result = new ArrayList<>();
				for(final Enumeration<?> names = properties.propertyNames(); names.hasMoreElements(); )
					result.add((String)names.nextElement());
				return Collections.unmodifiableList(result);
			}

			@Override
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

	public static final Source load(final File file)
	{
		return view(loadProperties(file), file.getAbsolutePath());
	}

	public static final Properties loadProperties(final File file)
	{
		try
		{
			return loadPropertiesAndClose(new FileInputStream(file));
		}
		catch(final IOException e)
		{
			throw new RuntimeException("property file " + file.getAbsolutePath() + " not found.", e);
		}
	}

	public static final Source load(final URL url)
	{
		try
		{
			return view(loadPropertiesAndClose(url.openStream()), url.toString());
		}
		catch(final IOException e)
		{
			throw new RuntimeException("property url " + url.toString() + " not found.", e);
		}
	}

	private static final Properties loadPropertiesAndClose(final InputStream stream) throws IOException
	{
		final Properties result = new Properties();
		try
		{
			result.load(stream);
		}
		finally
		{
			stream.close();
		}
		return result;
	}

	public static Source cascade(final Source... sources)
	{
		return CascadeSource.cascade(sources);
	}

	/**
	 * Checks a key to be valid for calling {@link com.exedio.cope.util.Properties.Source#get(String)}.
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
