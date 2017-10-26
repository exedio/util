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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.Assert.fail;

import java.security.NoSuchAlgorithmException;
import org.junit.jupiter.api.Test;

public class MessageDigestUtilTest
{
	@Test void testMD5()
	{
		assertEquals("MD5", MessageDigestUtil.getInstance("MD5").getAlgorithm());
	}

	@Test void testNotFound()
	{
		try
		{
			MessageDigestUtil.getInstance("NIXUS");
			fail();
		}
		catch(final IllegalAlgorithmException e)
		{
			assertEquals("NIXUS", e.getAlgorithm());
			assertTrue(e.getMessage().startsWith("no such MessageDigest NIXUS, choose one of: "), e.getMessage());
			assertEquals(NoSuchAlgorithmException.class, e.getCause().getClass());
		}
	}

	@Test void testEmpty()
	{
		try
		{
			MessageDigestUtil.getInstance("");
			fail();
		}
		catch(final IllegalAlgorithmException e)
		{
			assertEquals("", e.getAlgorithm());
			assertTrue(e.getMessage().startsWith("no such MessageDigest , choose one of: "), e.getMessage());
			assertEquals(NoSuchAlgorithmException.class, e.getCause().getClass());
		}
	}

	@Test void testNull()
	{
		try
		{
			MessageDigestUtil.getInstance(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("algorithm", e.getMessage());
		}
	}
}
