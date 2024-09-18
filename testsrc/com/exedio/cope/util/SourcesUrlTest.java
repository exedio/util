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

import static com.exedio.cope.junit.Assert.assertFails;
import static com.exedio.cope.junit.CopeAssert.assertContains;
import static com.exedio.cope.junit.CopeAssert.assertUnmodifiable;
import static com.exedio.cope.util.Sources.load;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.exedio.cope.util.Properties.Source;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import org.junit.jupiter.api.Test;

public class SourcesUrlTest
{
	@Test void testIt()
	{
		final URL url = SourcesUrlTest.class.getResource("sourcesUrlTest.properties");
		assertNotNull(url);
		final Source s = load(url);
		assertFails(() ->
			s.get(null),
			NullPointerException.class, "key");
		assertFails(() ->
			s.get(""),
			IllegalArgumentException.class, "key must not be empty");
		assertEquals(null, s.get("xxx"));
		assertEquals("testValue1", s.get("testKey1"));
		assertEquals("testValue2", s.get("testKey2"));
		assertContains("testKey1", "testKey2", s.keySet());
		assertUnmodifiable(s.keySet());
		assertEquals(url.toString(), s.getDescription());
		assertEquals(url.toString(), s.toString());
	}

	@Test void testNotExists()
	{
		assertFails(() ->
			load(new URI("https://sourcetest.invalid/sourcesUrlTest.properties").toURL()),
			RuntimeException.class,
			"property url https://sourcetest.invalid/sourcesUrlTest.properties not found.",
			UnknownHostException.class);
	}

	@Test void testNull()
	{
		//noinspection DataFlowIssue
		assertFails(() ->
			load((URL)null),
			NullPointerException.class,
			"Cannot invoke \"java.net.URL.openStream()\" " +
			"because \"url\" is null");
	}
}
