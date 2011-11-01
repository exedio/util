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

package com.exedio.cope.util.annotation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.exedio.cope.junit.CopeAssert;

/**
 * Test my understanding of annotation inheritance.
 */
public class AnnotationInheritanceTest extends CopeAssert
{
	public void testIt() throws NoSuchFieldException, NoSuchMethodException
	{
		assertAnno("BothSuperClassDeclared", "BothSuperClassInherited", BothSuper.class);
		assertAnno("BothSuperFieldDeclared", "BothSuperFieldInherited", field(BothSuper.class));
		assertAnno("BothSuperMethodDeclared", "BothSuperMethodInherited", method(BothSuper.class));

		assertAnno("BothSubClassDeclared", "BothSubClassInherited", BothSub.class);
		assertAnno("BothSubFieldDeclared", "BothSubFieldInherited", field(BothSub.class));
		assertAnno("BothSubMethodDeclared", "BothSubMethodInherited", method(BothSub.class));

		assertAnno("HeadSuperClassDeclared", "HeadSuperClassInherited", HeadSuper.class);
		assertAnno("HeadSuperFieldDeclared", "HeadSuperFieldInherited", field(HeadSuper.class));
		assertAnno("HeadSuperMethodDeclared", "HeadSuperMethodInherited", method(HeadSuper.class));

		assertAnno("nix", "HeadSuperClassInherited", HeadSub.class);
		assertAnno("nix", "nix", field(HeadSub.class));
		assertAnno("nix", "nix", method(HeadSub.class));
	}

	private static String value(final AnnoDeclared anno)
	{
		return anno!=null ? anno.value() : "nix";
	}

	private static String value(final AnnoInherited anno)
	{
		return anno!=null ? anno.value() : "nix";
	}

	private static void assertAnno(
			final String declared,
			final String inherited,
			final AnnotatedElement e)
	{
		assertEquals(declared,  value(e.getAnnotation(AnnoDeclared.class )));
		assertEquals(inherited, value(e.getAnnotation(AnnoInherited.class)));

		assertEquals(!"nix".equals(declared ), e.isAnnotationPresent(AnnoDeclared.class));
		assertEquals(!"nix".equals(inherited), e.isAnnotationPresent(AnnoInherited.class));
	}

	private static Field field(final Class<?> clazz) throws NoSuchFieldException
	{
		return clazz.getDeclaredField("field");
	}

	private static Method method(final Class<?> clazz) throws NoSuchMethodException
	{
		return clazz.getDeclaredMethod("method");
	}
}
