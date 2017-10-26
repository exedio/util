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

import java.time.Duration;

/**
 * An proxy implementation of {@link JobContext}.
 *
 * All methods implementing {@link JobContext}
 * do forward to another {@link JobContext}.
 *
 * You may want to subclass this class instead of
 * implementing {@link JobContext} directly
 * to make your subclass cope with new methods
 * in {@link JobContext}.
 */
public class ProxyJobContext implements JobContext
{
	private final JobContext target;

	public ProxyJobContext(final JobContext target)
	{
		this.target = requireNonNull(target, "target");
	}

	@Override
	public void stopIfRequested() throws JobStop
	{
		target.stopIfRequested();
	}

	/**
	 * When overriding this method make sure you don't ignore the request
	 * of the {@code target}.
	 * You may want to use {@link #max(Duration, Duration)} for merging requests.
	 */
	@Override
	public Duration requestsDeferral()
	{
		return target.requestsDeferral();
	}

	protected static final Duration max(final Duration a, final Duration b)
	{
		return a.compareTo(b)>=0 ? a : b;
	}

	@Override
	public void sleepAndStopIfRequested(final Duration duration) throws JobStop
	{
		target.sleepAndStopIfRequested(duration);
	}


	// message

	@Override
	public boolean supportsMessage()
	{
		return target.supportsMessage();
	}

	@Override
	public void setMessage(final String message)
	{
		target.setMessage(message);
	}


	// progress

	@Override
	public boolean supportsProgress()
	{
		return target.supportsProgress();
	}

	@Override
	public void incrementProgress()
	{
		target.incrementProgress();
	}

	@Override
	public void incrementProgress(final int delta)
	{
		target.incrementProgress(delta);
	}


	// completeness

	@Override
	public boolean supportsCompleteness()
	{
		return target.supportsCompleteness();
	}

	@Override
	public void setCompleteness(final double completeness)
	{
		target.setCompleteness(completeness);
	}
}
