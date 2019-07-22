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
import static com.exedio.cope.junit.CopeAssert.assertEqualsUnmodifiable;
import static com.exedio.cope.util.Hex.encodeLower;
import static com.exedio.cope.util.PropertiesTest.assertThrowsIllegalProperties;
import static com.exedio.cope.util.Sources.view;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.MessageDigest;
import org.junit.jupiter.api.Test;

public class PropertiesMessageDigestTest
{
	private static final MessageDigestFactory MD5     = new MessageDigestFactory("MD5");
	private static final MessageDigestFactory SHA_256 = new MessageDigestFactory("SHA-256");
	private static final MessageDigestFactory SHA_384 = new MessageDigestFactory("SHA-384");
	private static final MessageDigestFactory SHA_512 = new MessageDigestFactory("SHA-512");

	@Test void testMinimal()
	{
		final MyProps props = new MyProps(minimal());
		props.assertIt();

		assertEquals( MD5,      props.mandatory);
		assertEquals( SHA_512,  props.optional);
		assertEquals( MD5,      props.mandatoryF.getValue());
		assertEquals( SHA_512,  props.optionalF .getValue());
		assertEquals("MD5",     props.mandatoryF.getValueString());
		assertEquals("SHA-512", props.optionalF .getValueString());
		assertEquals("MD5",     props.mandatory.getAlgorithm());
		assertEquals("SHA-512", props.optional .getAlgorithm());
		assertEquals("MD5",     props.mandatory.toString());
		assertEquals("SHA-512", props.optional .toString());
		assertTrue (props.mandatoryF.isSpecified());
		assertFalse(props.optionalF .isSpecified());

		assertEquals( 16, props.mandatory.getLength());
		assertEquals( 64, props.optional .getLength());
		assertEquals( 32, props.mandatory.getLengthHex());
		assertEquals(128, props.optional .getLengthHex());
		assertEquals(
				"d41d8cd98f00b204e9800998ecf8427e",
				encodeLower(props.mandatory.getDigestForEmptyByteSequence()));
		assertEquals(
				"cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e",
				encodeLower(props.optional.getDigestForEmptyByteSequence()));
		assertEquals(
				"d41d8cd98f00b204e9800998ecf8427e",
				props.mandatory.getDigestForEmptyByteSequenceHex());
		assertEquals(
				"cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e",
				props.optional.getDigestForEmptyByteSequenceHex());

		final MessageDigest md5 = props.mandatory.newInstance();
		final MessageDigest sha = props.optional .newInstance();
		md5.update("Franz jagt im komplett verwahrlosten Taxi quer durch Bayern".getBytes(US_ASCII));
		sha.update("Franz jagt im komplett verwahrlosten Taxi quer durch Bayern".getBytes(US_ASCII));
		assertEquals(
				"a3cca2b2aa1e3b5b3b5aad99a8529074",
				encodeLower(md5.digest()));
		assertEquals(
				"af9ed2de700433b803240a552b41b5a472a6ef3fe1431a722b2063c75e9f07451f67a28e37d09cde769424c96aea6f8971389db9e1993d6c565c3c71b855723c",
				encodeLower(sha.digest()));
	}

	@Test void testSet()
	{
		final java.util.Properties p = minimal();
		p.setProperty("mandatory", "SHA-256");
		p.setProperty("optional",  "SHA-384");
		final MyProps props = new MyProps(p);
		props.assertIt();

		assertEquals( SHA_256,  props.mandatory);
		assertEquals( SHA_384,  props.optional);
		assertEquals("SHA-256", props.mandatory.getAlgorithm());
		assertEquals("SHA-384", props.optional .getAlgorithm());
		assertEquals( SHA_256,  props.mandatoryF.getValue());
		assertEquals( SHA_384,  props.optionalF .getValue());
		assertEquals("SHA-256", props.mandatoryF.getValueString());
		assertEquals("SHA-384", props.optionalF .getValueString());
		assertTrue (props.mandatoryF.isSpecified());
		assertTrue (props.optionalF .isSpecified());

		assertEquals(32, props.mandatory.getLength());
		assertEquals(48, props.optional .getLength());
		assertEquals(64, props.mandatory.getLengthHex());
		assertEquals(96, props.optional .getLengthHex());
	}

	@Test void testGetString()
	{
		final MyProps props = new MyProps(minimal());
		props.assertIt();

		assertEquals("MD5", props.mandatoryF.getString(MD5));
		assertEquals(null,  props.mandatoryF.getString(null));
		assertFails(
				() -> props.mandatoryF.getString(55),
				ClassCastException.class,
				"Cannot cast java.lang.Integer to " + MessageDigestFactory.class.getName());
	}

	@Test void testMandatoryWrong()
	{
		assertWrong(
				"mandatory", "WRONG",
				"must specify a digest, but was 'WRONG'",
				IllegalAlgorithmException.class);
	}

	@Test void testMandatoryUnspecified()
	{
		assertWrong(
				"mandatory", null,
				"must be specified as there is no default", null);
	}

	@Test void testOptionalWrong()
	{
		assertWrong(
				"optional", "WRONG",
				"must specify a digest, but was 'WRONG'",
				IllegalAlgorithmException.class);
	}


	static class MyProps extends Properties
	{
		final MessageDigestFactory mandatory = valueMessageDigest("mandatory", null);
		final MessageDigestFactory optional  = valueMessageDigest("optional" , "SHA-512");

		MyProps(final java.util.Properties source)
		{
			super(view(source, "sourceDescription"));
		}

		final Field<?> mandatoryF = getField("mandatory");
		final Field<?> optionalF  = getField("optional");


		void assertIt()
		{
			assertEqualsUnmodifiable(asList(), getProbes());
			assertEqualsUnmodifiable(asList(mandatoryF, optionalF), getFields());

			assertEquals("mandatory", mandatoryF.getKey());
			assertEquals("optional",  optionalF .getKey());

			assertEquals(null,      mandatoryF.getDefaultValue());
			assertEquals(new MessageDigestFactory("SHA-512"), optionalF.getDefaultValue());

			assertEquals(null,      mandatoryF.getDefaultValueString());
			assertEquals("SHA-512", optionalF .getDefaultValueString());

			assertEquals(null, mandatoryF.getDefaultValueFailure());
			assertEquals(null, optionalF .getDefaultValueFailure());

			assertFalse(mandatoryF.hasHiddenValue());
			assertFalse(optionalF .hasHiddenValue());
		}
	}

	private static void assertWrong(
			final String key,
			final String value,
			final String message,
			final Class<? extends Throwable> cause)
	{
		final java.util.Properties wrongProps = minimal();
		if(value!=null)
			wrongProps.setProperty(key, value);
		else
			wrongProps.remove(key);

		assertThrowsIllegalProperties(
				() -> new MyProps(wrongProps),
				key, message, cause);
	}

	private static java.util.Properties minimal()
	{
		final java.util.Properties result = new java.util.Properties();
		result.setProperty("mandatory", "MD5");
		return result;
	}
}
