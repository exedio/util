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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;

public final class MessageDigestUtil
{
	/**
	 * @throws IllegalAlgorithmException if {@code algorithm} does not specify a MessageDigest
	 */
	public static MessageDigest getInstance(final String algorithm)
	{
		requireNonNull(algorithm, "algorithm");
		try
		{
			return MessageDigest.getInstance(algorithm);
		}
		catch(final NoSuchAlgorithmException e)
		{
			final StringBuilder sb = new StringBuilder("no such MessageDigest ");
			sb.append(algorithm);
			sb.append(", choose one of: ");

			boolean first = true;
			Provider lastProvider = null;
			for(final Provider provider : Security.getProviders())
			{
				for(final Provider.Service service : provider.getServices())
				{
					if("MessageDigest".equals(service.getType()))
					{
						if(lastProvider!=provider)
						{
							sb.append('(');
							sb.append(provider.getName());
							sb.append(')');
							sb.append(':');
							lastProvider = provider;
							first = true;
						}

						if(first)
							first = false;
						else
							sb.append(',');

						sb.append(service.getAlgorithm());
					}
				}
			}

			throw new IllegalAlgorithmException(algorithm, sb.toString(), e);
		}
	}


	private MessageDigestUtil()
	{
		// prevent instantiation
	}
}
