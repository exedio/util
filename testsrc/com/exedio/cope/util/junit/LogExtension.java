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

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.LogbackException;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.status.Status;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.opentest4j.AssertionFailedError;
import org.slf4j.LoggerFactory;

@SuppressWarnings("AbstractClassWithoutAbstractMethods") // OK
public abstract class LogExtension implements AfterEachCallback, ParameterResolver
{
	private final Logger logger;

	protected LogExtension(final Class<?> clazz)
	{
		logger = (Logger)LoggerFactory.getLogger(requireNonNull(clazz, "clazz"));
	}

	@Override
	public final boolean supportsParameter(
			final ParameterContext parameterContext,
			final ExtensionContext extensionContext)
	{
		return getClass()==parameterContext.getParameter().getType();
	}

	@Override
	public final Object resolveParameter(
			final ParameterContext parameterContext,
			final ExtensionContext extensionContext)
	{
		try
		{
			return parameterContext.getParameter().getType().getDeclaredConstructor().newInstance();
		}
		catch(final ReflectiveOperationException e)
		{
			throw new ParameterResolutionException(null, e);
		}
	}

	public final void start()
	{
		assertNull(appender);
		//noinspection NestedAssignment
		logger.addAppender(appender = new Appender());
	}

	@Override
	public final void afterEach(final ExtensionContext context)
	{
		if(appender!=null)
		{
			logger.detachAppender(appender);
			appender = null;
		}
	}

	public final void assertWarn(final String msg)
	{
		assertMessage(Level.WARN, msg);
	}

	private void assertMessage(
			final Level level,
			final String msg)
	{
		assertTrue(!events().isEmpty(), "empty");
		final ILoggingEvent event = events().remove(0);
		assertAll(
				() -> assertEquals(level, event.getLevel()),
				() -> assertEquals(msg, event.getFormattedMessage()));
	}

	public final void assertEmpty()
	{
		assertEquals(List.of(), events());
	}

	private List<ILoggingEvent> events()
	{
		assertNotNull(appender, "not yet started");
		return appender.events;
	}

	private Appender appender;

	private final class Appender implements ch.qos.logback.core.Appender<ILoggingEvent>
	{
		final List<ILoggingEvent> events = new ArrayList<>();

		@Override
		public void doAppend(final ILoggingEvent o) throws LogbackException
		{
			events.add(o);
		}

		@Override
		public String getName()
		{
			throw new AssertionFailedError();
		}

		@Override
		public void setName(final String s)
		{
			throw new AssertionFailedError();
		}

		@Override
		public void setContext(final Context context)
		{
			throw new AssertionFailedError();
		}

		@Override
		public Context getContext()
		{
			throw new AssertionFailedError();
		}

		@Override
		public void addStatus(final Status status)
		{
			throw new AssertionFailedError();
		}

		@Override
		public void addInfo(final String s)
		{
			throw new AssertionFailedError();
		}

		@Override
		public void addInfo(final String s, final Throwable throwable)
		{
			throw new AssertionFailedError();
		}

		@Override
		public void addWarn(final String s)
		{
			throw new AssertionFailedError();
		}

		@Override
		public void addWarn(final String s, final Throwable throwable)
		{
			throw new AssertionFailedError();
		}

		@Override
		public void addError(final String s)
		{
			throw new AssertionFailedError();
		}

		@Override
		public void addError(final String s, final Throwable throwable)
		{
			throw new AssertionFailedError();
		}

		@Override
		public void addFilter(final Filter<ILoggingEvent> filter)
		{
			throw new AssertionFailedError();
		}

		@Override
		public void clearAllFilters()
		{
			throw new AssertionFailedError();
		}

		@Override
		public List<Filter<ILoggingEvent>> getCopyOfAttachedFiltersList()
		{
			throw new AssertionFailedError();
		}

		@Override
		public FilterReply getFilterChainDecision(final ILoggingEvent iLoggingEvent)
		{
			throw new AssertionFailedError();
		}

		@Override
		public void start()
		{
			throw new AssertionFailedError();
		}

		@Override
		public void stop()
		{
			throw new AssertionFailedError();
		}

		@Override
		public boolean isStarted()
		{
			throw new AssertionFailedError();
		}
	}
}
