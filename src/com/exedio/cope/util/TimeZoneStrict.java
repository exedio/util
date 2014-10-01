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

import java.util.TimeZone;

public final class TimeZoneStrict
{
	/**
	 * Workaround for {@link TimeZone#getTimeZone(String)},
	 * as this method always return a zone zone, even for
	 * unknown IDs.
	 */
	public static TimeZone getTimeZone(final String ID)
	{
		if(ID==null)
			throw new NullPointerException();
		if(ID.isEmpty())
			throw new IllegalArgumentException("ID must not be empty");

		final TimeZone zone = TimeZone.getTimeZone(ID);
		if(!ID.equals(zone.getID()))
			throw new IllegalStateException(ID + '#' + zone.getID());
		return zone;
	}

	private TimeZoneStrict()
	{
		// prevent instantiation
	}
}
