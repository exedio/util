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

package com.exedio.cope.util.junit;

import static java.util.TimeZone.getDefault;
import static java.util.TimeZone.setDefault;
import static org.junit.Assert.assertNotNull;

import java.util.TimeZone;
import org.junit.rules.ExternalResource;

public final class TimeZoneDefaultRule extends ExternalResource
{
	private TimeZone backup;

	public void set(final TimeZone zone)
	{
		if(backup!=null)
		{
			backup = getDefault();
			assertNotNull(backup);
		}

		setDefault(zone);
	}

	@Override
	protected void after()
	{
		if(backup!=null)
			setDefault(backup);
	}
}
