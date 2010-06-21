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

public final class Cast
{
	private Cast()
	{
		// prevent instantiation
	}

	/**
	 * Does the same as {@link Class#cast(Object)},
	 * but throws a ClassCastException
	 * with a more verbose message.
	 */
	public static <X> X verboseCast(final Class<X> clazz, final Object o)
	{
		// NOTE:
		// This code is redundant to the following call to Class#cast(Object),
		// but creates an exception with a much more verbose message.
		if(o!=null && !clazz.isInstance(o))
			throw new ClassCastException("expected a " + clazz.getName() + ", but was a " + o.getClass().getName());

		return clazz.cast(o);
	}

	public static <E> Collection<E> castElements(final Class<E> clazz, final Collection<?> c)
	{
		if(c==null)
			return null;

		final ArrayList<E> result = new ArrayList<E>(c.size());
		for(final Object o : c)
			result.add(verboseCast(clazz, o));
		return result;
	}
}
