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
import java.util.Collection;

/**
 * An implementation of {@link Source} where
 * all methods do fail with a
 * {@link AssertionError}.
 * <p>
 * You may want to subclass this class instead of
 * implementing {@link Source} directly
 * to make your subclass cope with new methods
 * in {@link Source}.
 */
public class AssertionErrorPropertiesSource implements Source
{
	@Override
	public String get(final String key)
	{
		throw new AssertionError(key);
	}

	@Override
	public Collection<String> keySet()
	{
		throw new AssertionError();
	}

	@Override
	public Source reload()
	{
		throw new AssertionError();
	}

	@Override
	public String getDescription()
	{
		throw new AssertionError();
	}

	@Override
	public String toString()
	{
		throw new AssertionError();
	}
}
