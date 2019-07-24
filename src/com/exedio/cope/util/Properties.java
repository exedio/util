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

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.time.zone.ZoneRulesException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;

@SuppressWarnings("AbstractClassWithoutAbstractMethods") // OK: instantiating makes no sense without subclass
public abstract class Properties
{
	 // yyyy-mm-dd, allow to leave out leading zeros on month and day but not on year, otherwise 14-1-1 may result in another day as expected
	static final Pattern DAY_PATTERN = Pattern.compile("(\\d{4})-(\\d{1,2})-(\\d{1,2})");

	final ArrayList<Field<?>> fields = new ArrayList<>();
	final HashMap<String, Field<?>> fieldsByKey = new HashMap<>();
	final HashMap<String, PropertiesField<?>> detectDuplicatePrefixes = new HashMap<>();
	final Source source;
	final String sourceDescription;

	protected Properties(final Source source)
	{
		this.source = requireNonNull(source, "source");
		this.sourceDescription = source.getDescription();
	}

	/**
	 * This default implementation returns {@link #getProbes()}.
	 * @deprecated Use {@link Probe} instead
	 */
	@Deprecated
	public List<? extends Callable<?>> getTests()
	{
		return getProbes();
	}

	public final Field<?> getField(final String key)
	{
		Sources.checkKey(key);
		return fieldsByKey.get(key);
	}

	public final List<Field<?>> getFields()
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

		/**
		 * Reloads the contents of this source from its origin,
		 * if this makes sense for the implementation.
		 * The reloaded contents are returned as a new source -
		 * this source is not modified.
		 * <p>
		 * If the implementation does not support reloading,
		 * simply return {@code this},
		 * which is also the default implementation.
		 */
		default Source reload()
		{
			return this;
		}

