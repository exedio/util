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

import jakarta.servlet.Filter;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.SessionCookieConfig;
import jakarta.servlet.SessionTrackingMode;
import jakarta.servlet.descriptor.JspConfigDescriptor;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Map;
import java.util.Set;
import org.opentest4j.AssertionFailedError;

public class AssertionFailedServletContext implements ServletContext
{
	@Override
	public String getInitParameter(final String name)
	{
		throw new AssertionFailedError();
	}

	@Override
	public String getServletContextName()
	{
		throw new AssertionFailedError();
	}

	@Override
	public Object getAttribute(final String arg0)
	{
		throw new AssertionFailedError();
	}

	@Override
	public Enumeration<String> getAttributeNames()
	{
		throw new AssertionFailedError();
	}

	@Override
	public ServletContext getContext(final String arg0)
	{
		throw new AssertionFailedError();
	}

	@Override
	public String getContextPath()
	{
		throw new AssertionFailedError();
	}

	@Override
	public Enumeration<String> getInitParameterNames()
	{
		throw new AssertionFailedError();
	}

	@Override
	public int getMajorVersion()
	{
		throw new AssertionFailedError();
	}

	@Override
	public String getMimeType(final String arg0)
	{
		throw new AssertionFailedError();
	}

	@Override
	public int getMinorVersion()
	{
		throw new AssertionFailedError();
	}

	@Override
	public RequestDispatcher getNamedDispatcher(final String arg0)
	{
		throw new AssertionFailedError();
	}

	@Override
	public String getRealPath(final String arg0)
	{
		throw new AssertionFailedError();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(final String arg0)
	{
		throw new AssertionFailedError();
	}

	@Override
	public URL getResource(final String arg0)
	{
		throw new AssertionFailedError();
	}

	@Override
	public InputStream getResourceAsStream(final String arg0)
	{
		throw new AssertionFailedError();
	}

	@Override
	public Set<String> getResourcePaths(final String arg0)
	{
		throw new AssertionFailedError();
	}

	@Override
	public String getServerInfo()
	{
		throw new AssertionFailedError();
	}

	@Override
	public void log(final String arg0)
	{
		throw new AssertionFailedError();
	}

	@Override
	public void log(final String arg0, final Throwable arg1)
	{
		throw new AssertionFailedError();
	}

	@Override
	public void removeAttribute(final String arg0)
	{
		throw new AssertionFailedError();
	}

	@Override
	public void setAttribute(final String arg0, final Object arg1)
	{
		throw new AssertionFailedError();
	}

	@Override
	public int getEffectiveMajorVersion()
	{
		throw new AssertionFailedError();
	}

	@Override
	public int getEffectiveMinorVersion()
	{
		throw new AssertionFailedError();
	}

	@Override
	public boolean setInitParameter(final String string, final String string1)
	{
		throw new AssertionFailedError();
	}

	@Override
	public ServletRegistration.Dynamic addServlet(final String string, final String string1)
	{
		throw new AssertionFailedError();
	}

	@Override
	public ServletRegistration.Dynamic addServlet(final String string, final Servlet srvlt)
	{
		throw new AssertionFailedError();
	}

	@Override
	public ServletRegistration.Dynamic addServlet(final String string, final Class<? extends Servlet> type)
	{
		throw new AssertionFailedError();
	}

	@Override
	public ServletRegistration.Dynamic addJspFile(final String servletName, final String jspFile)
	{
		throw new AssertionFailedError();
	}

	@Override
	public <T extends Servlet> T createServlet(final Class<T> type)
	{
		throw new AssertionFailedError();
	}

	@Override
	public ServletRegistration getServletRegistration(final String string)
	{
		throw new AssertionFailedError();
	}

	@Override
	public Map<String, ? extends ServletRegistration> getServletRegistrations()
	{
		throw new AssertionFailedError();
	}

	@Override
	public FilterRegistration.Dynamic addFilter(final String string, final String string1)
	{
		throw new AssertionFailedError();
	}

	@Override
	public FilterRegistration.Dynamic addFilter(final String string, final Filter filter)
	{
		throw new AssertionFailedError();
	}

	@Override
	public FilterRegistration.Dynamic addFilter(final String string, final Class<? extends Filter> type)
	{
		throw new AssertionFailedError();
	}

	@Override
	public <T extends Filter> T createFilter(final Class<T> type)
	{
		throw new AssertionFailedError();
	}

	@Override
	public FilterRegistration getFilterRegistration(final String string)
	{
		throw new AssertionFailedError();
	}

	@Override
	public Map<String, ? extends FilterRegistration> getFilterRegistrations()
	{
		throw new AssertionFailedError();
	}

	@Override
	public SessionCookieConfig getSessionCookieConfig()
	{
		throw new AssertionFailedError();
	}

	@Override
	public void setSessionTrackingModes(final Set<SessionTrackingMode> set)
	{
		throw new AssertionFailedError();
	}

	@Override
	public Set<SessionTrackingMode> getDefaultSessionTrackingModes()
	{
		throw new AssertionFailedError();
	}

	@Override
	public Set<SessionTrackingMode> getEffectiveSessionTrackingModes()
	{
		throw new AssertionFailedError();
	}

	@Override
	public void addListener(final String string)
	{
		throw new AssertionFailedError();
	}

	@Override
	public <T extends EventListener> void addListener(final T t)
	{
		throw new AssertionFailedError();
	}

	@Override
	public void addListener(final Class<? extends EventListener> type)
	{
		throw new AssertionFailedError();
	}

	@Override
	public <T extends EventListener> T createListener(final Class<T> type)
	{
		throw new AssertionFailedError();
	}

	@Override
	public JspConfigDescriptor getJspConfigDescriptor()
	{
		throw new AssertionFailedError();
	}

	@Override
	public ClassLoader getClassLoader()
	{
		throw new AssertionFailedError();
	}

	@Override
	public void declareRoles(final String... strings)
	{
		throw new AssertionFailedError();
	}

	@Override
	public String getVirtualServerName()
	{
		throw new AssertionFailedError();
	}

	@Override
	public int getSessionTimeout()
	{
		throw new AssertionFailedError();
	}

	@Override
	public void setSessionTimeout(final int sessionTimeout)
	{
		throw new AssertionFailedError();
	}

	@Override
	public String getRequestCharacterEncoding()
	{
		throw new AssertionFailedError();
	}

	@Override
	public void setRequestCharacterEncoding(final String encoding)
	{
		throw new AssertionFailedError();
	}

	@Override
	public String getResponseCharacterEncoding()
	{
		throw new AssertionFailedError();
	}

	@Override
	public void setResponseCharacterEncoding(final String encoding)
	{
		throw new AssertionFailedError();
	}
}
