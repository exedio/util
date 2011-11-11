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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.exedio.cope.util.annotation.AnnotationInheritanceTest;

public class PackageTest extends TestCase
{
	public static Test suite()
	{
		final TestSuite suite = new TestSuite();
		suite.addTestSuite(BeforeAndAfterTest.class);
		suite.addTestSuite(EmptyJobContextTest.class);
		suite.addTestSuite(ProxyJobContextTest.class);
		suite.addTestSuite(JobContextsEmptyTest.class);
		suite.addTestSuite(JobContextsIteratorTest.class);
		suite.addTestSuite(InterruptersVainTest.class);
		suite.addTestSuite(InterruptersIteratorTest.class);
		suite.addTestSuite(InterrupterJobContextAdapterTest.class);
		suite.addTestSuite(CastTest.class);
		suite.addTestSuite(CharSetTest.class);
		//suite.addTestSuite(CounterTest.class);
		suite.addTestSuite(HexTest.class);
		suite.addTestSuite(DayTest.class);
		suite.addTestSuite(MessageDigestUtilTest.class);
		suite.addTestSuite(PoolTest.class);
		suite.addTestSuite(PoolCounterTest.class);
		suite.addTestSuite(PropertiesTest.class);
		suite.addTestSuite(EmptySourceTest.class);
		suite.addTestSuite(SystemPropertySourceTest.class);
		suite.addTestSuite(PropertiesSourceTest.class);
		suite.addTestSuite(PrefixSourceTest.class);
		suite.addTestSuite(SequenceCheckerTest.class);
		suite.addTestSuite(XMLEncoderTest.class);
		suite.addTestSuite(StrictFileTest.class);
		suite.addTestSuite(SafeFileTest.class);
		suite.addTestSuite(AnnotationInheritanceTest.class);
		return suite;
	}
}
