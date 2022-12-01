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

import static com.exedio.cope.util.Check.requireNonEmpty;
import static com.exedio.cope.util.Clock.currentTimeMillis;

import com.exedio.cope.util.Properties.Source;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.function.Supplier;

public final class Sources
{
	public static final Source EMPTY = new EmptySource();

	private static final class EmptySource implements Source
	{
		EmptySource()
		{
			// empty
		}

		@Override
		public String get(final String key)
		{
			checkKey(key);
			return null;
		}

		@Override
		public Collection<String> keySet()
		{
			return Collections.emptyList();
		}

		@Override
		public String getDescription()
		{
			return "empty";
		}

		@Override
		public String toString()
		{
			return getClass().getSimpleName();
		}
	}

	public static Source view(final Properties properties, final String description)
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
				return Collections.unmodifiableSet(properties.stringPropertyNames());
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

	public static Source load(final File file)
	{
		return load(file.toPath());
	}

	public static Source load(final Path path)
	{
		return view(loadProperties(path), path.toAbsolutePath().toString());
	}

	public static Source loadIfExists(final Path path)
	{
		final Properties properties;
		try
		{
			properties = loadPropertiesAndClose(Files.newInputStream(path));
		}
		catch(final NoSuchFileException e)
		{
			return EMPTY;
		}
		catch(final IOException e)
		{
			throw new RuntimeException("property file " + path.toAbsolutePath() + " failed to load.", e);
		}
		return view(properties, path.toAbsolutePath().toString());
	}

	public static Properties loadProperties(final File file)
	{
		return loadProperties(file.toPath());
	}

	private static Properties loadProperties(final Path path)
	{
		try
		{
			return loadPropertiesAndClose(Files.newInputStream(path));
		}
		catch(final IOException e)
		{
			throw new RuntimeException("property file " + path.toAbsolutePath() + " not found.", e);
		}
	}

	public static Source load(final URL url)
	{
		try
		{
			return view(loadPropertiesAndClose(url.openStream()), url.toString());
		}
		catch(final IOException e)
		{
			throw new RuntimeException("property url " + url + " not found.", e);
		}
	}

	private static Properties loadPropertiesAndClose(final InputStream stream) throws IOException
	{
		final Properties result = new Properties();
		try(stream)
		{
			result.load(stream);
		}
		return result;
	}

	public static Source cascade(final Source... sources)
	{
		return CascadeSource.cascade(sources);
	}

	public static List<Source> decascade(final Source source)
	{
		return CascadeSource.decascade(source);
	}


	public static Source reloadable(final Supplier<Source> sourceSupplier)
	{
		return new Reloadable(sourceSupplier, 0);
	}

	private static final class Reloadable extends ProxyPropertiesSource
	{
		final Supplier<Source> sourceSupplier;
		final long reloadedMillis = currentTimeMillis();
		final int reloadedCount;

		Reloadable(
				final Supplier<Source> sourceSupplier,
				final int reloadedCount)
		{
			super(sourceSupplier.get());
			this.sourceSupplier = sourceSupplier;
			this.reloadedCount = reloadedCount;
		}

		@Override
		public Source reload()
		{
			return new Reloadable(sourceSupplier, reloadedCount+1);
		}

		@Override
		protected ProxyPropertiesSource reload(final Source reloadedTarget)
		{
			throw new AssertionError();
		}

		@Override
		public String getDescription()
		{
			final SimpleDateFormat df = new SimpleDateFormat(
					"(yyyy-MM-dd HH:mm:ss.SSS z (Z)" + (reloadedCount>0 ? (" 'reload' " + reloadedCount) : "") + ")",
					Locale.ENGLISH);
			return
					super.getDescription() +
					df.format(new Date(reloadedMillis));
		}

		@Override
		public String toString()
		{
			return super.toString() + "(reloadable)";
		}
	}


	public static final Source SYSTEM_PROPERTIES = new SystemPropertySource();

	private static final class SystemPropertySource implements Source
	{
		SystemPropertySource()
		{
			// empty
		}

		@Override
		public String get(final String key)
		{
			checkKey(key);
			return System.getProperty(key);
		}

		@Override
		public Collection<String> keySet()
		{
			return null;
		}

		@Override
		public String getDescription()
		{
			return "java.lang.System.getProperty";
		}

		@Override
		public String toString()
		{
			return getClass().getSimpleName();
		}
	}


	/**
	 * Checks a key to be valid for calling {@link Source#get(String)}.
	 */
	public static void checkKey(final String key)
	{
		requireNonEmpty(key, "key");
	}

	private Sources()
	{
		// prevent instantiation
	}
}
