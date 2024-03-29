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

/**
 * An empty implementation of {@link JobContext}.
 * <p>
 * All methods implementing {@link JobContext}
 * do as little as possible as allowed by the
 * specification of {@link JobContext}.
 * <p>
 * You may want to subclass this class instead of
 * implementing {@link JobContext} directly
 * to make your subclass cope with new methods
 * in {@link JobContext}.
 */
public class EmptyJobContext implements JobContext
{
	/**
	 * This default implementation does nothing.
	 */
	@Override
	public void stopIfRequested() throws JobStop
	{
		// empty
	}


	// message

	/**
	 * This default implementation always returns <i>false</i>.
	 */
	@Override
	public boolean supportsMessage()
	{
		return false;
	}

	/**
	 * This default implementation does nothing.
	 */
	@Override
	public void setMessage(final String message)
	{
		// empty
	}


	// progress

	/**
	 * This default implementation always returns <i>false</i>.
	 */
	@Override
	public boolean supportsProgress()
	{
		return false;
	}

	/**
	 * This default implementation calls
	 * {@link #incrementProgress(int) incrementProgress}(1).
	 */
	@Override
	public void incrementProgress()
	{
		incrementProgress(1);
	}

	/**
	 * This default implementation does nothing.
	 */
	@Override
	public void incrementProgress(final int delta)
	{
		// empty
	}


	// completeness

	/**
	 * This default implementation always returns <i>false</i>.
	 */
	@Override
	public boolean supportsCompleteness()
	{
		return false;
	}

	/**
	 * This default implementation does nothing.
	 */
	@Override
	public void setCompleteness(final double completeness)
	{
		// empty
	}
}
