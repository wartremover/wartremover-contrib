## Maven

To use `wartremover-contrib` with `maven` you need to add the following to your build script.

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">

  <properties>
    ...
    <wartremover.contrib>
      ${org.wartremover:wartremover-contrib_2.10:jar}
    </wartremover.contrib>
  </properties>

  <build>
    <plugins>
      ...
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-dependency-plugin</artifactId>
        <version>2.3</version>
        <executions>
          <execution>
            <goals>
              <goal>properties</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
      <plugin>
        <groupId>net.alchim31.maven</groupId>
        <artifactId>scala-maven-plugin</artifactId>
        <version>3.2.1</version>
        <configuration>
          <compilerPlugins>
            <compilerPlugin>
              <groupId>org.wartremover</groupId>
              <artifactId>wartremover_${scala-major.version}</artifactId>
              <version>2.0.0</version>
            </compilerPlugin>
          </compilerPlugins>
          <args>
            <arg>-P:wartremover:cp:file://${wartremover.contrib}</arg>
            <arg>-P:wartremover:only-warn-traverser:org.wartremover.warts.Unsafe</arg>
            <arg>-P:wartremover:only-warn-traverser:org.wartremover.contrib.warts.SealedCaseClass</arg>
          </args>
        </configuration>
      </plugin>
    </plugins>
  </build>

  <dependencies>
    ...
    <dependency>
      <groupId>org.wartremover</groupId>
      <artifactId>wartremover-contrib_${scala-major.version}</artifactId>
      <version>1.0.0</version>
    </dependency>
  </dependencies>

  ...
</project>
```
