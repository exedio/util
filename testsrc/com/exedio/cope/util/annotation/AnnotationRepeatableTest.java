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

package com.exedio.cope.util.annotation;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Test my understanding of repeatable annotations.
 */
public class AnnotationRepeatableTest
{
	@RepeatableContent("one")
	class One
	{
		// empty
	}
	@Test public void testOne()
	{
		final Class<?> clazz = One.class;
		assertEquals("one", value(clazz.getDeclaredAnnotation(content)));
		assertEquals("one", value(clazz.getAnnotation(content)));
		assertEquals(null, value(clazz.getDeclaredAnnotation(container)));
		assertEquals(null, value(clazz.getAnnotation(container)));
		assertEquals(asList("one"), value(clazz.getDeclaredAnnotationsByType(content)));
		assertEquals(asList("one"), value(clazz.getAnnotationsByType(content)));
	}


	@RepeatableContent("one")
	@RepeatableContent("two")
	class Two
	{
		// empty
	}
	@Test public void testTwo()
	{
		final Class<?> clazz = Two.class;
		assertEquals(null, value(clazz.getDeclaredAnnotation(content)));
		assertEquals(null, value(clazz.getAnnotation(content)));
		assertEquals(asList("one", "two"), value(clazz.getDeclaredAnnotation(container)));
		assertEquals(asList("one", "two"), value(clazz.getAnnotation(container)));
		assertEquals(asList("one", "two"), value(clazz.getDeclaredAnnotationsByType(content)));
		assertEquals(asList("one", "two"), value(clazz.getAnnotationsByType(content)));
	}


	@RepeatableContainer(
		@RepeatableContent("oneC")
	)
	class OneContainer
	{
		// empty
	}
	@Test public void testOneContainer()
	{
		final Class<?> clazz = OneContainer.class;
		assertEquals(null, value(clazz.getDeclaredAnnotation(content)));
		assertEquals(null, value(clazz.getAnnotation(content)));
		assertEquals(asList("oneC"), value(clazz.getDeclaredAnnotation(container)));
		assertEquals(asList("oneC"), value(clazz.getAnnotation(container)));
		assertEquals(asList("oneC"), value(clazz.getDeclaredAnnotationsByType(content)));
		assertEquals(asList("oneC"), value(clazz.getAnnotationsByType(content)));
	}


	@RepeatableContainer({
		@RepeatableContent("oneC"),
		@RepeatableContent("twoC")
	})
	class TwoContainer
	{
		// empty
	}
	@Test public void testTwoContainer()
	{
		final Class<?> clazz = TwoContainer.class;
		assertEquals(null, value(clazz.getDeclaredAnnotation(content)));
		assertEquals(null, value(clazz.getAnnotation(content)));
		assertEquals(asList("oneC", "twoC"), value(clazz.getDeclaredAnnotation(container)));
		assertEquals(asList("oneC", "twoC"), value(clazz.getAnnotation(container)));
		assertEquals(asList("oneC", "twoC"), value(clazz.getDeclaredAnnotationsByType(content)));
		assertEquals(asList("oneC", "twoC"), value(clazz.getAnnotationsByType(content)));
	}


	@RepeatableContainer({
		@RepeatableContent("oneC"),
		@RepeatableContent("twoC")
	})
	@RepeatableContent("top")
	class ContainerAndTop
	{
		// empty
	}
	@Test public void testContainerAndTop()
	{
		final Class<?> clazz = ContainerAndTop.class;
		assertEquals("top", value(clazz.getDeclaredAnnotation(content)));
		assertEquals("top", value(clazz.getAnnotation(content)));
		assertEquals(asList("oneC", "twoC"), value(clazz.getDeclaredAnnotation(container)));
		assertEquals(asList("oneC", "twoC"), value(clazz.getAnnotation(container)));
		assertEquals(asList("oneC", "twoC", "top"), value(clazz.getDeclaredAnnotationsByType(content)));
		assertEquals(asList("oneC", "twoC", "top"), value(clazz.getAnnotationsByType(content)));
	}


	@RepeatableContent("top")
	@RepeatableContainer({
		@RepeatableContent("oneC"),
		@RepeatableContent("twoC")
	})
	class ContainerAndTopReverse
	{
		// empty
	}
	@Test public void testContainerAndTopReverse()
	{
		final Class<?> clazz = ContainerAndTopReverse.class;
		assertEquals("top", value(clazz.getDeclaredAnnotation(content)));
		assertEquals("top", value(clazz.getAnnotation(content)));
		assertEquals(asList("oneC", "twoC"), value(clazz.getDeclaredAnnotation(container)));
		assertEquals(asList("oneC", "twoC"), value(clazz.getAnnotation(container)));
		assertEquals(asList("top", "oneC", "twoC"), value(clazz.getDeclaredAnnotationsByType(content)));
		assertEquals(asList("top", "oneC", "twoC"), value(clazz.getAnnotationsByType(content)));
	}



	private static String value(final RepeatableContent anno)
	{
		return anno!=null ? anno.value() : null;
	}

	private static List<String> value(final RepeatableContent[] annos)
	{
		if(annos==null)
			return null;

		final ArrayList<String> result = new ArrayList<>();
		for(final RepeatableContent anno : annos)
			result.add(value(anno));
		return result;
	}

	private static List<String> value(final RepeatableContainer anno)
	{
		return anno!=null ? value(anno.value()) : null;
	}

	private static final Class<RepeatableContent> content = RepeatableContent.class;
	private static final Class<RepeatableContainer> container = RepeatableContainer.class;
}
