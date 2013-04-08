/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package com.exedio.copedemo.feature.util;

import static com.exedio.cope.util.Properties.EMPTY_SOURCE;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.util.Sources;

public final class CascadeSource
{
	public static Source cascade(final Source... sources)
	{
		for(final Source source : sources)
			if(source==null)
				throw new NullPointerException();

		switch(sources.length)
		{
			case  0: return EMPTY_SOURCE;
			case  1: return sources[0];
			default:	return new Cascade(sources);
		}
	}

	private static class Cascade implements Source
	{
		private final Source[] sources;

		Cascade(final Source... sources)
		{
			// TODO check for nested cascades
			// TODO check for nested empty
			this.sources = sources;
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
			final LinkedHashSet<String> result = new LinkedHashSet<String>();
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
