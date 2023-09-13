# server-compatibility-maven-plugin

This Plugin opens each JAR file within the final archive and removes the classes compiled in Java 9+.
This is designed to solve problems with deployment on incompatible application servers, for example, which include older versions of the bytecode handling library.

In particular, you can resolve the error below when deploying an application on weblogic 12.1.3.

```text
<Error> <J2EE> <BEA-160228> <AppMerge failed to merge your application. If you are running AppMerge on the command-line, merge again with the -verbose option for more details. See the error message(s) below.>
```
## Usage
```xml
<plugin>
    <groupId>io.github.jeancnasc</groupId>
    <artifactId>server-compatibility-maven-plugin</artifactId>
    <version>1.0.0</version>
    <executions>
        <execution>
            <id>compatibility</id>
            <goals>
                <goal>java8-runtime-compatibility</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```