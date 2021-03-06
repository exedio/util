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

public final class PoolProperties extends Properties
{
	final int idleInitial;
	final int idleLimit;

	public static Factory<PoolProperties> factory(final int idleLimitDefault)
	{
		return source -> new PoolProperties(source, idleLimitDefault);
	}

	private PoolProperties(final Source source, final int idleLimitDefault)
	{
		super(source);
		final String idleInitialKey = "idleInitial";
		final String idleLimitKey   = "idleLimit";

		this.idleInitial = value(idleInitialKey, 0, 0);
		this.idleLimit   = value(idleLimitKey,   idleLimitDefault, 0);
		if(idleInitial>idleLimit)
			throw newException(
					idleInitialKey,
					"must be less or equal " + idleLimitKey + '=' + idleLimit + ", " +
					"but was " + idleInitial);
	}

	public int getIdleInitial()
	{
		return idleInitial;
	}

	public int getIdleLimit()
	{
		return idleLimit;
	}
}
