/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package org.junit.rules;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * This is a replacement of the respective class in JUnit 4.
 * Allows switching to JUnit 5 without extensive changes in the project.
 */
public final class TemporaryFolder
{
	private final File root;

	private TemporaryFolder() throws IOException
	{
		root = File.createTempFile("junit-TemporaryFolder", ".tmp");
		assertTrue(root.delete());
		assertTrue(root.mkdir());
	}

	public File getRoot()
	{
		return root;
	}

	public File newFile(final String name) throws IOException
	{
		final File result = new File(root, name);
		assertTrue(result.createNewFile());
		return result;
	}

	public static final class Extension implements ParameterResolver, AfterEachCallback
	{
		private TemporaryFolder folder;

		@Override
		public boolean supportsParameter(
				final ParameterContext parameterContext,
				final ExtensionContext extensionContext)
		{
			return TemporaryFolder.class==parameterContext.getParameter().getType();
		}

		@Override
		public Object resolveParameter(
				final ParameterContext parameterContext,
				final ExtensionContext extensionContext)
				throws ParameterResolutionException
		{
			assertNull(folder);
			try
			{
				folder = new TemporaryFolder();
			}
			catch(final IOException e)
			{
				throw new ParameterResolutionException("TemporaryFolder", e);
			}
			return folder;
		}

		@Override
		public void afterEach(final ExtensionContext context) throws IOException
		{
			if(folder!=null)
			{
				Files.walkFileTree(folder.root.toPath(), new SimpleFileVisitor<Path>()
				{
					@Override
					public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException
					{
						Files.delete(file);
						return super.visitFile(file, attrs);
					}

					@Override
					public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException
					{
						Files.delete(dir);
						return super.postVisitDirectory(dir, exc);
					}
				});
				folder = null;
			}
		}
	}
}
