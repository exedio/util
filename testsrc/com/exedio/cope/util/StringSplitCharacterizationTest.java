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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.junit.jupiter.api.Test;

/**
 * Characterization test for EnumMap and containsKey.
 */
public class StringSplitCharacterizationTest
{
	@SuppressWarnings("HardcodedLineSeparator")
	@Test void testStringTokenizer()
	{
		assertEquals(List.of("alpha", "beta", "gamma"), splitT("alpha beta gamma"));
		assertEquals(List.of("alpha", "beta", "gamma"), splitT(" alpha  beta  gamma "));
		assertEquals(List.of("alpha", "beta", "gamma"), splitT("  alpha   beta   gamma  "));
		assertEquals(List.of("alpha,", "beta,", "gamma"), splitT("alpha, beta, gamma"));
		assertEquals(List.of("alpha\n", "beta\n", "gamma"), splitT("alpha\n beta\n gamma"));
		assertEquals(List.of("alpha\nbeta\ngamma"), splitT("alpha\nbeta\ngamma"));
	}

	private static List<String> splitT(final String s)
	{
		final ArrayList<String> result = new ArrayList<>();
		for(final StringTokenizer sn = new StringTokenizer(s, " "); sn.hasMoreTokens(); )
			result.add(sn.nextToken());
		return result;
	}


	@SuppressWarnings("HardcodedLineSeparator")
	@Test void testStringSplit()
	{
		assertEquals(List.of("alpha", "beta", "gamma"), splitS("alpha beta gamma"));
		assertEquals(List.of("", "alpha", "", "beta", "", "gamma"), splitS(" alpha  beta  gamma ")); // this is NOT what I need !!!
		assertEquals(List.of("", "", "alpha", "", "", "beta", "", "", "gamma"), splitS("  alpha   beta   gamma  ")); // this is NOT what I need !!!
		assertEquals(List.of("alpha,", "beta,", "gamma"), splitS("alpha, beta, gamma"));
		assertEquals(List.of("alpha\n", "beta\n", "gamma"), splitS("alpha\n beta\n gamma"));
		assertEquals(List.of("alpha\nbeta\ngamma"), splitS("alpha\nbeta\ngamma"));
	}

	private static List<String> splitS(final String s)
	{
		return List.of(s.split(" "));
	}
}
