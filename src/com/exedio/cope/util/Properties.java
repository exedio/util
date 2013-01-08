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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

public class Properties
{
	final ArrayList<Field> fields = new ArrayList<Field>();
	final HashMap<String, Field> detectDuplicateKeys = new HashMap<String, Field>();
	final HashMap<String, PropertiesField<?>> detectDuplicatePrefixes = new HashMap<String, PropertiesField<?>>();
	final Source source;
	final String sourceDescription;
	private final Source context;

	public Properties(final Source source)
	{
		this(source, null);
	}

	/**
	 * @deprecated
	 * Use {@link #Properties(Source)} instead.
	 * Using context is deprecated at all.
	 */
	@Deprecated
	public Properties(final Source source, final Source context)
	{
		this.source = source;
		this.sourceDescription = source.getDescription();
		this.context = context;
	}

	/**
	 * This default implementation returns an empty list.
	 */
	public List<? extends Callable<?>> getTests()
	{
		return Collections.emptyList();
	}

	public final List<Field> getFields()
	{
		return Collections.unmodifiableList(fields);
	}

	public final Source getSourceObject()
	{
		return source;
	}

	public final String getSource()
	{
		return sourceDescription;
	}

	/**
	 * @throws IllegalStateException if there is no context for these properties.
	 */
	public final Source getContext()
	{
		if(context==null)
			throw new IllegalStateException("no context available");

		return context;
	}

	final String resolve(final String key)
	{
		final String raw = source.get(key);
		if(raw==null || context==null)
			return raw;

		final StringBuilder bf = new StringBuilder();
		int previous = 0;
		for(int dollar = raw.indexOf("${"); dollar>=0; dollar = raw.indexOf("${", previous))
		{
			final int contextKeyBegin = dollar+2;
			final int contextKeyEnd = raw.indexOf('}', contextKeyBegin);
			if(contextKeyEnd<0)
				throw new IllegalArgumentException("missing '}' in " + raw);
			if(contextKeyBegin==contextKeyEnd)
				throw new IllegalArgumentException("${} not allowed in " + raw);
			final String contextKey = raw.substring(contextKeyBegin, contextKeyEnd);
			final String replaced = context.get(contextKey);
			if(replaced==null)
				throw new IllegalArgumentException("key '" + contextKey + "\' not defined by context " + context.getDescription());
			bf.append(raw.substring(previous, dollar)).
				append(replaced);
			previous = contextKeyEnd + 1;
		}
		bf.append(raw.substring(previous));
		return bf.toString();
	}

	public interface Source
	{
		/**
		 * @throws RuntimeException if key is null or empty.
		 * You may want to use {@link Sources#checkKey(String)} for implementations.
		 */
		String get(String key);

		/**
		 * Returns all keys, for which {@link #get(String)}
		 * does not return null.
		 * This operation is optional -
		 * if this source does not support this operation,
		 * it returns null.
		 * The result is always unmodifiable.
		 */
		Collection<String> keySet();

		String getDescription();
	}


	public static final Source EMPTY_SOURCE = new EmptySource();

	private static final class EmptySource implements Source
	{
		EmptySource()
		{
			// empty
		}

		public String get(final String key)
		{
			Sources.checkKey(key);
			return null;
		}

		public Collection<String> keySet()
		{
			return Collections.<String>emptyList();
		}

		public String getDescription()
		{
			return "empty";
		}

		@Override
		public String toString()
		{
			return getClass().getSimpleName();
		}
	}


	public static final Source SYSTEM_PROPERTY_SOURCE = new SystemPropertySource();

	private static final class SystemPropertySource implements Source
	{
		SystemPropertySource()
		{
			// empty
		}

		public String get(final String key)
		{
			Sources.checkKey(key);
			return System.getProperty(key);
		}

		public Collection<String> keySet()
		{
			return null;
		}

		public String getDescription()
		{
			return "java.lang.System.getProperty";
		}

		@Override
		public String toString()
		{
			return getClass().getSimpleName();
		}
	}

	/**
	 * @deprecated Use {@link #SYSTEM_PROPERTY_SOURCE} instead.
	 */
	@Deprecated
	public static final Source getSystemPropertySource()
	{
		return SYSTEM_PROPERTY_SOURCE;
	}


