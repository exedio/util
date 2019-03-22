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

import java.io.File;

/**
 * @deprecated Use {@link StrictFile} instead.
 */
@Deprecated
@SuppressWarnings("unused") // OK: renamed deprecated API
public final class SafeFile
{
	public static void delete(final File file)
	{
		StrictFile.delete(file);
	}

	public static void mkdir(final File file)
	{
		StrictFile.mkdir(file);
	}

	public static void mkdirs(final File file)
	{
		StrictFile.mkdirs(file);
	}

	public static void renameTo(final File file, final File dest)
	{
		StrictFile.renameTo(file, dest);
	}

	private SafeFile()
	{
		// prevent instantiation
	}
}
