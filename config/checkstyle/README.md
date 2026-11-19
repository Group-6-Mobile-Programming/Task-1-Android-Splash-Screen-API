# Checkstyle for UNEB e-Results

Style rules checked on every Jenkins build (see `Jenkinsfile`).

| Requirement | Checkstyle modules | Severity |
|---|---|---|
| Naming: camelCase methods, PascalCase classes, UPPER_SNAKE_CASE constants | `MethodName`, `TypeName`, `ConstantName`, `MemberName`, `ParameterName`, `LocalVariableName`, `PackageName` | error |
| Javadoc on public classes and methods | `MissingJavadocType`, `MissingJavadocMethod` | error |
| No magic numbers | `MagicNumber` | error |
| Max line length 100 | `LineLength` | error |
| Spaces around operators and after commas | `WhitespaceAround`, `WhitespaceAfter` | error |
| Other whitespace and indentation | `Indentation`, `NoWhitespaceBefore`, `ParenPad`, ... | warning |
| TODO / FIXME comments | `TodoComment` | info |

* **error** fails the build, **warning** and **info** are only reported.
* Generated code (`R.java`, `BuildConfig.java`) is skipped via `suppressions.xml`.

## Run it locally

Download `checkstyle-10.21.0-all.jar` from the Checkstyle releases page, then from the project root:

```
java -Dconfig_loc=config/checkstyle -jar checkstyle-10.21.0-all.jar -c config/checkstyle/checkstyle.xml app/src/main/java
```

The exit code is the number of `error` violations (0 = pass).
