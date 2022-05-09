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

package com.exedio.cope.util.servlet;

import static com.exedio.cope.util.Sources.checkKey;
import static com.exedio.cope.util.Sources.load;
import static com.exedio.cope.util.Sources.reloadable;

import com.exedio.cope.util.PrefixSource;
import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.util.ProxyPropertiesSource;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import javax.servlet.ServletContext;

public final class ServletSource
{
	private ServletSource()
	{
		// prevent instantiation
	}

	public static Source create(final ServletContext context)
	{
		final String contextPath = context.getContextPath();
		final String prefix;
		if(contextPath==null)
			prefix = null;
		else if(contextPath.isEmpty())
			prefix = "root.";
		else if(contextPath.startsWith("/"))
			prefix = contextPath.substring(1) + '.';
		else
			prefix = contextPath + '.';

		final Source initParam = PrefixSource.wrap(new InitParameter(context), prefix);
		final Source keys;
		{
			final String file = initParam.get("com.exedio.cope.servletutil.ServletSource.propertiesFile");
			keys =
				file!=null
				? reloadable(() -> load(new File(file)))
				: initParam;
		}

		return new ContextPath(contextPath, keys);
	}

	private static final class InitParameter implements Source
	{
		private final ServletContext context;

		InitParameter(final ServletContext context)
		{
			this.context = context;
		}

		@Override
		public String get(final String key)
		{
			return context.getInitParameter(key);
		}

		@Override
		public Collection<String> keySet()
		{
			final ArrayList<String> result = new ArrayList<>();
			for(final Enumeration<?> e = context.getInitParameterNames(); e.hasMoreElements(); )
				result.add((String)e.nextElement());
			return result;
		}

		@Override
		public String getDescription()
		{
			return toString();
		}

		@Override
		public String toString()
		{
			return "ServletContext '" + context.getContextPath() + '\'';
		}
	}

	private static class ContextPath extends ProxyPropertiesSource
	{
		private final String contextPath;

		ContextPath(final String contextPath, final Source initParam)
		{
			super(initParam);
			this.contextPath = contextPath;
		}

		private static final String KEY = "contextPath";

		@Override
		public String get(final String key)
		{
			checkKey(key);

			if(KEY.equals(key))
				return contextPath;

			return super.get(key);
		}

		@Override
		public Collection<String> keySet()
		{
			final ArrayList<String> result = new ArrayList<>();
			result.add(KEY);
			result.addAll(super.keySet());
			return Collections.unmodifiableList(result);
		}

		@Override
		protected ContextPath reload(final Source reloadedTarget)
		{
			return new ContextPath(contextPath, reloadedTarget);
		}
	}
}
