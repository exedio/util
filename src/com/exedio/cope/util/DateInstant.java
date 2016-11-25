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

import java.time.Instant;
import java.util.Date;

public final class DateInstant
{
	/**
	 * @see Date#toInstant()
	 */
	public static Instant to(final Date day)
	{
		return day!=null ? day.toInstant() : null;
	}

	/**
	 * @see Date#from(java.time.Instant)
	 */
	public static Date from(final Instant instant)
	{
		return instant!=null ? Date.from(instant) : null;
	}


	private DateInstant()
	{
		// prevent instantiation
	}
}
