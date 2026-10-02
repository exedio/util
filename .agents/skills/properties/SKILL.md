---
name: properties
description: Design, edit, or debug Exedio configuration classes built on com.exedio.cope.util.Properties, including typed values, nested prefixes, service factories, validation, and probes. Use when that API matters; ordinary java.util.Properties usage is out of scope.
---

# Exedio configuration properties

In this codebase the typed configuration base class is `com.exedio.cope.util.Properties`. Cope classes such as `ConnectProperties` import that type; do not confuse it with `java.util.Properties`, which is often just an input source. Check imports before changing a class. The requested name `com.exedio.cope.Properties` is not the base class used here.

Before changing behavior, read the relevant methods in `src/com/exedio/cope/util/Properties.java` and a focused example or test. The API has deliberate parsing, default, validation, and error-reporting semantics; do not replace it with ad hoc parsing.

## Defining configuration

- Subclass `Properties`, accept a `Properties.Source`, and pass it to `super(source)`. Declare values as fields initialized with the protected `value...` methods. Parsing and validation happen as the fields initialize, so a missing required value or malformed input fails during construction.
- Prefer immutable `final` fields. Use `newException(key, detail)` in the constructor body for constraints involving multiple parsed fields so the error identifies the property and source.
- Select the overload that expresses the intended contract: for example, `value("enabled", false)`, `value("workers", 8, 1)`, or `value("label", "default")`. A null default means required only for overloads that support it, such as `value("token", (String)null)`. Integer and duration overloads also validate their declared bounds and defaults.
- Use `valueHidden` for secret strings so field metadata marks them as hidden. Use the built-in typed readers for enums, dates, durations, paths, zones, charsets, lists, and service classes instead of parsing their strings separately.
- Avoid deprecated `field(...)` readers and deprecated constructors that accept `java.util.Properties`; use the value readers and a `Source` from `Sources` (`view`, `load`, `cascade`, or another appropriate source).

## Nested configuration and services

Use `valnp("prefix", Child::new)` for a nested `Properties` object. Its child's keys are flat and prefixed, such as `prefix.timeout`; nested fields are included in the parent's field list and checked for key collisions. `valnp` also avoids a documented Java overload-ambiguity issue with `value`. For an optional nested object, use `value("prefix", false, Child::new)` and handle the resulting nullable value.

Use `valueService` when configuration selects a service implementation. Follow its constructor and `@ServiceProperties` requirements, and inspect `ServiceFactory` and nearby implementations rather than adding custom reflection or class loading.

## Validation and diagnostics

Unknown-key checks are explicit: call `ensureValidity(...)` where the configuration boundary should reject unrecognized keys. Optional allowed prefixes should be narrowly chosen. `getOrphanedKeys()` can aid diagnostics, but it returns `null` when the source cannot enumerate keys; do not treat that as an empty set. Use `getField` or `getFields` for metadata and `isSpecified()` to distinguish an explicit value from a default.

Methods annotated with `@Probe` are operational checks exposed through `getProbes()`. They must be non-static and take no arguments; names must be unique in their class. Nested probes are automatically prefixed. Throw `newProbeAbortedException(...)` only when a probe's precondition is absent, not to report a failed check.

When investigating a surprising result, trace the `Source` chain and the exact field declaration first. The source description is retained for configuration errors, and nested prefixes are added by the framework; manually stripping prefixes or flattening sources can change both accepted keys and diagnostics.
