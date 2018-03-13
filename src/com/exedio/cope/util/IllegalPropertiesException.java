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

public final class IllegalPropertiesException extends IllegalArgumentException
{
	private static final long serialVersionUID = 1l;

	private final String key;
	private final String sourceDescription;
	private final String detail;

	IllegalPropertiesException(final String key, final String sourceDescription, final String detail, final Throwable cause)
	{
		// Specifying message==null avoids detailMessage computed from cause in
		// Throwable constructor, not needed because getMessage is overridden anyway.
		super(null, cause);
		this.key = requireNonNull(key, "key");
		this.sourceDescription = sourceDescription;
		this.detail = requireNonNull(detail, "detail");
	}

	IllegalPropertiesException(final String prefix, final String sourceDescription, final IllegalPropertiesException cause)
	{
		// Specifying message==null avoids detailMessage computed from cause in
		// Throwable constructor, not needed because getMessage is overridden anyway.
		super(null, cause);
		this.key = prefix + cause.key;
		this.sourceDescription = sourceDescription;
		this.detail = cause.detail;
	}

	public String getKey()
	{
		return key;
	}

	public String getDetail()
	{
		return detail;
	}

	@Override
	public String getMessage()
	{
		return "property " + key + " in " + sourceDescription + ' ' + detail;
	}
}
