package com.exedio.cope.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.TemporaryFolder;

@ExtendWith(TemporaryFolder.Extension.class)
public class PropertiesValidatorMainTest
{
	static final class TestProperties extends Properties
	{
		@SuppressWarnings("unused")
		final boolean testBoolean = value("testBoolean", false);

		@SuppressWarnings("unused")
		final String mandatoryString = value("mandatoryString", (String) null);

		private TestProperties(final Source source)
		{
			super(source);
		}
	}

	static final class AdditionalParameterTestProperties extends Properties
	{
		@SuppressWarnings("unused")
		final boolean testBoolean = value("testBoolean", false);

		@SuppressWarnings("unused")
		final String mandatoryString = value("mandatoryString", (String) null);

		@SuppressWarnings("unused")
		private AdditionalParameterTestProperties(final Source source, final String additionalParameter)
		{
			super(source);
		}
	}

	@Test void testSuccess(final TemporaryFolder folder) throws IOException
	{
		final File file = folder.newFile("testSuccess");
		final java.util.Properties p = new java.util.Properties();
		p.setProperty("testBoolean", "true");
		p.setProperty("mandatoryString", "testValue");
		store(p, file);
		final PropertiesValidatorMain.Result result = PropertiesValidatorMain.validate(file.getAbsolutePath(), TestProperties.class.getName());
		assertNull(result.message);
		assertNull(result.exception);
		assertEquals(0, result.exitCode);
	}

	@Test void testMissingProperty(final TemporaryFolder folder) throws IOException
	{
		final File file = folder.newFile("testMissingProperty");
		final java.util.Properties p = new java.util.Properties();
		p.setProperty("testBoolean", "true");
		store(p, file);
		final PropertiesValidatorMain.Result result = PropertiesValidatorMain.validate(file.getAbsolutePath(), TestProperties.class.getName());
		assertNull(result.message);
		assertNotNull(result.exception);
		assertEquals(IllegalPropertiesException.class, result.exception.getClass());
		assertEquals("property mandatoryString in "+file.getAbsolutePath()+" must be specified as there is no default", result.exception.getMessage());
		assertEquals(1, result.exitCode);
	}

	@Test void testInvalidPropertyType(final TemporaryFolder folder) throws IOException
	{
		final File file = folder.newFile("testInvalidPropertyType");
		final java.util.Properties p = new java.util.Properties();
		p.setProperty("mandatoryString", "testValue");
		p.setProperty("testBoolean", "noBoolean");
		store(p, file);
		final PropertiesValidatorMain.Result result = PropertiesValidatorMain.validate(file.getAbsolutePath(), TestProperties.class.getName());
		assertNull(result.message);
		assertNotNull(result.exception);
		assertEquals(IllegalPropertiesException.class, result.exception.getClass());
		assertEquals("property testBoolean in "+file.getAbsolutePath()+" must be either 'true' or 'false', but was 'noBoolean'", result.exception.getMessage());
		assertEquals(1, result.exitCode);
	}

	@Test void testNoFile(final TemporaryFolder folder)
	{
		final File file = new File(folder.getRoot(), "testNoFile.inexist");
		final PropertiesValidatorMain.Result result = PropertiesValidatorMain.validate(file.getAbsolutePath(), TestProperties.class.getName());
		assertEquals("Properties file not found or is no file: "+file.getAbsolutePath(), result.message);
		assertNull(result.exception);assertEquals(1, result.exitCode);
	}

	@Test void testMissingClass(final TemporaryFolder folder) throws IOException
	{
		final File file = folder.newFile("testMissingClass");
		final PropertiesValidatorMain.Result result = PropertiesValidatorMain.validate(file.getAbsolutePath(), TestProperties.class.getName()+"Missing");
		assertEquals("Class not found: "+TestProperties.class.getName()+"Missing", result.message);
		assertNull(result.exception);assertEquals(1, result.exitCode);
	}

	@Test void testWrongClass(final TemporaryFolder folder) throws IOException
	{
		final File file = folder.newFile("testWrongClass");
		final PropertiesValidatorMain.Result result = PropertiesValidatorMain.validate(file.getAbsolutePath(), PropertiesValidatorMainTest.class.getName());
		assertEquals("Class is no Properties Class: "+PropertiesValidatorMainTest.class.getName(), result.message);
		assertNull(result.exception);assertEquals(1, result.exitCode);
	}

	@Test void testMissingConstructor(final TemporaryFolder folder) throws IOException
	{
		final File file = folder.newFile("testMissingConstructor");
		final PropertiesValidatorMain.Result result = PropertiesValidatorMain.validate(file.getAbsolutePath(), AdditionalParameterTestProperties.class.getName());
		assertEquals("No constructor with source param found for class: "+AdditionalParameterTestProperties.class.getName(), result.message);
		assertNull(result.exception);assertEquals(1, result.exitCode);
	}

	@Test void testOrphanedKeys(final TemporaryFolder folder) throws IOException
	{
		final File file = folder.newFile("testOrphanedKeys");
		final java.util.Properties p = new java.util.Properties();
		p.setProperty("testBoolean", "true");
		p.setProperty("mandatoryString", "testValue");
		p.setProperty("orphanedKey", "testValue");
		p.setProperty("orphanedKey2", "testValue");
		store(p, file);
		final PropertiesValidatorMain.Result result = PropertiesValidatorMain.validate(file.getAbsolutePath(), TestProperties.class.getName());
		assertEquals("Orphaned Keys:"+System.lineSeparator()+"orphanedKey"+System.lineSeparator()+"orphanedKey2"+System.lineSeparator(),result.message);
		assertNull(result.exception);
		assertEquals(0, result.exitCode);
	}

	private static void store(final java.util.Properties p, final File file) throws IOException
	{
		try(FileOutputStream s = new FileOutputStream(file))
		{
			p.store(s, null);
		}
	}
}