	public static final Source getSource(final java.util.Properties properties, final String description)
	{
		return new Source(){
			public String get(final String key)
			{
				Sources.checkKey(key);
				return properties.getProperty(key);
			}

			public Collection<String> keySet()
			{
				final ArrayList<String> result = new ArrayList<String>();
				for(final Enumeration<?> names = properties.propertyNames(); names.hasMoreElements(); )
					result.add((String)names.nextElement());
				return Collections.unmodifiableList(result);
			}

			public String getDescription()
			{
				return description;
			}

			@Override
			public String toString()
			{
				return description;
			}
		};
	}

	public static final Source getSource(final File file)
	{
		return getSource(loadProperties(file), file.getAbsolutePath());
	}

	public abstract class Field
	{
		final String key;
		private final boolean specified;

		Field(final boolean top, final String key)
		{
			this.key = key;
			this.specified = source.get(key)!=null;

			if(key==null)
				throw new NullPointerException("key");
			if(key.length()==0)
				throw new RuntimeException("key must not be empty.");
			if(detectDuplicateKeys.put(key, this)!=null)
				throw new IllegalArgumentException("duplicate key '" + key + '\'');
			if(top)
				for(final String prefix : detectDuplicatePrefixes.keySet())
					if(key.startsWith(prefix))
						throw new IllegalArgumentException("properties field '" + prefix + "' collides with field '" + key + '\'');

			fields.add(this);
		}

		public final String getKey()
		{
			return key;
		}

		public abstract Object getDefaultValue();
		public abstract Object getValue();

		public final boolean isSpecified()
		{
			return specified;
		}

		public boolean hasHiddenValue()
		{
			return false;
		}
	}

	protected final boolean value(final String key, final boolean defaultValue)
	{
		return field(key, defaultValue).get();
	}

	protected final BooleanField field(final String key, final boolean defaultValue)
	{
		return new BooleanField(key, defaultValue);
	}

	public final class BooleanField extends Field
	{
		private final boolean defaultValue;
		private final boolean value;

		/**
		 * @deprecated Use {@link Properties#field(String, boolean)} instead
		 */
		@Deprecated
		public BooleanField(final String key, final boolean defaultValue)
		{
			super(true, key);
			this.defaultValue = defaultValue;

			final String s = resolve(key);
			if(s==null)
				this.value = defaultValue;
			else
			{
				if(s.equals("true"))
					this.value = true;
				else if(s.equals("false"))
					this.value = false;
				else
					throw new IllegalArgumentException("property " + key + " in " + sourceDescription + " has invalid value, expected >true< or >false<, but got >" + s + "<.");
			}
		}

		BooleanField(final String key, final BooleanField template)
		{
			super(false, key);
			this.defaultValue = template.defaultValue;
			this.value = template.value;
		}

		@Override
		public Object getDefaultValue()
		{
			return Boolean.valueOf(defaultValue);
		}

		@Override
		public Object getValue()
		{
			return Boolean.valueOf(value);
		}

		public boolean get()
		{
			return value;
		}

		// ------------------- deprecated stuff -------------------

		/**
		 * @deprecated Use {@link #get()} instead
		 */
		@Deprecated
		public boolean getBooleanValue()
		{
			return get();
		}

		/**
		 * @deprecated Use {@link #get()} instead
		 */
		@Deprecated
		public boolean booleanValue()
		{
			return get();
		}
	}

	protected final int value(final String key, final int defaultValue, final int minimum)
	{
		return field(key, defaultValue, minimum).get();
	}

	protected final IntField field(final String key, final int defaultValue, final int minimum)
	{
		return new IntField(key, defaultValue, minimum);
	}

	public final class IntField extends Field
	{
		private final int defaultValue;
		private final int value;
		private final int minimum;

		/**
		 * @deprecated Use {@link Properties#field(String, int, int)} instead
		 */
		@Deprecated
		public IntField(final String key, final int defaultValue, final int minimum)
		{
			super(true, key);
			this.defaultValue = defaultValue;
			this.minimum = minimum;

			if(defaultValue<minimum)
				throw new RuntimeException(key+defaultValue+','+minimum);

			final String s = resolve(key);
			if(s==null)
				value = defaultValue;
			else
			{
				try
				{
					value = Integer.parseInt(s);
				}
				catch(final NumberFormatException e)
				{
					throw new IllegalArgumentException(
							"property " + key + " in " + sourceDescription + " has invalid value, " +
							"expected an integer greater or equal " + minimum + ", but got >" + s + "<.", e);
				}

				if(value<minimum)
					throw new IllegalArgumentException(
							"property " + key + " in " + sourceDescription + " has invalid value, " +
							"expected an integer greater or equal " + minimum + ", but got " + value + '.');
			}
		}

