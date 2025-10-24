package com.exedio.cope.util;

import static com.exedio.cope.junit.Assert.assertFails;
import static com.exedio.cope.junit.CopeAssert.assertEqualsUnmodifiable;
import static com.exedio.cope.util.Sources.view;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;

public class PropertiesListTest
{
	@Test
	void testMinimal()
	{
		final MyProps props = new MyProps(new java.util.Properties());
		props.assertIt();
		assertEqualsUnmodifiable(
				asList(
						props.stringsCount,
						props.withDefaultsCount, props.withDefaults0, props.getField("withDefaults.1"),
						props.filesCount,
						props.integersCount, props.getField("integers.0")),
				props.getFields());
		assertEquals(List.of(), props.strings);
		assertEquals(List.of("defA", "defB"), props.withDefaults);
	}

	@Test
	void setStrings()
	{
		final java.util.Properties p = new java.util.Properties();
		p.setProperty("strings.count", "2");
		p.setProperty("strings.0", "foo");
		p.setProperty("strings.1", "bar");
		final MyProps props = new MyProps(p);
		props.assertIt();
		assertEqualsUnmodifiable(
				asList(props.stringsCount, props.getField("strings.0"), props.getField("strings.1")),
				props.getFields("strings"));
		assertEquals(List.of("foo", "bar"), props.strings);
	}

	@Test
	void dropDefaults()
	{
		final java.util.Properties p = new java.util.Properties();
		p.setProperty("withDefaults.count", "1");
		final MyProps props = new MyProps(p);
		props.assertIt();
		assertEqualsUnmodifiable(
				asList(props.withDefaultsCount, props.withDefaults0),
				props.getFields("withDefaults"));
		assertEquals(List.of(), props.strings);
		assertEquals(List.of("defA"), props.withDefaults);
	}

	@Test
	void belowMinimum()
	{
		final java.util.Properties p = new java.util.Properties();
		p.setProperty("withDefaults.count", "0");
		assertFails(
				() -> new MyProps(p),
				IllegalPropertiesException.class,
				"property withDefaults.count in sourceDescription must be an integer greater or equal 1, but was 0"
		);
	}

	@Test
	void addToDefaults()
	{
		final java.util.Properties p = new java.util.Properties();
		p.setProperty("withDefaults.count", "3");
		p.setProperty("withDefaults.2", "add");
		final MyProps props = new MyProps(p);
		props.assertIt();
		assertEquals(List.of("defA", "defB", "add"), props.withDefaults);
	}

	@Test
	void missingValue()
	{
		final java.util.Properties p = new java.util.Properties();
		p.setProperty("strings.count", "1");
		assertFails(
				() -> new MyProps(p),
				IllegalPropertiesException.class,
				"property strings.0 in sourceDescription must be specified as there is no default");
	}

	@Test
	void setFiles()
	{
		final java.util.Properties p = new java.util.Properties();
		p.setProperty("files.count", "1");
		p.setProperty("files.0", "foo");
		final MyProps props = new MyProps(p);
		props.assertIt();
		assertEqualsUnmodifiable(
				asList(props.filesCount, props.getField("files.0")),
				props.getFields("files"));
		assertEquals(List.of(new File("foo")), props.files);
	}

	@Test
	void setIntegers()
	{
		final java.util.Properties p = new java.util.Properties();
		p.setProperty("integers.count", "1");
		final MyProps props = new MyProps(p);
		props.assertIt();
		assertEqualsUnmodifiable(
				asList(props.integersCount, props.getField("integers.0")),
				props.getFields("integers"));
		assertEquals(List.of(123), props.integers);

		p.setProperty("integers.0", "42");
		assertEquals(List.of(42), new MyProps(p).integers);

		p.setProperty("integers.0", "39");
		assertFails(
				() -> new MyProps(p),
				IllegalPropertiesException.class,
				"property integers.0 in sourceDescription must be an integer greater or equal 40, but was 39"
		);
	}

	@Test
	void integerMissingDefault()
	{
		assertFails(
				() -> new EmptyProps().valueIntegerList("broken", 2, 0, List.of(0)),
				IllegalArgumentException.class,
				"default of broken must not have size smaller than minSize of 2, but was [0]"
		);
		assertFails(
				() -> new EmptyProps().valueIntegerList("broken", 1, 0, asList((Integer)null)),
				IllegalArgumentException.class,
				"default of broken must not contain null"
		);
		assertFails(
				() -> new EmptyProps().valueIntegerList("broken", 1, 0, null),
				NullPointerException.class,
				"integer list needs defaults"
		);
	}

	@Test
	void integerInvalidDefault()
	{
		assertFails(
				() -> new EmptyProps().valueIntegerList("broken", 1, 0, List.of(-1)),
				IllegalArgumentException.class,
				"default of broken.0 must not be smaller than minimum of 0, but was -1"
		);
	}

	@Test
	void complexMissing()
	{
		assertFails(
				() -> new ComplexProps(new java.util.Properties()),
				IllegalPropertiesException.class,
				"property sub.0.d in sourceDescription must be specified as there is no default",
				IllegalPropertiesException.class
		);
	}

	@Test
	void complexSet()
	{
		final java.util.Properties p = new java.util.Properties();
		p.setProperty("sub.0.d", "PT2M");
		final ComplexProps props = new ComplexProps(p);
		assertEquals(1, props.sub.size());
		assertEquals(0, props.sub.get(0).a);
		assertEquals(Duration.ofMinutes(2), props.sub.get(0).d);
	}

	static final class MyProps extends MyProperties
	{
		final List<String> strings = valueStringList("strings", 0);
		final List<String> withDefaults = valueStringList("withDefaults", 1, List.of("defA", "defB"));
		final List<File> files = valueList("files", this::valueFile, 0);
		final List<Integer> integers = valueIntegerList("integers", 0, 40, List.of(123));

		final Field<?> stringsCount = getField("strings.count");
		final Field<?> withDefaultsCount = getField("withDefaults.count");
		final Field<?> withDefaults0 = getField("withDefaults.0");
		final Field<?> filesCount = getField("files.count");
		final Field<?> integersCount = getField("integers.count");

		MyProps(final java.util.Properties source)
		{
			super(view(source, "sourceDescription"));
		}

		void assertIt()
		{
			assertEqualsUnmodifiable(asList(), getProbes());
			assertEquals("strings.count", stringsCount.getKey());
			assertEquals(0, stringsCount.getDefaultValue());
			assertEquals(false, stringsCount.hasHiddenValue());
		}

		List<Field<?>> getFields(final String keyStart)
		{
			return getFields().stream().filter( f -> f.getKey().startsWith(keyStart) ).toList();
		}
	}

	static final class EmptyProps extends Properties
	{
		EmptyProps()
		{
			super(view(new java.util.Properties(), "sourceDescription"));
		}
	}

	static final class ComplexProps extends MyProperties
	{
		final List<SubProps> sub = valueList("sub", k -> valnp(k, SubProps::new), 1);

		ComplexProps(final java.util.Properties source)
		{
			super(view(source, "sourceDescription"));
		}
	}

	static final class SubProps extends Properties
	{
		final int a = value("a", 0, 0);
		final Duration d = value("d", null, Duration.ZERO);

		SubProps(final Source source)
		{
			super(source);
		}
	}
}
