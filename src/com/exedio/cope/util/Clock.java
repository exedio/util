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

import java.util.Date;

public final class Clock
{
	private static volatile Strategy strategy = null;

	public static Date newDate()
	{
		return new Date(currentTimeMillis());
	}

	public static long currentTimeMillis()
	{
		final Strategy strategy = Clock.strategy;
		return
			(strategy!=null)
			? strategy.currentTimeMillis()
			: System.currentTimeMillis();
	}

	@FunctionalInterface
	public interface Strategy
	{
		long currentTimeMillis();
	}

	public static void override(final Strategy strategy)
	{
		Clock.strategy = requireNonNull(strategy, "strategy");
	}

	public static void clearOverride()
	{
		strategy = null;
	}

	private Clock()
	{
		// prevent instantiation
	}
}
