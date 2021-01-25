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
import static com.exedio.cope.junit.EqualsAssert.assertEqualsAndHash;
import static com.exedio.cope.junit.EqualsAssert.assertNotEqualsAndHash;
import static com.exedio.cope.util.Hex.encodeLower;
import static com.exedio.cope.util.MessageDigestUtilTest.assertThrowsIllegalAlgorithm;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import com.exedio.cope.junit.CopeAssert;
import java.security.MessageDigest;
import org.junit.jupiter.api.Test;

public class MessageDigestFactoryTest
{
	@Test void algorithmNull()
	{
		assertFails(() ->
			new MessageDigestFactory(null),
			 NullPointerException.class, "algorithm");
	}

	@Test void algorithmEmpty()
	{
		assertThrowsIllegalAlgorithm(() ->
			new MessageDigestFactory(""),
			"");
	}

	@Test void algorithmNotFound()
	{
		assertThrowsIllegalAlgorithm(() ->
			new MessageDigestFactory("NIXUS"),
			"NIXUS");
	}

	@Test void testSHA512()
	{
		final MessageDigestFactory mdf = new MessageDigestFactory("SHA-512");
		assertEquals("SHA-512", mdf.getAlgorithm());
		assertEquals("SHA-512", mdf.toString());
		assertEquals( 64, mdf.getLength());
		assertEquals(128, mdf.getLengthHex());
		assertDigest(
				"cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e",
				"af9ed2de700433b803240a552b41b5a472a6ef3fe1431a722b2063c75e9f07451f67a28e37d09cde769424c96aea6f8971389db9e1993d6c565c3c71b855723c",
				mdf);
	}

	@Test void testMD5()
	{
		final MessageDigestFactory mdf = new MessageDigestFactory("MD5");
		assertEquals("MD5", mdf.getAlgorithm());
		assertEquals("MD5", mdf.toString());
		assertEquals(16, mdf.getLength());
		assertEquals(32, mdf.getLengthHex());
		assertDigest(
				"d41d8cd98f00b204e9800998ecf8427e",
				"a3cca2b2aa1e3b5b3b5aad99a8529074",
				mdf);
	}

	private static void assertDigest(
			final String empty,
			final String franz,
			final MessageDigestFactory mdf)
	{
		assertEquals(empty, encodeLower(mdf.getDigestForEmptyByteSequence()));
		assertEquals(empty, mdf.getDigestForEmptyByteSequenceHex());

		final byte[] franzInput = "Franz jagt im komplett verwahrlosten Taxi quer durch Bayern".getBytes(US_ASCII);

		assertEquals(franz, encodeLower(mdf.digest(franzInput)));
		assertEquals(empty, encodeLower(mdf.digest(new byte[0])));

		final MessageDigest md = mdf.newInstance();
		md.update(franzInput);
		assertEquals(franz, encodeLower(md.digest()));

		assertEquals(franz, encodeLower(md.digest(franzInput)));
		assertEquals(empty, encodeLower(md.digest(new byte[0])));
	}

	@Test void testDigestForEmptyByteSequenceCopied()
	{
		final MessageDigestFactory mdf = new MessageDigestFactory("MD5");
		final byte[] digest = mdf.getDigestForEmptyByteSequence();
		assertEquals("d41d8cd98f00b204e9800998ecf8427e", encodeLower(digest));

		digest[0] = -1;
		assertEquals("ff1d8cd98f00b204e9800998ecf8427e", encodeLower(digest));
		assertEquals("d41d8cd98f00b204e9800998ecf8427e", encodeLower(mdf.getDigestForEmptyByteSequence()));
	}

	@Test void testEquals()
	{
		assertEqualsAndHash(
				new MessageDigestFactory("SHA-512"),
				new MessageDigestFactory("SHA-512"));
		assertNotEqualsAndHash(
				new MessageDigestFactory("SHA-512"),
				new MessageDigestFactory("SHA-384"),
				new MessageDigestFactory("MD5"));
	}

	@Test void testSerialize()
	{
		final MessageDigestFactory mdf = new MessageDigestFactory("MD5");
		assertEquals("MD5", mdf.getAlgorithm());
		assertEquals("MD5", mdf.toString());
		assertEquals(16, mdf.getLength());
		assertEquals(32, mdf.getLengthHex());
		assertEquals("d41d8cd98f00b204e9800998ecf8427e", encodeLower(mdf.getDigestForEmptyByteSequence()));
		assertEquals("d41d8cd98f00b204e9800998ecf8427e", mdf.getDigestForEmptyByteSequenceHex());

		final MessageDigestFactory serialized = CopeAssert.reserialize(mdf, 500);
		assertNotSame(mdf, serialized);
		assertEquals("MD5", serialized.getAlgorithm());
		assertEquals("MD5", serialized.toString());
		assertEquals(16, serialized.getLength());
		assertEquals(32, serialized.getLengthHex());
		assertEquals("d41d8cd98f00b204e9800998ecf8427e", encodeLower(serialized.getDigestForEmptyByteSequence()));
		assertEquals("d41d8cd98f00b204e9800998ecf8427e", serialized.getDigestForEmptyByteSequenceHex());
	}
}