		IntField(final String key, final IntField template)
		{
			super(false, key);
			this.defaultValue = template.defaultValue;
			this.value = template.value;
			this.minimum = template.minimum;
		}

		@Override
		public Object getDefaultValue()
		{
			return Integer.valueOf(defaultValue);
		}

		@Override
		public Object getValue()
		{
			return Integer.valueOf(value);
		}

		public int getMinimum()
		{
			return minimum;
		}

		public int get()
		{
			return value;
		}

		// ------------------- deprecated stuff -------------------

		/**
		 * @deprecated Use {@link #get()} instead
		 */
		@Deprecated
		public int getIntValue()
		{
			return get();
		}

		/**
		 * @deprecated Use {@link #get()} instead
		 */
		@Deprecated
		public int intValue()
		{
			return get();
		}
	}

	protected final String value(final String key, final String defaultValue)
	{
		return field(key, defaultValue).get();
	}

	protected final String valueHidden(final String key, final String defaultValue)
	{
		return new StringField(key, defaultValue, true).get();
	}

	protected final StringField field(final String key, final String defaultValue)
	{
		return
			defaultValue!=null
			? new StringField(key, defaultValue)
			: new StringField(key);
	}

	public final class StringField extends Field
	{
		private final String defaultValue;
		private final boolean hideValue;
		private final String value;

		/**
		 * Creates a mandatory string field.
		 * @deprecated Use {@link Properties#field(String, String)} instead
		 */
		@Deprecated
		public StringField(final String key)
		{
			this(key, null, false);
		}

		/**
		 * @deprecated Use {@link Properties#field(String, String)} instead
		 */
		@Deprecated
		public StringField(final String key, final String defaultValue)
		{
			this(key, defaultValue, false);

			if(defaultValue==null)
				throw new NullPointerException("defaultValue");
		}

		StringField(final String key, final String defaultValue, final boolean hideValue)
		{
			super(true, key);
			this.defaultValue = defaultValue;
			this.hideValue = hideValue;

			final String s = resolve(key);
			if(s==null)
			{
				if(defaultValue==null)
					throw new IllegalArgumentException("property " + key + " in " + sourceDescription + " not set and no default value specified.");
				else
					this.value = defaultValue;
			}
			else
				this.value = s;
		}

		StringField(final String key, final StringField template)
		{
			super(false, key);
			this.defaultValue = template.defaultValue;
			this.hideValue = template.hideValue;
			this.value = template.value;
		}

		@Override
		public Object getDefaultValue()
		{
			return defaultValue;
		}

		@Override
		public Object getValue()
		{
			return value;
		}

		public String get()
		{
			return value;
		}

		@Override
		public boolean hasHiddenValue()
		{
			return hideValue;
		}

		// ------------------- deprecated stuff -------------------

		/**
		 * Creates a mandatory string field.
		 * @deprecated Use {@link Properties#valueHidden(String, String)} instead.
		 */
		@Deprecated
		public StringField(final String key, final boolean hideValue)
		{
			this(key, null, hideValue);
		}

		/**
		 * @deprecated Use {@link #get()} instead
		 */
		@Deprecated
		public String getStringValue()
		{
			return get();
		}

		/**
		 * @deprecated Use {@link #get()} instead
		 */
		@Deprecated
		public String stringValue()
		{
			return get();
		}
	}

	protected final FileField fieldFile(final String key)
	{
		return new FileField(key);
	}

	public final class FileField extends Field
	{
		private final File value;

		/**
		 * @deprecated Use {@link Properties#fieldFile(String)} instead
		 */
		@Deprecated
		public FileField(final String key)
		{
			super(true, key);

			final String valueString = resolve(key);
			this.value = (valueString==null) ? null : new File(valueString);
		}

		FileField(final String key, final FileField template)
		{
			super(false, key);
			this.value = template.value;
		}

		@Override
		public Object getDefaultValue()
		{
			return null;
		}

