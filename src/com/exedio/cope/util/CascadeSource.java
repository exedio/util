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

import static com.exedio.cope.util.Sources.EMPTY;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.util.Properties.Source;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

final class CascadeSource
{
	static Source cascade(final Source... sources)
	{
		for(final Source source : sources)
			if(source==null)
				throw new NullPointerException();

		return switch(sources.length)
		{
			case 0  -> EMPTY;
			case 1  -> sources[0];
			default -> new Cascade(sources);
		};
	}

	private static class Cascade implements Source
	{
		private final Source[] sources;

		Cascade(final Source... sources)
		{
			// TODO check for nested empty

			// make a copy to avoid modifications afterwards
			final ArrayList<Source> sourcesList = new ArrayList<>(sources.length);
			for(final Source source : sources)
			{
				if(source instanceof Cascade)
					sourcesList.addAll(asList(((Cascade)source).sources));
				else
					sourcesList.add(source);
			}
			this.sources = sourcesList.toArray(EMPTY_SOURCES);
		}

		private static final Source[] EMPTY_SOURCES = new Source[0];

		@Override
		public String get(final String key)
		{
			Sources.checkKey(key);

			for(final Source source : sources)
			{
				final String value = source.get(key);
				if(value!=null)
					return value;
			}

			return null;
		}

		@Override
		public Collection<String> keySet()
		{
			final LinkedHashSet<String> result = new LinkedHashSet<>();
			for(final Source source : sources)
			{
				final Collection<String> keySet = source.keySet();
				if(keySet==null)
					return null;
				result.addAll(keySet);
			}
			return Collections.unmodifiableSet(result);
		}

		@Override
		public Source reload()
		{
			final Source[] sources = new Source[this.sources.length];
			Arrays.setAll(sources, i -> this.sources[i].reload());
			for(int i = 0; i<sources.length; i++)
				if(this.sources[i]!=sources[i])
					return new Cascade(sources);

			return this;
		}

		@Override
		public String getDescription()
		{
			final StringBuilder result = new StringBuilder();
			boolean first = true;
			for(final Source source : sources)
			{
				if(first)
					first = false;
				else
					result.append(" / ");

				result.append(source.getDescription());
			}
			return result.toString();
		}

		@Override
		public String toString()
		{
			final StringBuilder result = new StringBuilder();
			boolean first = true;
			for(final Source source : sources)
			{
				if(first)
					first = false;
				else
					result.append(" / ");

				result.append(source);
			}
			return result.toString();
		}
	}

	static List<Source> decascade(final Source source)
	{
		requireNonNull(source, "source");

		return source instanceof Cascade
				? List.of(((Cascade)source).sources)
				: singletonList(source);
	}


	private CascadeSource()
	{
		// prevent instantiation
	}
}
