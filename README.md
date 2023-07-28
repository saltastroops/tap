# SALT TAP service
TAP service implementation that supports the IVOA [TAP-1.1](http://www.ivoa.net/documents/TAP/20190927/) web service, aimed at loading astronomical catalogues the Southern African Large Telescope (SALT).

## Dependencies
- [Java 11+](https://www.oracle.com/za/java/technologies/downloads/archive/)
- [Apache Tomcat 9.0.78](https://tomcat.apache.org/download-90.cgi)
- [Apache Maven 3.9+](https://maven.apache.org/download.cgi)
- `postgresql-42.6.0.jar`
- `jakarta-servlet-api-4.jar`
- `stil-4.1.jar`
- `servlet-api.jar`
- `java-json.jar`

## Other requirements
- [Gradle 2.5+](http://www.gradle.org/)
- `tap_2.3.jar`

**Important note:**
Instead of using an existing `tap_*.jar` file, you have to generate it from source code to avoid getting errors. To do this, clone the following repository: https://www.ict.inaf.it/gitlab/zorba/vollt.git. Build the source code as a jar file using Gradle, by following the instructions provided in the documentation. Lastly, rename the built jar file to `tap_2.3.jar`.

## Configuration
Include the following properties in the `{TomcatHome}/conf/catalina.properties` file to configure the tomcat server and some of the java libraries used in the service:
```
sdb.url={database url} (without the url scheme, just the host and the port)
sdb.database={database name}
sdb.username={database username}
sdb.password={database password}
```

## Building the service
In order to build the TAP service library, all the libraries (JARs) listed above must be provided in the classpath of your Web Application Server. To do this, put the JAR in following folder of the service: `src/main/webapp/WEB-INF/lib`. Once this is done, build the war file using `Apache Maven`.

## Deployment
Use `Apache Tomcat` to deploy the generated war file.