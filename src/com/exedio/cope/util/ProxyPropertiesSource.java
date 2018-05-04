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

import static java.util.Objects.requireNonNull;

import com.exedio.cope.util.Properties.Source;
import java.util.Collection;

/**
 * An proxy implementation of {@link Source}.
 *
 * All methods implementing {@link Source}
 * do forward to another {@link Source}.
 *
 * You may want to subclass this class instead of
 * implementing {@link Source} directly
 * to make your subclass cope with new methods
 * in {@link Source}.
 */
public abstract class ProxyPropertiesSource implements Source
{
	private final Source target;

	protected ProxyPropertiesSource(final Source target)
	{
		this.target = requireNonNull(target, "target");
	}

	protected final Source getTarget()
	{
		return target;
	}

	@Override
	public String get(final String key)
	{
		return target.get(key);
	}

	@Override
	public Collection<String> keySet()
	{
		return target.keySet();
	}

	@Override
	public Source reload()
	{
		final Source reloadedTarget = target.reload();
		return
				reloadedTarget==target
				? this
				: reload(reloadedTarget);
	}

	protected abstract ProxyPropertiesSource reload(Source reloadedTarget);

	@Override
	public String getDescription()
	{
		return target.getDescription();
	}

	@Override
	public String toString()
	{
		return target.toString();
	}
}
