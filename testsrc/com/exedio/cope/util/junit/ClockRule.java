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

import static com.exedio.cope.util.Clock.clearOverride;

import com.exedio.cope.util.Clock;
import com.exedio.cope.util.Clock.Strategy;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

public final class ClockRule
{
	private ClockRule()
	{
		// just make private
	}

	@SuppressWarnings("static-method")
	public void override(final Strategy strategy)
	{
		Clock.override(strategy);
	}

	@SuppressWarnings("static-method")
	public void clear()
	{
		clearOverride();
	}

	public static final class Extension implements AfterEachCallback, ParameterResolver
	{
		private Extension()
		{
			// just make private
		}

		@Override
		public boolean supportsParameter(
				final ParameterContext parameterContext,
				final ExtensionContext extensionContext)
		{
			return ClockRule.class==parameterContext.getParameter().getType();
		}

		@Override
		public Object resolveParameter(
				final ParameterContext parameterContext,
				final ExtensionContext extensionContext)
		{
			return new ClockRule();
		}

		@Override
		public void afterEach(final ExtensionContext context)
		{
			clearOverride();
		}
	}
}