		@Override
		public Object getValue()
		{
			return value;
		}

		public File get()
		{
			return value;
		}

		@Override
		public boolean hasHiddenValue()
		{
			return false;
		}

		// ------------------- deprecated stuff -------------------

		/**
		 * @deprecated Use {@link #get()} instead
		 */
		@Deprecated
		public File getFileValue()
		{
			return get();
		}

		/**
		 * @deprecated Use {@link #get()} instead
		 */
		@Deprecated
		public File fileValue()
		{
			return get();
		}
	}

	protected final MapField fieldMap(final String key)
	{
		return new MapField(key);
	}

	public final class MapField extends Field
	{
		private final java.util.Properties value;

		/**
		 * @deprecated Use {@link Properties#fieldMap(String)} instead
		 */
		@Deprecated
		public MapField(final String key)
		{
			super(true, key);

			value = new java.util.Properties();

			final Collection<String> keySet = source.keySet(); // TODO should not depend on keySet
			if(keySet==null)
				return;

			final String prefix = key + '.';
			final int prefixLength = prefix.length();
			for(final String currentKey : keySet)
			{
				if(currentKey.startsWith(prefix))
					value.put(currentKey.substring(prefixLength), resolve(currentKey));
			}
		}

		MapField(final String key, final MapField template)
		{
			super(false, key);
			this.value = template.value;
		}

		@Override
		public Object getDefaultValue()
		{
			return null;
		}

		@Override
		public Object getValue()
		{
			return value;
		}

		public java.util.Properties mapValue()
		{
			return value;
		}

		public String getValue(final String key)
		{
			return value.getProperty(key);
		}

		// ------------------- deprecated stuff -------------------

		/**
		 * @deprecated Use {@link #mapValue()} instead
		 */
		@Deprecated
		public java.util.Properties getMapValue()
		{
			return mapValue();
		}
	}

	protected final <T extends Properties> T value(final String key, final Factory<T> factory)
	{
		return field(key, factory).get();
	}

	protected final <T extends Properties> T value(final String key, final boolean enabledDefault, final Factory<T> factory)
	{
		return value(key, enabledDefault) ? field(key, factory).get() : null;
	}

	protected final <T extends Properties> PropertiesField<T> field(final String key, final Factory<T> factory)
	{
		return new PropertiesField<T>(this, key, factory);
	}

	public static interface Factory<T extends Properties>
	{
		T create(Source source);
	}

	public static final class PropertiesField<T extends Properties>
	{
		private final String key;
		private final T value;

		PropertiesField(final Properties properties, final String key, final Factory<T> factory)
		{
			this.key = key;
			final String prefix = key + '.';

			for(final String other : properties.detectDuplicatePrefixes.keySet())
				if(other.startsWith(prefix) || prefix.startsWith(other))
					throw new IllegalArgumentException("properties field '" + prefix + "' collides with properties field '" + other + '\'');
			for(final String simple : properties.detectDuplicateKeys.keySet())
				if(simple.startsWith(prefix))
					throw new IllegalArgumentException("properties field '" + key + "' collides with field '" + simple + '\'');
			if(properties.detectDuplicatePrefixes.put(prefix, this)!=null)
				throw new IllegalArgumentException("duplicate prefix '" + prefix + '\'');

			final Source source = new PrefixSource(properties.source, prefix);
			try
			{
				value = factory.create(source);
			}
			catch(final RuntimeException e)
			{
				throw new IllegalArgumentException(
						"property " + key + " in " + properties.sourceDescription + " invalid, see nested exception",
						e);
			}
			for(final Field field : value.fields)
				properties.copy(prefix + field.key, field);
		}

		String getKey()
		{
			return key;
		}

		public T get()
		{
			return value;
		}
	}

	@SuppressWarnings("unused")
	final void copy(final String key, final Field field)
	{
		if(field instanceof BooleanField)
			new BooleanField(key, (BooleanField)field);
		else if(field instanceof IntField)
			new IntField(key, (IntField)field);
		else if(field instanceof StringField)
			new StringField(key, (StringField)field);
		else if(field instanceof FileField)
			new FileField(key, (FileField)field);
		else if(field instanceof MapField)
			new MapField(key, (MapField)field);
		else
			throw new RuntimeException(field.getClass().getName());
	}

