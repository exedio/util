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

import java.time.Duration;

/**
 * An implementation of {@link JobContext} where
 * all methods do fail with a
 * {@link AssertionError}.
 * <p>
 * You may want to subclass this class instead of
 * implementing {@link JobContext} directly
 * to make your subclass cope with new methods
 * in {@link JobContext}.
 */
public class AssertionErrorJobContext implements JobContext
{
	@Override
	public void stopIfRequested()
	{
		throw new AssertionError();
	}

	@Override
	public Duration requestsDeferral()
	{
		throw new AssertionError();
	}

	@Override
	public void sleepAndStopIfRequested(final Duration duration)
	{
		throw new AssertionError(duration);
	}


	// message

	@Override
	public boolean supportsMessage()
	{
		throw new AssertionError();
	}

	@Override
	public void setMessage(final String message)
	{
		throw new AssertionError(message);
	}


	// progress

	@Override
	public boolean supportsProgress()
	{
		throw new AssertionError();
	}

	@Override
	public void incrementProgress()
	{
		throw new AssertionError();
	}

	@Override
	public void incrementProgress(final int delta)
	{
		throw new AssertionError(delta);
	}


	// completeness

	@Override
	public boolean supportsCompleteness()
	{
		throw new AssertionError();
	}

	@Override
	public void setCompleteness(final double completeness)
	{
		throw new AssertionError(completeness);
	}
}
