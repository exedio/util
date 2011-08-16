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

import com.exedio.cope.util.Properties.Source;

public final class PrefixSource implements Source
{
	public static Source wrap(final Source source, final String prefix)
	{
		return
			(prefix!=null && prefix.length()>0)
			? new PrefixSource(source, prefix)
			: source;
	}

	private final Source source;
	private final String prefix;

	public PrefixSource(final Source source, final String prefix)
	{
		if(source==null)
			throw new NullPointerException("source");
		if(prefix==null)
			throw new NullPointerException("prefix");
		if(prefix.length()==0)
			throw new IllegalArgumentException("prefix");
		this.source = source;
		this.prefix = prefix;
	}

	public String get(final String key)
	{
		if(key==null)
			throw new NullPointerException("key");
		if(key.length()==0)
			throw new IllegalArgumentException("key must not be empty");

		return source.get(prefix + key);
	}

	public Collection<String> keySet()
	{
		final Collection<String> sourceKeySet = source.keySet();
		if(sourceKeySet==null)
			return null;

		final ArrayList<String> result = new ArrayList<String>();
		for(final String key : sourceKeySet)
			if(key!=null && key.startsWith(prefix))
				result.add(key.substring(prefix.length()));
		return Collections.unmodifiableList(result);
	}

	public String getDescription()
	{
		final String sourceDescription = source.getDescription();
		return
			sourceDescription!=null
			? (sourceDescription + " (prefix " + prefix + ')')
			: ("unknown prefix " + prefix);
	}

	@edu.umd.cs.findbugs.annotations.SuppressWarnings("RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE") // happens in code generated for plus operator
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