		String getDescription();
	}

	@SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_NEEDS_THIS") // backwards compatibility
	public final class Field<E>
	{
		final String key;
		private final Class<E> valueClass;
		private final E minimum;
		private final E defaultValue;
		private final String defaultValueFailure;
		private final boolean hideValue;
		private final boolean specified;
		private final E value;
		private final Function<E, String> getString;

		@SuppressWarnings("ThisEscapedInObjectConstruction")
		Field(
				final String key,
				final Class<E> valueClass,
				final E minimum,
				final E defaultValue,
				final String defaultValueFailure,
				final boolean hideValue,
				final boolean specified,
				final E value,
				final Function<E, String> getString)
		{
			this.key = key;
			this.valueClass = requireNonNull(valueClass);
			this.minimum = minimum;
			this.defaultValue = defaultValue;
			this.defaultValueFailure = defaultValueFailure;
			this.hideValue = hideValue;
			this.specified = specified;
			this.value = value;
			this.getString = requireNonNull(getString);

			if(fieldsByKey.putIfAbsent(key, this)!=null)
				throw new RuntimeException(key);
			fields.add(this);
		}

		Field(final String key, final Field<E> template)
		{
			this(
					key,
					template.valueClass,
					template.minimum,
					template.defaultValue,
					template.defaultValueFailure,
					template.hideValue,
					template.specified,
					template.value,
					template.getString);
		}

		@Nonnull
		public String getKey()
		{
			return key;
		}

		/**
		 * Do no apply {@link Object#toString()} to the result,
		 * but use {@link #getMinimumString()} instead.
		 */
		public E getMinimum()
		{
			return minimum;
		}

		/**
		 * Returns {@link #getString(Object) getString}({@link #getMinimum()}).
		 */
		public String getMinimumString()
		{
			return minimum!=null ? getString.apply(minimum) : null;
		}

		/**
		 * Do no apply {@link Object#toString()} to the result,
		 * but use {@link #getDefaultValueString()} instead.
		 */
		public E getDefaultValue()
		{
			return defaultValue;
		}

		/**
		 * Returns {@link #getString(Object) getString}({@link #getDefaultValue()}).
		 */
		public String getDefaultValueString()
		{
			return defaultValue!=null ? getString.apply(defaultValue) : null;
		}

		public String getDefaultValueFailure()
		{
			return defaultValueFailure;
		}

		public boolean hasHiddenValue()
		{
			return hideValue;
		}

		public boolean isSpecified()
		{
			return specified;
		}

		/**
		 * Never returns null.
		 * <p>
		 * Do no apply {@link Object#toString()} to the result,
		 * but use {@link #getValueString()} instead.
		 */
		@Nonnull
		public E getValue()
		{
			return value;
		}

		/**
		 * Returns {@link #getString(Object) getString}({@link #getValue()}).
		 */
		@Nonnull
		public String getValueString()
		{
			return getString.apply(value);
		}

		/**
		 * Returns a string that would be parsed to the same
		 * {@link #getValue() value} {@code someValue} by this field.
		 * Returns {@code null}, if {@code someValue} is {@code null}.
		 *
		 * @throws ClassCastException
		 *         if {@code someValue} does not match class of {@link #getValue() value}
		 */
		public String getString(final Object someValue)
		{
			return someValue!=null ? getString.apply(valueClass.cast(someValue)) : null;
		}

		@Nonnull
		public E get()
		{
			return value;
		}

		@Override
		public String toString()
		{
			return key;
		}
	}

	private <E> Field<E> parseField(
			final String key,
			final Class<E> valueClass,
			final E minimum,
			final E defaultValue,
			final Function<String, E> parser)
	{
		return parseField(key, valueClass, minimum, () -> defaultValue, false, parser, Object::toString);
	}

	private <E> Field<E> parseField(
			final String key,
			final Class<E> valueClass,
			final E minimum,
			final Supplier<E> defaultValueSupplier,
			final boolean hideValue,
			final Function<String, E> parser,
			final Function<E, String> getString)
	{
		Sources.checkKey(key);
		if(fieldsByKey.containsKey(key))
			throw new IllegalArgumentException("duplicate key '" + key + '\'');
		for(final String prefix : detectDuplicatePrefixes.keySet())
			if(key.startsWith(prefix))
				throw new IllegalArgumentException("properties field '" + prefix + "' collides with field '" + key + '\'');

		final String s = source.get(key);
		final E defaultValue;
		final String defaultValueFailure;
		final boolean specified;
		final E value;
		if(s==null)
		{
			defaultValue = defaultValueSupplier.get();
			if(defaultValue==null)
				throw newException(key,
						"must be specified as there is no default");
			defaultValueFailure = null;

			specified = false;
			value = defaultValue;
		}
		else
		{
			specified = true;
			value = requireNonNull(parser.apply(s), key);

			if(defaultValueSupplier!=null)
			{
				E d = null;
				String f = null;
				// Parsing the default value may fail, for instance because the Class
				// or the MessageDigest does not exist. In such cases the failure
				// must be suppressed. Otherwise it would not help to override the default.
				try
				{
					d = defaultValueSupplier.get();
				}
				catch(final IllegalPropertiesException e)
				{
					f = e.getDetail();
				}
				defaultValue = d;
				defaultValueFailure = f;
			}
			else
			{
				defaultValue = null;
				defaultValueFailure = null;
			}
		}
		return new Field<>(
				key, valueClass, minimum,
				defaultValue, defaultValueFailure,
				hideValue, specified,
				value, getString);
	}

	private <E> Field<E> parseFieldOrDefault(
			final String key,
			final Class<E> valueClass,
			final String defaultValue,
			final Function<String, E> parser,
			final Function<E, String> getString)
	{
		return parseField(
				key, valueClass, null,
				() -> defaultValue!=null ? requireNonNull(parser.apply(defaultValue), key) : null,
				false,
				parser, getString);
	}


	@SuppressWarnings("deprecation") // needed for idea
	protected final boolean value(final String key, final boolean defaultValue)
	{
		return field(key, defaultValue).get();
	}

	/**
	 * @deprecated Use {@link #value(String, boolean)} instead
	 */
	@Deprecated
	protected final Field<Boolean> field(final String key, final boolean defaultValue)
	{
		return parseField(key, Boolean.class, null, defaultValue, (s) ->
		{
			switch(s)
			{
				case "true" : return true;
				case "false": return false;
				default:
					throw newException(key,
							"must be either 'true' or 'false', " +
									"but was '" + s + '\'');
			}
		});
	}


	@SuppressWarnings("deprecation") // needed for idea
	protected final int value(final String key, final int defaultValue, final int minimum)
	{
		return field(key, defaultValue, minimum).get();
	}

	/**
	 * @deprecated Use {@link #value(String, int, int)} instead
	 */
	@Deprecated
	protected final Field<Integer> field(final String key, final int defaultValue, final int minimum)
	{
		if(defaultValue<minimum)
			throw new IllegalArgumentException(
					"default of " + key + " must not be smaller than minimum of " + minimum + ", " +
					"but was " + defaultValue);

		return parseField(key, Integer.class, minimum, defaultValue, (s) ->
		{
			final int value;
			try
			{
				value = Integer.parseInt(s);
			}
			catch(final NumberFormatException e)
			{
				throw newException(key,
						mustBe(minimum) +
						"but was '" + s + '\'',
						e);
			}

			if(value<minimum)
				throw newException(key,
						mustBe(minimum) +
						"but was " + value);

			return value;
		});
	}

	private static String mustBe(final int minimum)
	{
			return
				"must be an integer" +
				(minimum>Integer.MIN_VALUE ? (" greater or equal " + minimum) : "") + ", ";
	}


	protected final LocalDate value(final String key, final LocalDate defaultValue)
	{
		return parseField(key, LocalDate.class, null, defaultValue, (value) ->
		{
			try
			{
				return LocalDate.parse(value);
			}
			catch(final DateTimeParseException e)
			{
				throw newException(key,
						"must be a local date formatted as yyyy-mm-dd, " +
						"but was '" + value + '\'',
						e);
			}
		}).get();
	}

	@SuppressWarnings("deprecation") // needed for idea
	protected final Day value(final String key, final Day defaultValue)
	{
		return field(key, defaultValue).get();
	}

	/**
	 * @deprecated Use {@link #value(String, Day)} instead
	 */
	@Deprecated
	protected final Field<Day> field(final String key, final Day defaultValue)
	{
		return parseField(key, Day.class, null, () -> defaultValue, false, (s) ->
		{
			try
			{
				final Matcher matcher = DAY_PATTERN.matcher(s);
				if(!matcher.matches())
					throw new IllegalArgumentException("Input sequence does not match the pattern.");

				return new Day(
						Integer.parseInt(matcher.group(1)),  // year
						Integer.parseInt(matcher.group(2)),  // month
						Integer.parseInt(matcher.group(3))); // day
			}
			catch(final IllegalArgumentException e)
			{
				throw newException(key,
						"must be a day formatted as yyyy-mm-dd, " +
						"but was '" + s + '\'',
						e);
			}
		}, d -> d.toLocalDate().toString());
	}


	@SuppressWarnings("deprecation") // needed for idea
	protected final String value(final String key, final String defaultValue)
	{
		return field(key, defaultValue).get();
	}

	protected final String valueHidden(final String key, final String defaultValue)
	{
		return parseField(key, String.class, null, () -> defaultValue, true, identity(), identity()).get();
	}

	/**
	 * @deprecated Use {@link #value(String, String)} instead
	 */
	@Deprecated
	protected final Field<String> field(final String key, final String defaultValue)
	{
		return parseField(key, String.class, null, defaultValue, identity());
	}


	protected final Path valuePath(final String key)
	{
		return parseField(key, Path.class, null, null, Paths::get).get();
	}


	@SuppressWarnings("deprecation") // needed for idea
	protected final File valueFile(final String key)
	{
		return fieldFile(key).get();
	}

	/**
	 * @deprecated Use {@link #valueFile(String)} instead
	 */
	@Deprecated
	protected final Field<File> fieldFile(final String key)
	{
		return parseField(key, File.class, null, null, File::new);
	}


	protected final <E extends Enum<E>> E value(final String key, final Class<E> valueClass)
	{
		return value(key, valueClass, null);
	}

	protected final <E extends Enum<E>> E value(final String key, final E defaultValue)
	{
		return value(key, defaultValue.getDeclaringClass(), defaultValue);
	}

	private <E extends Enum<E>> E value(
			final String key,
			final Class<E> valueClass,
			final E defaultValue)
	{
		return parseField(key, valueClass, null, defaultValue, (value) ->
		{
			for(final E result : valueClass.getEnumConstants())
				if(value.equals(result.name()))
					return result;
			throw newException(key,
					"must be one of " + asList(valueClass.getEnumConstants()) + ", " +
					"but was '" + value + '\'');
		}).get();
	}

	protected final Charset value(final String key, final Charset defaultValue)
	{
		return parseField(key, Charset.class, null, defaultValue, (value) ->
		{
			try
			{
				return Charset.forName(value);
			}
			catch(final UnsupportedCharsetException e)
			{
				throw newException(key,
						"must be one of Charset.availableCharsets(), " +
						"but was '" + value + '\'', e);
			}
		}).get();
	}

	protected final ZoneId value(final String key, final ZoneId defaultValue)
	{
		return parseField(key, ZoneId.class, null, defaultValue, (value) ->
		{
			try
			{
				return ZoneId.of(value);
			}
			catch(final ZoneRulesException e)
			{
				throw newException(key,
						"must be one of ZoneId.getAvailableZoneIds(), " +
						"but was '" + value + '\'', e);
			}
		}).get();
	}

	protected final Duration value(
			final String key,
			final Duration defaultValue,
			final Duration minimum)
	{
		return value(key, defaultValue, minimum, DURATION_MAX_VALUE);
	}

	private static final Duration DURATION_MAX_VALUE = Duration.ofSeconds(Long.MAX_VALUE, 999_999_999);

	protected final Duration value(
			final String key,
			final Duration defaultValue,
			final Duration minimum,
			final Duration maximum)
	{
		requireNonNull(minimum, "minimum");
		requireNonNull(maximum, "maximum");
		if(defaultValue!=null)
		{
			if(defaultValue.compareTo(minimum)<0)
				throw new IllegalArgumentException(
						"default of " + key + " must not be smaller than minimum of " + string(minimum) + ", " +
						"but was " + string(defaultValue));
			if(defaultValue.compareTo(maximum)>0)
				throw new IllegalArgumentException(
						"default of " + key + " must not be greater than maximum of " + string(maximum) + ", " +
						"but was " + string(defaultValue));
		}

		return parseField(key, Duration.class, minimum, () -> defaultValue, false, (value) ->
		{
			Duration result;
			try
			{
				result = Duration.parse(value);
			}
			catch(final DateTimeParseException e)
			{
				final long millis;
				try
				{
					millis = Long.parseLong(value);
				}
				catch(final NumberFormatException ignored)
				{
					throw newException(key,
							"must be a duration, but was '" + value + '\'', e);
				}
				result = Duration.ofMillis(millis);
			}

			if(result.compareTo(minimum)<0 ||
				result.compareTo(maximum)>0)
				throw newException(key,
						maximum.equals(DURATION_MAX_VALUE)
						? "must be a duration greater or equal " + string(minimum) +                    ", but was " + string(result)
						: "must be a duration between " + string(minimum) + " and " + string(maximum) + ", but was " + string(result) );

			return result;
		}, Properties::string).get();
	}

	static final String string(final Duration d)
	{
		final long days = d.toDays();
		if(days==0)
			return d.toString();

		final Duration withoutDays = d.minusDays(days);
		if(Duration.ZERO.equals(withoutDays))
			return "P" + days + 'D';

		return "P" + days + 'D' + withoutDays.toString().substring(1);
	}

	protected final MessageDigestFactory valueMessageDigest(final String key, final String defaultValue)
	{
		return parseFieldOrDefault(key, MessageDigestFactory.class, defaultValue, (value) ->
		{
			try
			{
				return new MessageDigestFactory(value);
			}
			catch(final IllegalAlgorithmException e)
			{
				throw newException(key, "must specify a digest, but was '" + e.getAlgorithm() + '\'', e);
			}
		}, Object::toString).get();
	}

	/**
	 * Implementations of {@code superclass} to be instantiated by
	 * {@link ServiceFactory#newInstance(Object)}
	 * must have a suitable constructor:
	 *
	 * The type of the first parameter is specified by parameter {@code parameterType} of this method.
	 * The type of the second parameter is specified by annotation @{@link ServiceProperties}
	 * at the implementation class.
	 * If there is no such annotation, the constructor must have one parameter only.
	 * Please note that @{@link ServiceProperties} could be
	 * {@link java.lang.annotation.Inherited inherited} from a superclass.
	 */
	protected final <T,P> ServiceFactory<T,P> valueService(
			final String key,
			final Class<T> superclass,
			final Class<P> parameterType)
	{
		return valueService(key, (String)null, superclass, parameterType);
	}

	protected final <T,P> ServiceFactory<T,P> valueService(
			final String key,
			final Class<? extends T> defaultValue,
			final Class<T> superclass,
			final Class<P> parameterType)
	{
		return valueService(key,
				defaultValue!=null ? defaultValue.getName() : null,
				superclass, parameterType);
	}

	protected final <T,P> ServiceFactory<T,P> valueService(
			final String key,
			final String defaultValue,
			final Class<T> superclass,
			final Class<P> parameterType)
	{
		@SuppressWarnings("rawtypes")
		final Function<Class,String> classGetName = Class::getName;
		final Class<?> classRaw = parseFieldOrDefault(key, Class.class, defaultValue, (name) ->
		{
			try
			{
				return Class.forName(name);
			}
			catch(final ClassNotFoundException e)
			{
				throw newException(key, "must name a class, but was '" + name + '\'', e);
			}
		}, classGetName).get();

		if(Modifier.isAbstract(classRaw.getModifiers()))
			throw newException(key,
					"must name a non-abstract class, " +
					"but was " + classRaw.getName());

		final Class<? extends T> clazz;
		try
		{
			clazz = classRaw.asSubclass(superclass);
		}
		catch(final ClassCastException e)
		{
			throw newException(key,
					"must name a subclass of " + superclass.getName() + ", " +
					"but was " + classRaw.getName(), e);
		}

		final ServiceProperties propertiesAnnotation = classRaw.getAnnotation(ServiceProperties.class);
		final Class<? extends Properties> propertiesClass;
		final Properties properties;
		if(propertiesAnnotation==null)
		{
			propertiesClass = null;
			properties = null;
		}
		else
		{
			propertiesClass = propertiesAnnotation.value();

			final Constructor<? extends Properties> constructor;
			try
			{
				constructor = propertiesClass.getDeclaredConstructor(Source.class);
			}
			catch(final ReflectiveOperationException e)
			{
				throw newException(key,
						"names a class " + classRaw.getName() + " " +
						"annotated by @" + ServiceProperties.class.getSimpleName() + '(' + propertiesClass.getName() + "), " +
						"which must have a constructor with parameter " + Source.class.getName(), e);
			}
			constructor.setAccessible(true);

			properties = value(key, (Factory<Properties>)source ->
			{
				try
				{
					return constructor.newInstance(source);
				}
				catch(final InvocationTargetException e)
				{
					final Throwable target = e.getTargetException();
					if(target instanceof RuntimeException)
						throw (RuntimeException)target;
					else
						// TODO test
						throw newException(key,
								"names a class " + classRaw.getName() + " " +
								"annotated by @" + ServiceProperties.class.getSimpleName() + '(' + propertiesClass.getName() + "), " +
								"which must have a constructor with parameter " + Source.class.getName(), e);
				}
				catch(final ReflectiveOperationException e)
				{
					// TODO test
					throw newException(key,
							"names a class " + classRaw.getName() + " " +
							"annotated by @" + ServiceProperties.class.getSimpleName() + '(' + propertiesClass.getName() + "), " +
							"which must have a constructor with parameter " + Source.class.getName(), e);
				}
			});
		}

		final Constructor<? extends T> constructor;
		try
		{
			constructor =
					propertiesClass!=null
					? clazz.getDeclaredConstructor(parameterType, propertiesClass)
					: clazz.getDeclaredConstructor(parameterType);
		}
		catch(final NoSuchMethodException e)
		{
			throw newException(key,
					"must name a class with a constructor with " +
					(propertiesClass!=null
							? "parameters " + parameterType.getName() + ',' + propertiesClass.getName()
							: "parameter "  + parameterType.getName()
					) + ", " +
					"but was " + classRaw.getName(), e);
		}
		return new ServiceFactory<>(constructor, properties);
	}


	/**
	 * If calling this method causes javac problem:
	 * {@code error: reference to value is ambiguous},
	 * use {@link #valnp(String, Factory)} instead.
	 */
	@SuppressWarnings("deprecation") // needed for idea
	protected final <T extends Properties> T value(final String key, final Factory<T> factory)
	{
		return field(key, factory).get();
	}

	/**
	 * Replacement for {@link #value(String, Factory)} to avoid javac problem:
	 * <p>
	 * {@code error: reference to value is ambiguous}
	 * <p>
	 * https://bugs.java.com/bugdatabase/view_bug.do?bug_id=JDK-8170842
	 */
	protected final <T extends Properties> T valnp(final String key, final Factory<T> factory)
	{
		return value(key, factory);
	}

	protected final <T extends Properties> T value(final String key, final boolean enabledDefault, final Factory<T> factory)
	{
		return value(key, enabledDefault) ? value(key, factory) : null;
	}

	/**
	 * @deprecated Use {@link #value(String, Factory)} instead
	 */
	@Deprecated
	protected final <T extends Properties> PropertiesField<T> field(final String key, final Factory<T> factory)
	{
		final PropertiesField<T> result = new PropertiesField<>(this, key, factory);
		for(final Prober prober : result.value.probers)
			probers.add(prober.prefix(key));
		return result;
	}

	@FunctionalInterface
	public interface Factory<T extends Properties>
	{
		T create(Source source);
	}

	public static final class PropertiesField<T extends Properties>
	{
		private final String key;
		final T value;

		@SuppressWarnings("ThisEscapedInObjectConstruction")
		PropertiesField(final Properties properties, final String key, final Factory<T> factory)
		{
			this.key = key;
			final String prefix = key + '.';

			for(final String other : properties.detectDuplicatePrefixes.keySet())
				if(other.startsWith(prefix) || prefix.startsWith(other))
					throw new IllegalArgumentException("properties field '" + prefix + "' collides with properties field '" + other + '\'');
			for(final String simple : properties.fieldsByKey.keySet())
				if(simple.startsWith(prefix))
					throw new IllegalArgumentException("properties field '" + key + "' collides with field '" + simple + '\'');
			if(properties.detectDuplicatePrefixes.put(prefix, this)!=null)
				throw new IllegalArgumentException("duplicate prefix '" + prefix + '\'');

			final Source source = PrefixSource.wrap(properties.source, prefix);
			try
			{
				value = factory.create(source);
			}
			catch(final IllegalPropertiesException e)
			{
				throw new IllegalPropertiesException(
						prefix, properties.sourceDescription, e);
			}
			catch(final RuntimeException e)
			{
				throw new IllegalArgumentException(
						"property " + key + " in " + properties.sourceDescription + " invalid, see nested exception",
						e);
			}
			for(final Field<?> field : value.fields)
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


	@SuppressWarnings({"unused", "ResultOfObjectAllocationIgnored"})
	final <E> void copy(final String key, final Field<E> field)
	{
		new Field<>(key, field);
	}

	protected final IllegalPropertiesException newException(final String key, final String detail)
	{
		return newException(key, detail, null);
	}

	protected final IllegalPropertiesException newException(final String key, final String detail, final Throwable cause)
	{
		return new IllegalPropertiesException(key, source.getDescription(), detail, cause);
	}

	public final Set<String> getOrphanedKeys()
	{
		final Collection<String> keySet = source.keySet();
		if(keySet==null)
			return null;

		final HashSet<String> allowedValues = new HashSet<>();
		for(final Field<?> field : fields)
			allowedValues.add(field.key);

		final TreeSet<String> result = new TreeSet<>();
		for(final String key : keySet)
			if(!allowedValues.contains(key))
				result.add(key);

		return Collections.unmodifiableSet(result);
	}

	public final void ensureValidity(final String... prefixes)
	{
		final Collection<String> keySet = source.keySet();
		if(keySet==null)
			return;

		final HashSet<String> allowedValues = new HashSet<>();
		for(final Field<?> field : fields)
			allowedValues.add(field.key);

		final ArrayList<String> allowedPrefixes = new ArrayList<>();
		if(prefixes!=null)
			allowedPrefixes.addAll(asList(prefixes));

		for(final String key : keySet)
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
					final ArrayList<String> allowedValueList = new ArrayList<>();
					for(final Field<?> field : fields)
						allowedValueList.add(field.key);

					final StringBuilder bf = new StringBuilder();
					bf.append("property ").append(key).
						append(" in ").append(sourceDescription).
						append(" is not allowed, but only one of ").append(allowedValueList);
					if(!allowedPrefixes.isEmpty())
						bf.append(" or one starting with ").append(allowedPrefixes);
					bf.append('.');
					throw new IllegalArgumentException(bf.toString());
				}
			}
		}
	}

	public final void ensureEquality(final Properties other)
	{
		final Iterator<Field<?>> j = other.fields.iterator();
		for(final Iterator<Field<?>> i = fields.iterator(); i.hasNext()&&j.hasNext(); )
		{
			final Field<?> thisField = i.next();
			final Field<?> otherField = j.next();
			final boolean thisHideValue = thisField.hasHiddenValue();
			final boolean otherHideValue = otherField.hasHiddenValue();

			if(!thisField.key.equals(otherField.key))
				throw new RuntimeException("inconsistent fields");
			if(thisHideValue!=otherHideValue)
				throw new RuntimeException("inconsistent fields with hide value");

			final Object thisValue = thisField.getValue();
			final Object otherValue = otherField.getValue();

			if(!Objects.equals(thisValue, otherValue))
				throw new IllegalArgumentException(
						"inconsistent initialization for " + thisField.key +
						" between " + sourceDescription + " and " + other.sourceDescription +
						(thisHideValue ? "." : "," + " expected " + thisValue + " but got " + otherValue + '.'));
		}
	}


	// probe

	private static final class Prober implements Callable<Object>
	{
		private final Properties instance;
		private final Method method;
		private final String name;

		@SuppressFBWarnings("DP_DO_INSIDE_DO_PRIVILEGED")
		Prober(final Properties instance, final Method method, final Probe ann)
		{
			this.instance = instance;
			this.method = method;
			this.name = name(method, ann);

			if((method.getModifiers() & Modifier.STATIC)!=0)
				throw new IllegalArgumentException(
						"@Probe method must be non-static: " + method);
			if(method.getParameterCount()!=0)
				throw new IllegalArgumentException(
						"@Probe method must have no parameters: " + method);
			method.setAccessible(true);
		}

		private static String name(final Method method, final Probe ann)
		{
			final String override = ann.name();
			return override.isEmpty() ? stripProbeName(method.getName()) : override;
		}

		Prober prefix(final String key)
		{
			return new Prober(this, key);
		}

		Prober(final Prober template, final String prefix)
		{
			this.instance = template.instance;
			this.method = template.method;
			this.name = prefix + '.' + template.name;
		}

		@Override
		public Object call() throws Exception
		{
			try
			{
				return method.invoke(instance);
			}
			catch(final InvocationTargetException e)
			{
				final Throwable target = e.getTargetException();
				if(target instanceof Exception)
					throw (Exception)target;
				else if(target instanceof Error)
					throw (Error)target;
				else
					throw e;
			}
		}

		@Override
		public String toString()
		{
			return name;
		}
	}

	static final String stripProbeName(final String name)
	{
		final String PREFIX = "probe";
		if(name.length()<=PREFIX.length() ||
			!name.startsWith(PREFIX) ||
			Character.isLowerCase(name.charAt(PREFIX.length())))
			return name;

		return name.substring(PREFIX.length());
	}

	@SuppressWarnings("ThisEscapedInObjectConstruction")
	final ArrayList<Prober> probers = initProbes(this);

	static final ArrayList<Prober> initProbes(final Properties instance)
	{
		final ArrayList<Prober> result = new ArrayList<>();

		final ArrayList<Class<?>> classes = new ArrayList<>();
		for(
				Class<?> clazz = instance.getClass();
				!Properties.class.equals(clazz);
				clazz = clazz.getSuperclass())
			classes.add(clazz);

		for(final ListIterator<Class<?>> i = classes.listIterator(classes.size()); i.hasPrevious(); )
		{
			final Class<?> clazz = i.previous();
			final TreeMap<String,Prober> classMethods = new TreeMap<>();
			for(final Method method : clazz.getDeclaredMethods())
			{
				final Probe ann = method.getAnnotation(Probe.class);
				if(ann!=null)
					add(classMethods, new Prober(instance, method, ann));
			}
			result.addAll(classMethods.values());
		}

		return result;
	}

	private static void add(final TreeMap<String,Prober> probers, final Prober prober)
	{
		final Prober collision = probers.putIfAbsent(prober.name, prober);
		if(collision!=null)
		{
			final Prober a;
			final Prober b;
			if(collision.method.getName().compareTo(prober.method.getName())>0)
			{
				a = prober;
				b = collision;
			}
			else
			{
				a = collision;
				b = prober;
			}
			throw new IllegalArgumentException(
					"@Probe method has duplicate name '" + prober.name + "': " +
					a.method + " vs. " + b.method);
		}
	}

	/**
	 * Declares a method to be a probe.
	 * Probes are returned by {@link #getProbes()}.
	 * Probe methods must not be static and must have no parameters.
	 * <p>
	 * A probe may throw a {@link #newProbeAbortedException(String) ProbeAbortedException}
	 * to indicate, that this probe has been aborted because one of its preconditions is not fulfilled.
	 * This may happen, if the probe depends on something that does not exist in this
	 * properties instance.
	 */
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Probe
	{
		/**
		 * Specifies the name of the test.
		 * Defaults to the name of the method, stripped of prefix "probe" if present.
		 * Names must be unique within a class.
		 */
		String name() default "";
	}

	@SuppressWarnings("MethodMayBeStatic") // OK: should be called from instance context only
	protected final ProbeAbortedException newProbeAbortedException(final String message)
	{
		return new ProbeAbortedException(message);
	}

	public static final class ProbeAbortedException extends Exception
	{
		private ProbeAbortedException(final String message)
		{
			super(message);
		}

		private static final long serialVersionUID = 1l;
	}

	/**
	 * Returns all probes of this properties instance.
	 * Probes are methods annotated by {@link Probe}.
	 * The result includes probes of super classes and probes of
	 * {@link #value(String, Factory) nested} properties.
	 * @see #getTests()
	 */
	public final List<? extends Callable<?>> getProbes()
	{
		return Collections.unmodifiableList(probers);
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated
	 * Use {@link #Properties(Source)} instead.
	 * Using context is no longer supported.
	 */
	@Deprecated
	protected Properties(final Source source, final Source context)
	{
		this(source);
		//noinspection VariableNotUsedInsideIf OK: context no longer supported
		if(context!=null)
			throw new IllegalArgumentException(CONTEXT_NOT_SUPPORTED);
	}

	/**
	 * @deprecated Use {@link Sources#EMPTY} instead
	 */
	@Deprecated
	public static final Source EMPTY_SOURCE = Sources.EMPTY;

	/**
	 * @deprecated Use {@link #Properties(Source, Source)} instead.
	 */
	@Deprecated
	protected Properties(final java.util.Properties source, final String sourceDescription)
	{
		this(source, sourceDescription, null);
	}

	/**
	 * @deprecated Use {@link #Properties(Source, Source)} instead.
	 */
	@Deprecated
	protected Properties(final java.util.Properties source, final String sourceDescription, final Source context)
	{
		this(getSource(source, sourceDescription), context);
	}

	/**
	 * @throws IllegalStateException always
	 * @deprecated Using context is no longer supported.
	 */
	@Deprecated
	@SuppressWarnings("MethodMayBeStatic") // OK: context no longer supported
	public final String getContext(final String key)
	{
		if(key==null)
			throw new NullPointerException("key");
		throw new IllegalStateException(CONTEXT_NOT_SUPPORTED);
	}

	/**
	 * @deprecated
	 * Use {@link #Properties(Source)} instead.
	 * Using context is no longer supported.
	 * @throws IllegalStateException always
	 */
	@Deprecated
	@SuppressWarnings("MethodMayBeStatic") // OK: no longer supported
	public final Source getContext()
	{
		throw new IllegalStateException(CONTEXT_NOT_SUPPORTED);
	}

	@Deprecated
	private static final String CONTEXT_NOT_SUPPORTED = "context no longer supported";

	/**
	 * @deprecated Use {@link Sources#SYSTEM_PROPERTIES} instead
	 */
	@Deprecated
	public static final Source SYSTEM_PROPERTY_SOURCE = Sources.SYSTEM_PROPERTIES;

	/**
	 * @deprecated Use {@link Sources#SYSTEM_PROPERTIES} instead
	 */
	@Deprecated
	public static final Source getSystemPropertySource()
	{
		return Sources.SYSTEM_PROPERTIES;
	}

	/**
	 * @deprecated Use {@link Sources#SYSTEM_PROPERTIES} instead
	 */
	@Deprecated
	public static final Source getSystemPropertyContext()
	{
		return Sources.SYSTEM_PROPERTIES;
	}

	/**
	 * @deprecated Use {@link #getSource(java.util.Properties,String)} instead
	 */
	@Deprecated
	public static final Source getContext(final java.util.Properties properties, final String description)
	{
		return getSource(properties, description);
	}

	/**
	 * @deprecated Use {@link Sources#view(java.util.Properties, String)} instead.
	 */
	@Deprecated
	public static final Source getSource(final java.util.Properties properties, final String description)
	{
		return Sources.view(properties, description);
	}

	/**
	 * @deprecated Use {@link Sources#load(File)} instead.
	 */
	@Deprecated
	public static final Source getSource(final File file)
	{
		return Sources.load(file);
	}

	/**
	 * @deprecated Use {@link Sources#loadProperties(File)} instead.
	 */
	@Deprecated
	public static final java.util.Properties loadProperties(final File file)
	{
		return Sources.loadProperties(file);
	}
}
