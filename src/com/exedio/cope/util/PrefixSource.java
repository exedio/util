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

import com.exedio.cope.util.Properties.Source;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public final class PrefixSource implements Source
{
	public static Source wrap(final Source source, final String prefix)
	{
		if(prefix==null || prefix.isEmpty())
			return source;

		if(source instanceof PrefixSource)
		{
			final PrefixSource ps =(PrefixSource)source;
			return new PrefixSource(ps.source, ps.prefix + prefix);
		}

		return new PrefixSource(source, prefix);
	}

	private final Source source;
	private final String prefix;

	public PrefixSource(final Source source, final String prefix)
	{
		if(source==null)
			throw new NullPointerException("source");
		if(prefix==null)
			throw new NullPointerException("prefix");
		if(prefix.isEmpty())
			throw new IllegalArgumentException("prefix");
		this.source = source;
		this.prefix = prefix;
	}

	@Override
	public String get(final String key)
	{
		Sources.checkKey(key);
		return source.get(prefix + key);
	}

	@Override
	public Collection<String> keySet()
	{
		final Collection<String> sourceKeySet = source.keySet();
		if(sourceKeySet==null)
			return null;

		final ArrayList<String> result = new ArrayList<>();
		for(final String key : sourceKeySet)
			if(key!=null && key.startsWith(prefix))
				result.add(key.substring(prefix.length()));
		return Collections.unmodifiableList(result);
	}

	@Override
	public PrefixSource reload()
	{
		return new PrefixSource(source.reload(), prefix);
	}

	@Override
	public String getDescription()
	{
		final String sourceDescription = source.getDescription();
		return
			sourceDescription!=null
			? (sourceDescription + " (prefix " + prefix + ')')
			: ("unknown prefix " + prefix);
	}

	@SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE") // happens in code generated for plus operator
	@Override
	public String toString()
	{
		final String sourceResult = source.toString();
		return
			sourceResult!=null
			? (sourceResult + " (prefix " + prefix + ')')
			: null;
	}
}
