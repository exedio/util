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

import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.Duration;
import org.junit.jupiter.api.Test;

public class JobContextDefaultRequestsDeferralTest
{
	@Test void test()
	{
		final JC ctx = new JC();
		assertSame(Duration.ZERO, ctx.requestsDeferral());
	}

	private static final class JC implements JobContext
	{
		@Override
		public void stopIfRequested()
		{
			throw new AssertionError();
		}

		@Override
		public void sleepAndStopIfRequested(final Duration duration)
		{
			throw new AssertionError(duration);
		}

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
}
