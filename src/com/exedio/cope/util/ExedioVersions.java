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

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class ExedioVersions
{
	@SuppressWarnings("unused") // OK: cannot be tested
	public static void register(final MeterRegistry registry)
	{
		register(registry, Stream.of(Package.getPackages()).map(p -> new Pack(){
			@Override
			public String name()
			{
				return p.getName();
			}
			@Override
			public String version()
			{
				return p.getSpecificationVersion();
			}
		}));
	}

	interface Pack extends Comparable<Pack>
	{
		String name();
		String version();

		@Override
		default int compareTo(final Pack o)
		{
			return name().compareTo(o.name());
		}
	}

	static void register(final MeterRegistry registry, final Stream<Pack> packages)
	{
		final HashSet<String> versionsDone = new HashSet<>();
		packages.
				filter(p -> p.name().startsWith("com.exedio.")).
				sorted().
				forEach(pack ->
		{
			final String packName = pack.name();
			final String version = pack.version();
			if(version==null || version.isEmpty() || !versionsDone.add(version))
				return;

			final Spec spec = spec(version);
			if(spec==null)
				return;

			final String className = ExedioVersions.class.getName();
			final Tags tags = Tags.of(
					"package", packName,
					"branch", spec.branch);
			final double revision = spec.revision;
			Gauge.builder(className + ".revision", () -> revision).
					tags(tags).
					register(registry);

			if(spec.date!=null)
			{
				final double date = spec.date.toEpochDay();
				Gauge.builder(className + ".date", () -> date).
						tags(tags).
						register(registry);
			}
		});
	}

	static Spec spec(final String input)
	{
		final String BRANCH = "(?:origin/)?+(?<branch>master|main|wicket\\d{1,2})";
		final String REVISION = "(?<revision>\\d{1,6})";
		final String DATE = "(?<date>\\d{4}-\\d{2}-\\d{2})(?:_\\d{2}-\\d{2}-\\d{2})?";
		for(final String regex : new String[] {
				"^build " + BRANCH + " " + REVISION + " " + DATE + "\\b.*$",
				"^git " + BRANCH + "\\b.*\\bjenkins " + REVISION + "\\b.*\\b" + DATE + "\\b.*$",
				"^git " + BRANCH + "\\b.*\\bjenkins " + REVISION + "\\b.*$"})
		{
			final Pattern pattern = Pattern.compile(regex);
			final Matcher matcher = pattern.matcher(input);
			if(matcher.matches())
			{
				final String date = groupOptional(matcher, "date");
				return new Spec(
						matcher.group("branch"),
						Integer.parseInt(matcher.group("revision")),
						date!=null ? LocalDate.from(DateTimeFormatter.ofPattern("uuuu-MM-dd", Locale.US).parse(date)) : null);
			}
		}

		return null;
	}

	private static String groupOptional(final Matcher matcher, final String name)
	{
		try
		{
			return matcher.group(name);
		}
		catch(final IllegalArgumentException ignored)
		{
			return null;
		}
	}

	record Spec(String branch, int revision, LocalDate date)
	{
	}

	private ExedioVersions()
	{
		// prevent instantiation
	}
}
