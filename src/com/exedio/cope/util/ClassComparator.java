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

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;

/**
 * @deprecated This class is not used in the cope runtime library anymore.
 */
@Deprecated
public final class ClassComparator implements Comparator<Class<?>>, Serializable
{
	@Serial
	private static final long serialVersionUID = 1l;

	/**
	 * @deprecated This class is not used in the cope runtime library anymore.
	 */
	@Deprecated
	public static ClassComparator getInstance()
	{
		return new ClassComparator();
	}

	private ClassComparator()
	{
		// do not allow instantiation, is a singleton
	}

	@Override
	public int compare(final Class<?> c1, final Class<?> c2)
	{
		return c1.getName().compareTo(c2.getName());
	}
}
