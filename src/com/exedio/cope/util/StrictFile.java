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

import java.io.File;

public final class StrictFile
{
	public static void delete(final File file)
	{
		if(!file.delete())
			throw failure(file);
	}

	public static void mkdir(final File file)
	{
		if(!file.mkdir())
			throw failure(file);
	}

	public static void mkdirs(final File file)
	{
		if(!file.mkdirs())
			throw failure(file);
	}

	public static void renameTo(final File file, final File dest)
	{
		if(!file.renameTo(dest))
			throw failure(file);
	}

	// TODO add all methods boolean File.setXXX

	private static IllegalStateException failure(final File file)
	{
		return new IllegalStateException(file.getAbsolutePath());
	}

	private StrictFile()
	{
		// prevent instantiation
	}
}
