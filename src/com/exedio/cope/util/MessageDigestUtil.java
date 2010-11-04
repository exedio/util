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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;

public final class MessageDigestUtil
{
	public static MessageDigest getInstance(final String algorithm)
	{
		try
		{
			return MessageDigest.getInstance(algorithm);
		}
		catch(final NoSuchAlgorithmException e)
		{
			final StringBuilder bf = new StringBuilder("no such MessageDigest ");
			bf.append(algorithm);
			bf.append(", choose one of: ");

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
							bf.append('(');
							bf.append(provider.getName());
							bf.append(')');
							bf.append(':');
							lastProvider = provider;
							first = true;
						}

						if(first)
							first = false;
						else
							bf.append(',');

						bf.append(service.getAlgorithm());
					}
				}
			}

			throw new IllegalArgumentException(bf.toString(), e);
		}
	}


	private MessageDigestUtil()
	{
		// prevent instantiation
	}
}
