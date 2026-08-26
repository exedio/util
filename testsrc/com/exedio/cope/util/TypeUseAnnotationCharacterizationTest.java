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

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

/**
 * Characterization test for
 * {@link java.lang.annotation.ElementType#TYPE_USE} vs.
 * {@link java.lang.annotation.ElementType#METHOD} and {@link java.lang.annotation.ElementType#PARAMETER}.
 * <p>
 * A type-use annotation is associated with the type, even when written at the
 * declaration element. Reflection exposes it via {@link java.lang.reflect.AnnotatedType}, not
 * via {@link java.lang.reflect.Method} or {@link java.lang.reflect.Parameter}.
 * <p>
 * An annotation whose {@link java.lang.annotation.Target} includes both the
 * declaration element ({@code METHOD} or {@code PARAMETER}) and
 * {@code TYPE_USE} is applicable in both contexts. Consequently,
 * {@code javac} records it in both {@code RuntimeVisibleAnnotations} and
 * {@code RuntimeVisibleTypeAnnotations}; reflection exposes it through both
 * the declaration ({@link java.lang.reflect.Method} or
 * {@link java.lang.reflect.Parameter}) and {@link java.lang.reflect.AnnotatedType}
 * views.
 */
class TypeUseAnnotationCharacterizationTest
{
	@Test
	void elementAtElement() throws NoSuchMethodException
	{
		assertIt("elementAtElement", Element.class, true, false);
	}
	@Element
	@SuppressWarnings("unused") // OK: called by reflection
	private static String elementAtElement(@Element final Object ignored)
	{
		throw new AssertionFailedError();
	}

	@Test
	void elementAtTypeUse() throws NoSuchMethodException
	{
		assertIt("elementAtTypeUse", Element.class, true, false);
	}
	@SuppressWarnings({
			"unused", // OK: called by reflection
			"MissortedModifiers"}) // OK: testing the weird stuff
	private static @Element String elementAtTypeUse(final @Element Object ignored)
	{
		throw new AssertionFailedError();
	}

	@Test
	void typeUseAtElement() throws NoSuchMethodException
	{
		assertIt("typeUseAtElement", TypeUse.class, false, true);
	}
	@TypeUse
	@SuppressWarnings("unused") // OK: called by reflection
	private static String typeUseAtElement(@TypeUse final Object ignored)
	{
		throw new AssertionFailedError();
	}

	@Test
	void typeUseAtTypeUse() throws NoSuchMethodException
	{
		assertIt("typeUseAtTypeUse", TypeUse.class, false, true);
	}
	@SuppressWarnings("unused") // OK: called by reflection
	private static @TypeUse String typeUseAtTypeUse(final @TypeUse Object ignored)
	{
		throw new AssertionFailedError();
	}

	@Test
	void bothAtElement() throws NoSuchMethodException
	{
		assertIt("bothAtElement", Both.class, true, true);
	}
	@Both
	@SuppressWarnings("unused") // OK: called by reflection
	private static String bothAtElement(@Both final Object ignored)
	{
		throw new AssertionFailedError();
	}

	@Test
	void bothAtTypeUse() throws NoSuchMethodException
	{
		assertIt("bothAtTypeUse", Both.class, true, true);
	}
	@SuppressWarnings("unused") // OK: called by reflection
	private static @Both String bothAtTypeUse(final @Both Object ignored)
	{
		throw new AssertionFailedError();
	}

	@Retention(RUNTIME)
	@Target({ElementType.METHOD, ElementType.PARAMETER})
	private @interface Element
	{
	}
	@Retention(RUNTIME)
	@Target(ElementType.TYPE_USE)
	private @interface TypeUse
	{
	}
	@Retention(RUNTIME)
	@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE_USE})
	private @interface Both
	{
	}

	private static void assertIt(
			final String name,
			final Class<? extends Annotation> annotationClass,
			final boolean element,
			final boolean typeUse)
			throws NoSuchMethodException
	{
		final var method = TypeUseAnnotationCharacterizationTest.class.getDeclaredMethod(name, Object.class);
		assertAnno(annotationClass, element, method);
		assertAnno(annotationClass, typeUse, method.getAnnotatedReturnType());

		final var parameter = method.getParameters()[0];
		assertAnno(annotationClass, element, parameter);
		assertAnno(annotationClass, typeUse, parameter.getAnnotatedType());
	}

	private static void assertAnno(
			final Class<? extends Annotation> annotationClass,
			final boolean present,
			final AnnotatedElement element)
	{
		assertEquals(present, element.isAnnotationPresent(annotationClass));
		assertEquals(present, element.getAnnotation(annotationClass)!=null);
	}
}
