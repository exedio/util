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

import com.exedio.cope.util.Properties.Factory;
import com.exedio.cope.util.Properties.Source;
import java.io.Serial;

public final class PropertiesInstance<P extends Properties>
{
	public static <P extends Properties> PropertiesInstance<P> create(final Factory<P> factory)
	{
		return new PropertiesInstance<>(factory);
	}

	private final Factory<P> factory;
	private volatile P value = null;

	private PropertiesInstance(final Factory<P> factory)
	{
		this.factory = requireNonNull(factory, "factory");
	}

	public P create(final Source source)
	{
		return factory.create(requireNonNull(source, "source"));
	}

	/**
	 * @return Returns the instance set.
	 */
	public P set(final Source source)
	{
		return set(create(source));
	}

	/**
	 * @return Returns the value of the parameter {@code newValue}.
	 */
	public P set(final P newValue)
	{
		value = requireNonNull(newValue, "newValue");
		return newValue;
	}

	public void remove()
	{
		value = null;
	}

	/**
	 * @throws NotSetException if no value has been set before.
	 */
	public P get()
	{
		final P result = value;
		if(result==null)
			throw new NotSetException();
		return result;
	}

	public static final class NotSetException extends IllegalStateException
	{
		@Serial
		private static final long serialVersionUID = 1L;

		NotSetException() { } // just make it package private
	}
}
