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

import static com.exedio.cope.util.ExedioVersions.register;
import static com.exedio.cope.util.ExedioVersions.spec;
import static java.time.LocalDate.of;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class ExedioVersionsTest
{
	@Test void testSpec()
	{
		assertSpec("master", 10858, of(2022,  9,  9), "build master 10858 2022-09-09 " + GIT1 + " " + GIT2); // cope
		assertSpec("master",  7100, of(2022,  5,  2), "build master 7100 2022-05-02 "  + GIT1 + " " + GIT2); // copeconsole
		assertSpec("main",    7100, of(2022,  5,  2), "build main 7100 2022-05-02 "    + GIT1 + " " + GIT2); // copeconsole with main instead of master
		assertSpec("master",    63, of(2021,  4, 20), "build master 63 2021-04-20 "    + GIT1 + " " + GIT2); // copehistory
		assertSpec("master",   367, of(2021, 12, 14), "build master 367 2021-12-14 "   + GIT1 + " " + GIT2); // copeim4java
		assertSpec("master",   395, of(2021,  6, 29), "build master 395 2021-06-29 "   + GIT1 + " " + GIT2); // copepatch
		assertSpec("master",   395, of(2021,  2, 28), "build master 395 2021-02-30 "   + GIT1 + " " + GIT2); // copepatch with broken date
		assertSpec("master",   978, of(2022,  5,  9), "build master 978 2022-05-09 "   + GIT1 + " " + GIT2); // copeutil
		assertSpec("master",    54, of(2021,  7, 27), "build master 54 2021-07-27 "    + GIT1 + " " + GIT2); // copewicket
		assertSpec("master",   384, of(2022,  5,  9), "build master 384 2022-05-09 "   + GIT1 + " " + GIT2); // copecron
		assertSpec("master",   123, of(2021,  8,  4), "build master 123 2021-08-04 "   + GIT1 + " " + GIT2); // copevault
		assertSpec("master",    49, of(2022,  2, 14), "build master 49 2022-02-14 "    + GIT1 + " " + GIT2); // servletvault
		assertSpec("master",   175, of(2021,  9, 24), "build master 175 2021-09-24 "   + GIT1 + " " + GIT2); // wicket.behaviour
		assertSpec("master",   345, of(2020,  8, 11), "git master " + GIT1 + " " + GIT2 + " jenkins 345 2020-08-11_07-42-06"); // sendmail
		assertSpec("master",  3060, of(2020,  4,  6), "git master " + GIT1 + " " + GIT2 + " jenkins 3060 2020-04-06_09-02-23"); // copaiba
		assertSpec("master",   540, of(2019, 11, 26), "git master " + GIT1 + " " + GIT2 + " jenkins 540 2019-11-26_13-39-31"); // cops
		assertSpec("master",    68, of(2020, 12,  3), "git master " + GIT1 + " " + GIT2 + " jenkins 68 2020-12-03_15-58-24"); // insightwysiwyg
		assertSpec("master",   107, of(2018,  5, 23), "git master " + GIT1 + " " + GIT2 + " jenkins 107 2018-05-23_18-43-46"); // servletutil
		assertSpec("wicket9",  985, of(2022,  8, 29), "build wicket9 985 2022-08-29 " + GIT1 + " " + GIT2); // insight
		assertSpec("wicket10",  85, of(2024, 10, 31), "build wicket10 85 2024-10-31 " + GIT1 + " " + GIT2); // insight
		assertSpec("master",    74, null,             "git origin/master " + GIT1 + " jenkins 74 ldapconnector 74");
		assertSpec("master",     4, of(2018,  2, 14), "git origin/master " + GIT1 + " jenkins 4 tomcatfilestore_tomcat8 2018-02-14_14-59-29");
	}

	private static void assertSpec(
			final String branch,
			final int revision,
			final LocalDate date,
			final String input)
	{
		final ExedioVersions.Spec actual = spec(input);
		assertNotNull(actual, "actual");
		assertAll(
				() -> assertEquals(branch, actual.branch, "branch"),
				() -> assertEquals(revision, actual.revision, "revision"),
				() -> assertEquals(date, actual.date, "date"));
	}

	private static final String GIT1 = "c85aff35c85aff35c85aff35c85aff35c85aff35";
	private static final String GIT2 = "5adbd86f5adbd86f5adbd86f5adbd86f5adbd86f";


	@Test void testRegister()
	{
		final SimpleMeterRegistry registry = new SimpleMeterRegistry();
		register(registry, new Pack("com.exedio.copeutil", "build master 978 2022-05-09"));
		final Gauge revision = (Gauge)registry.getMeters().get(0);
		assertEquals(
				"MeterId{" +
				"name='com.exedio.cope.util.ExedioVersions.revision', " +
				"tags=[tag(branch=master),tag(package=com.exedio.copeutil)]}",
				revision.getId().toString());
		assertEquals(978, revision.value());
		final Gauge date = (Gauge)registry.getMeters().get(1);
		assertEquals(
				"MeterId{" +
				"name='com.exedio.cope.util.ExedioVersions.date', " +
				"tags=[tag(branch=master),tag(package=com.exedio.copeutil)]}",
				date.getId().toString());
		assertEquals(of(2022, 5, 9).toEpochDay(), date.value());
		assertEquals(2, registry.getMeters().size());
	}

	@Test void testRegisterSubPackage()
	{
		final SimpleMeterRegistry registry = new SimpleMeterRegistry();
		register(registry,
				new Pack("com.exedio.copeutil",     "build master 978 2022-05-09"),
				new Pack("com.exedio.copeutil.sub", "build master 978 2022-05-09"));
		assertEquals(asList(
				"MeterId{" +
					"name='com.exedio.cope.util.ExedioVersions.revision', " +
					"tags=[tag(branch=master),tag(package=com.exedio.copeutil)]}",
				"MeterId{" +
					"name='com.exedio.cope.util.ExedioVersions.date', " +
					"tags=[tag(branch=master),tag(package=com.exedio.copeutil)]}"),
				registry.getMeters().stream().map(m -> m.getId().toString()).collect(toList()));
	}

	@Test void testRegisterSubPackageFirst()
	{
		final SimpleMeterRegistry registry = new SimpleMeterRegistry();
		register(registry,
				new Pack("com.exedio.copeutil.sub", "build master 978 2022-05-09"),
				new Pack("com.exedio.copeutil",     "build master 978 2022-05-09"));
		assertEquals(asList(
				"MeterId{" +
					"name='com.exedio.cope.util.ExedioVersions.revision', " +
					"tags=[tag(branch=master),tag(package=com.exedio.copeutil)]}",
				"MeterId{" +
					"name='com.exedio.cope.util.ExedioVersions.date', " +
					"tags=[tag(branch=master),tag(package=com.exedio.copeutil)]}"),
				registry.getMeters().stream().map(m -> m.getId().toString()).collect(toList()));
	}

	@Test void testRegisterOld()
	{
		final SimpleMeterRegistry registry = new SimpleMeterRegistry();
		register(registry,
				new Pack("com.exedio.xxx", "git master abc jenkins 345 2020-08-11_07-42-06"));
		assertEquals(asList(
				"MeterId{" +
					"name='com.exedio.cope.util.ExedioVersions.date', " +
					"tags=[tag(branch=master),tag(package=com.exedio.xxx)]}",
				"MeterId{" +
					"name='com.exedio.cope.util.ExedioVersions.revision', " +
					"tags=[tag(branch=master),tag(package=com.exedio.xxx)]}"),
				registry.getMeters().stream().map(m -> m.getId().toString()).sorted().collect(toList()));
	}

	@Test void testRegisterOldWithoutDate()
	{
		final SimpleMeterRegistry registry = new SimpleMeterRegistry();
		register(registry,
				new Pack("com.exedio.xxx", "git master abc jenkins 345"));
		assertEquals(asList(
				"MeterId{" +
					"name='com.exedio.cope.util.ExedioVersions.revision', " +
					"tags=[tag(branch=master),tag(package=com.exedio.xxx)]}"),
				registry.getMeters().stream().map(m -> m.getId().toString()).sorted().collect(toList()));
	}

	static class Pack implements ExedioVersions.Pack
	{
		final String name;
		final String version;

		Pack(final String name, final String version)
		{
			this.name = name;
			this.version = version;
		}

		@Override
		public String getName()
		{
			return name;
		}

		@Override
		public String getSpecificationVersion()
		{
			return version;
		}
	}
}
