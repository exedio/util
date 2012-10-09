/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.util;

// TODO move into framework
// TODO
// must not move this class into frameworks other that util
// to avoid collitions with upcoming features
public class MyProperties extends Properties
{
	protected MyProperties(final Source source, final Source context)
	{
		super(source, context);
	}

	protected final boolean value(final String key, final boolean defaultValue)
	{
		return field(key, defaultValue).get();
	}

	protected final int value(final String key, final int defaultValue, final int minimum)
	{
		return field(key, defaultValue, minimum).get();
	}

	protected final String value(final String key, final String defaultValue)
	{
		return field(key, defaultValue).get();
	}

	protected final <T extends Properties> T value(final String key, final Factory<T> factory)
	{
		return field(key, factory).get();
	}

	// TODO make field(key, factory, optionDefault)
	protected final <T extends Properties> PropertiesField<T> fieldOptional(final String key, final Factory<T> factory)
	{
		return value(key, false) ? field(key, factory) : null;
	}

	// TODO make field(key, factory, optionDefault)
	protected final <T extends Properties> T valueOptional(final String key, final Factory<T> factory)
	{
		final PropertiesField<T> field = fieldOptional(key, factory);
		return field!=null ? field.get() : null;
	}

	// for testing only

	final Field forKey(final String key)
	{
		return detectDuplicateKeys.get(key);
	}
}
