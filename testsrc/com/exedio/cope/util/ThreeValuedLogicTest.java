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

import static com.exedio.cope.util.ThreeValuedLogicTest.Value.False;
import static com.exedio.cope.util.ThreeValuedLogicTest.Value.True;
import static com.exedio.cope.util.ThreeValuedLogicTest.Value.Unknown;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * This test helps me to understand three value logic.
 * @author Ralf Wiebicke
 */
public class ThreeValuedLogicTest
{
	/**
	 * The table below is from
	 * <a href="https://en.wikipedia.org/wiki/Null_%28SQL%29#Comparisons_with_NULL_and_the_three-valued_logic_.283VL.29">Comparisons with NULL and the three-valued logic</a>
	 */
	@Test void test()
	{
		assertIt(True,    True,    True,    True,    True   );
		assertIt(True,    False,   True,    False,   False  );
		assertIt(True,    Unknown, True,    Unknown, Unknown);
		assertIt(False,   True,    True,    False,   False  );
		assertIt(False,   False,   False,   False,   True   );
		assertIt(False,   Unknown, Unknown, False,   Unknown);
		assertIt(Unknown, True,    True,    Unknown, Unknown);
		assertIt(Unknown, False,   Unknown, False,   Unknown);
		assertIt(Unknown, Unknown, Unknown, Unknown, Unknown);
	}

	private static void assertIt(
			final Value p, final Value q,
			final Value expectedOr, final Value expectedAnd,
			@SuppressWarnings("unused") final Value equal)
	{
		assertEquals(expectedOr,  composite(Operator.OR , p, q), "OR" );
		assertEquals(expectedAnd, composite(Operator.AND, p, q), "AND");
	}

	private static Value composite(final Operator operator, final Value... arguments)
	{
		final Value absorber = operator.absorber;
		Value resultWithoutAbsorber = operator.identity;
		for(final Value argument : arguments)
		{
			if(argument==absorber)
				return absorber;
			if(argument==Unknown)
				resultWithoutAbsorber = Unknown;
		}
		return resultWithoutAbsorber;
	}

	enum Operator
	{
		AND(False, True),
		OR (True, False);

		final Value absorber;
		final Value identity;

		Operator(
				final Value absorber,
				final Value identity)
		{
			this.absorber = absorber;
			this.identity = identity;
		}
	}

	enum Value
	{
		True,
		False,
		Unknown
	}
}
