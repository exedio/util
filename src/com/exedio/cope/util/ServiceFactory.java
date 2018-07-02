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

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Objects;

public final class ServiceFactory<T,P>
{
	private final Constructor<? extends T> constructor;
	private final Properties properties;

	ServiceFactory(
			final Constructor<? extends T> constructor,
			final Properties properties)
	{
		this.constructor = constructor;
		this.properties = properties;
		constructor.setAccessible(true);
	}

	public Class<? extends T> getServiceClass()
	{
		return constructor.getDeclaringClass();
	}

	public T newInstance(final P parameter)
	{
		try
		{
			return
					properties!=null
					? constructor.newInstance(parameter, properties)
					: constructor.newInstance(parameter);
		}
		catch(final ReflectiveOperationException e)
		{
			throw new RuntimeException(constructor.toGenericString(), e);
		}
	}

	@Override
	public boolean equals(final Object other)
	{
		return
				other instanceof ServiceFactory &&
				constructor.equals(((ServiceFactory<?,?>)other).constructor) &&
				Objects.equals(properties, ((ServiceFactory<?,?>)other).properties); // NOTE: Properties.equals is not (yet) implemented, so this works just for null/non-null and identity
	}

	@Override
	@SuppressWarnings("ObjectInstantiationInEqualsHashCode") // OK: called rarely
	public int hashCode()
	{
		return
				constructor.hashCode() ^
				Arrays.hashCode(constructor.getParameterTypes()) ^ // needed because Constructor#hashCode considers declaring class only
				Objects.hashCode(properties) ^ // NOTE: Properties.hashCode is not (yet) implemented, so this works just for null/non-null and identity
				926792354;
	}

	@Override
	public String toString()
	{
		return getServiceClass().getName();
	}
}
