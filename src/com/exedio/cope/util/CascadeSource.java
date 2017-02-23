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

import com.exedio.cope.util.Properties.Source;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

final class CascadeSource
{
	static Source cascade(final Source... sources)
	{
		for(final Source source : sources)
			if(source==null)
				throw new NullPointerException();

		switch(sources.length)
		{
			case  0: return EMPTY;
			case  1: return sources[0];
			default: return new Cascade(sources);
		}
	}

	private static class Cascade implements Source
	{
		private final Source[] sources;

		Cascade(final Source... sources)
		{
			// TODO check for nested cascades
			// TODO check for nested empty

			// make a copy to avoid modifications afterwards
			this.sources = new Source[sources.length];
			System.arraycopy(sources, 0, this.sources, 0, sources.length);
		}

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

				result.append(source.toString());
			}
			return result.toString();
		}
	}

	private CascadeSource()
	{
		// prevent instantiation
	}
}
