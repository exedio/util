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

import java.io.Serializable;
import java.security.MessageDigest;
import java.util.Arrays;

@SuppressWarnings("TransientFieldNotInitialized") // OK: handled by readResolve
public final class MessageDigestFactory implements Serializable
{
	private static final long serialVersionUID = 1l;

	private final String algorithm;

	// computed from algorithm
	private final transient int length;
	private final transient int lengthHex;
	private final transient byte[] empty;
	private final transient String emptyHex;

	public MessageDigestFactory(final String algorithm)
	{
		this.algorithm = requireNonNull(algorithm, "algorithm");

		final MessageDigest md = MessageDigestUtil.getInstance(algorithm);

		this.length = md.getDigestLength();
		if(length<=0)
			throw new IllegalArgumentException(algorithm + " must specify digest length, but was " + length); // TODO test

		this.lengthHex = length * 2;

		this.empty = md.digest();
		this.emptyHex = Hex.encodeLower(empty);
	}

	public String getAlgorithm()
	{
		return algorithm;
	}

	public int getLength()
	{
		return length;
	}

	public int getLengthHex()
	{
		return lengthHex;
	}

	public byte[] getDigestForEmptyByteSequence()
	{
		return Arrays.copyOf(empty, empty.length);
	}

	public String getDigestForEmptyByteSequenceHex()
	{
		return emptyHex;
	}

	public MessageDigest newInstance()
	{
		return MessageDigestUtil.getInstance(algorithm);
	}

	public byte[] digest(final byte[] input)
	{
		return newInstance().digest(input);
	}


	@Override
	public boolean equals(final Object other)
	{
		return
				other instanceof MessageDigestFactory &&
				algorithm.equals(((MessageDigestFactory)other).algorithm);
	}

	@Override
	public int hashCode()
	{
		return algorithm.hashCode() ^ 2783178;
	}

	/**
	 * <a href="https://java.sun.com/j2se/1.5.0/docs/guide/serialization/spec/input.html#5903">See Spec</a>
	 */
	private Object readResolve()
	{
		return new MessageDigestFactory(algorithm);
	}

	@Override
	public String toString()
	{
		return algorithm;
	}
}
