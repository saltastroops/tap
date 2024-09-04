# SALT TAP service
TAP service implementation that supports the IVOA [TAP-1.1](http://www.ivoa.net/documents/TAP/20190927/) web service, aimed at loading astronomical catalogues the Southern African Large Telescope (SALT).

This service was implemented by following and adapting code from [TAPTuto](http://cdsportal.u-strasbg.fr/taptuto/gettingstarted_servlet.html) demo.

## Dependencies
- [Java 11+](https://www.oracle.com/za/java/technologies/downloads/archive/)
- [Apache Tomcat 10.1.28](https://tomcat.apache.org/download-10.cgi)
- [Apache Maven 3.9+](https://maven.apache.org/download.cgi)

## Other requirements
- [Gradle 2.5+](http://www.gradle.org/)

**Important note:**
Instead of using a `tap_*.jar` file provided in the demo, you have to generate it from source code to avoid getting errors. To do this, clone the following repository: https://github.com/mofokeng-chaka/vollt.git. 
Build the source code as a jar file using Gradle, by following the instructions provided in the documentation. Lastly, install the built jar file in the local repository of maven, using the following command:

```
mvn install:install-file -Dfile=/path/to/the/tap/library.jar -DgroupId=org.example -DartifactId=tap-service -Dversion=2.3 -Dpackaging=jar
```

[//]: # (Also include the following in the `pom.xml` file:)

[//]: # (```xml)

[//]: # (<dependency>)

[//]: # (    <groupId>org.examples</groupId>)

[//]: # (    <artifactId>tap-service</artifactId>)

[//]: # (    <version>2.3</version>)

[//]: # (</dependency>)

[//]: # (```)

## Configuration
Include the following properties in the `src/main/webapp/WEB-INF/classes/application.properties` file to configure the tomcat server and some of the java libraries used in the service:
```
sdb.url={database url} (without the url scheme, just the host and the port)
sdb.database={database name}
sdb.username={database username}
sdb.password={database password}
```

## Building the service
Build the war file using `Apache Maven`.
```shell
mvn clean install compile
mvn war:war
```

## Deployment
Use `Apache Tomcat` to deploy the generated war file.
**Important note:**
Set custom context root to `/`