	public final void ensureValidity(final String... prefixes)
	{
		// TODO make a method Collection<String> getOrphanedKeys() from this method

		final Collection<String> keySet = source.keySet();
		if(keySet==null)
			return;

		final HashSet<String> allowedValues = new HashSet<String>();
		final ArrayList<String> allowedPrefixes = new ArrayList<String>();

		for(final Field field : fields)
		{
			if(field instanceof MapField)
				allowedPrefixes.add(field.key+'.');
			else
				allowedValues.add(field.key);
		}

		if(prefixes!=null)
			allowedPrefixes.addAll(Arrays.asList(prefixes));

		for(final String key : source.keySet())
		{
			if(!allowedValues.contains(key))
			{
				boolean error = true;
				for(final String allowedPrefix : allowedPrefixes)
				{
					if(key.startsWith(allowedPrefix))
					{
						error = false;
						break;
					}
				}
				if(error)
				{
					// maintain order of fields lost in allowedValues
					final ArrayList<String> allowedValueList = new ArrayList<String>();
					for(final Field field : fields)
						if(!(field instanceof MapField))
							allowedValueList.add(field.key);

					throw new IllegalArgumentException("property " + key + " in " + sourceDescription + " is not allowed, but only one of " + allowedValueList + " or one starting with " + allowedPrefixes + '.');
				}
			}
		}
	}

	public final void ensureEquality(final Properties other)
	{
		final Iterator<Field> j = other.fields.iterator();
		for(final Iterator<Field> i = fields.iterator(); i.hasNext()&&j.hasNext(); )
		{
			final Field thisField = i.next();
			final Field otherField = j.next();
			final boolean thisHideValue = thisField.hasHiddenValue();
			final boolean otherHideValue = otherField.hasHiddenValue();

			if(!thisField.key.equals(otherField.key))
				throw new RuntimeException("inconsistent fields");
			if(thisHideValue!=otherHideValue)
				throw new RuntimeException("inconsistent fields with hide value");

			final Object thisValue = thisField.getValue();
			final Object otherValue = otherField.getValue();

			if((thisValue!=null && !thisValue.equals(otherValue)) ||
				(thisValue==null && otherValue!=null))
				throw new IllegalArgumentException(
						"inconsistent initialization for " + thisField.key +
						" between " + sourceDescription + " and " + other.sourceDescription +
						(thisHideValue ? "." : "," + " expected " + thisValue + " but got " + otherValue + '.'));
		}
	}

	public static final java.util.Properties loadProperties(final File file)
	{
		final java.util.Properties result = new java.util.Properties();
		try
		{
			final FileInputStream stream = new FileInputStream(file);
			try
			{
				result.load(stream);
			}
			finally
			{
				stream.close();
			}
			return result;
		}
		catch(final IOException e)
		{
			throw new RuntimeException("property file "+file.getAbsolutePath()+" not found.", e);
		}
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #Properties(Source, Source)} instead.
	 */
	@Deprecated
	public Properties(final java.util.Properties source, final String sourceDescription)
	{
		this(source, sourceDescription, null);
	}

	/**
	 * @deprecated Use {@link #Properties(Source, Source)} instead.
	 */
	@Deprecated
	public Properties(final java.util.Properties source, final String sourceDescription, final Source context)
	{
		this(getSource(source, sourceDescription), context);
	}

	/**
	 * @throws IllegalArgumentException if the context does not contain a value for <tt>key</tt>.
	 * @throws IllegalStateException if there is no context for these properties.
	 * @deprecated Use {@link #getContext()} instead.
	 */
	@Deprecated
	public final String getContext(final String key)
	{
		if(key==null)
			throw new NullPointerException("key");
		if(context==null)
			throw new IllegalStateException("no context available");

		final String result = context.get(key);
		if(result==null)
			throw new IllegalArgumentException("no value available for key >" + key + "< in context " + context.getDescription());

		return result;
	}

	/**
	 * @deprecated Use {@link #getSystemPropertySource()} instead
	 */
	@Deprecated
	public static final Source getSystemPropertyContext()
	{
		return getSystemPropertySource();
	}

	/**
	 * @deprecated Use {@link #getSource(java.util.Properties,String)} instead
	 */
	@Deprecated
	public static final Source getContext(final java.util.Properties properties, final String description)
	{
		return getSource(properties, description);
	}
}
