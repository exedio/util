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
		this.target = target;

		if(target==null)
			throw new NullPointerException("target");
	}

	public void stopIfRequested() throws JobStop
	{
		target.stopIfRequested();
	}

	@Deprecated
	public boolean requestedToStop()
	{
		return target.requestedToStop();
	}


	// message

	public boolean supportsMessage()
	{
		return target.supportsMessage();
	}

	public void setMessage(final String message)
	{
		target.setMessage(message);
	}


	// progress

	public boolean supportsProgress()
	{
		return target.supportsProgress();
	}

	public void incrementProgress()
	{
		target.incrementProgress();
	}

	public void incrementProgress(final int delta)
	{
		target.incrementProgress(delta);
	}


	// completeness

	public boolean supportsCompleteness()
	{
		return target.supportsCompleteness();
	}

	public void setCompleteness(final double completeness)
	{
		target.setCompleteness(completeness);
	}
}
