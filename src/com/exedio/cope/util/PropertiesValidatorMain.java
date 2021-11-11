package com.exedio.cope.util;

import com.exedio.cope.util.Properties.Source;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

/**
 * Class with main method to validate a properties file against a Properties class.
 * This checks only syntax: if the Properties class can be instantiated using the given file with no exception.
 *
 * Arguments:
 *  - Properties file: path (absolute path is strongly recommended)
 *  - Properties class name: full qualified class name of the properties class.
 */
public final class PropertiesValidatorMain
{
	private PropertiesValidatorMain()
	{
		// prevent instantiation
	}

	@SuppressWarnings("CallToSystemExit")
	public static void main(final String[] args)
	{
		try
		{
			if (args.length != 2)
			{
				System.err.println("Expected 2 arguments (file path and class name) but got "+args.length);
				System.exit(1);
			}
			final String filePath = args[0];
			final String className = args[1];

			final Result validationResult = validate(filePath, className);
			if ( validationResult.exitCode == 0 )
			{
				if (validationResult.message != null)
					System.out.println(validationResult.message);
				else
					System.out.println("ok");
			}
			else
			{
				if (validationResult.message != null)
					System.err.println(validationResult.message);
				else
					validationResult.exception.printStackTrace();
				System.exit(validationResult.exitCode);
			}
		}
		// handle any uncaught runtime exception
		catch (final RuntimeException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	static Result validate(final String filePath, final String className)
	{
		final File propertiesFile = new File(filePath);

		if (!propertiesFile.isFile())
		{
			return Result.failure("Properties file not found or is no file: "+filePath);
		}

		final Class<?> propertiesClassCandidate;
		try
		{
			propertiesClassCandidate = Class.forName(className);
		}
		catch (final ClassNotFoundException e)
		{
			return Result.failure("Class not found: "+className);
		}
		final Class<? extends Properties> propertiesClass;
		try
		{
			propertiesClass = propertiesClassCandidate.asSubclass(Properties.class);
		}
		catch (final ClassCastException e)
		{
			return Result.failure("Class is no Properties Class: "+className);
		}
		final Constructor<? extends Properties> constructor;
		try
		{
			constructor = propertiesClass.getDeclaredConstructor(Source.class);
			// allow also private constructors
			constructor.setAccessible(true);
		}
		catch (final NoSuchMethodException e)
		{
			return Result.failure("No constructor with source param found for class: "+className);
		}

		try
		{
			final Properties properties = constructor.newInstance(Sources.load(propertiesFile));
			final Set<String> orphanedKeys = properties.getOrphanedKeys();
			if (orphanedKeys == null || orphanedKeys.isEmpty())
				return Result.success(null);
			else
			{
				final StringBuilder message = new StringBuilder("Orphaned Keys:").append(System.lineSeparator());
				for (final String key : orphanedKeys)
				{
					message.append(key).append(System.lineSeparator());
				}
				return Result.success(message.toString());
			}
		}
		catch (final IllegalArgumentException | InstantiationException | IllegalAccessException e)
		{
			return Result.failure(e);
		}
		catch (final InvocationTargetException e)
		{
			return Result.failure(e.getCause() instanceof Exception ? (Exception) e.getCause() : e);
		}
	}

	static final class Result
	{
		final int exitCode;
		final String message;
		final Exception exception;

		private Result(final int exitCode, final String message, final Exception exception)
		{
			this.exitCode = exitCode;
			this.message = message;
			this.exception = exception;
		}

		private static Result failure(final Exception cause)
		{
			return new Result(1, null, cause);
		}

		private static Result failure(final String message)
		{
			return new Result(1, message, null);
		}

		private static Result success(final String message)
		{
			return new Result(0, message, null);
		}
	}
}
