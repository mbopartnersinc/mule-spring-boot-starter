# Mule Spring Boot Starter

Mule CE is an open source integration tool. Mule CE applications are normally run inside a Mule runtime. 
With mule-spring-boot-starter, you can run Mule CE embedded in a Spring Boot application. This allows Mule 
developers to quickly prototype and/or deploy Mule applications with having to download Mule runtime, create 
A Maven artifact and push the artifact to the Mule runtime. This project will allow developers to build the 
Mule application in much the same manner as other Spring Boot applications. 

## Maven Dependency:
To get started simply include the dependency in your pom file:
```
<dependency>
    <groupId>com.mbopartners.boss</groupId>
	<artifactId>mule-spring-boot-starter</artifactId>
	<version>1.0.0</version>
</dependency>
```

## Add repositories:
```

<repositories>
    <repository>
        <id>mulesoft-releases</id>
        <name>MuleSoft Repository</name>
        <url>http://repository.mulesoft.org/releases/</url>
        <layout>default</layout>
    </repository>
</repositories>

```
## Add mule modules and dependencies as needed.

Ex.

```
<dependency>
	<groupId>org.mule.transports</groupId>
	<artifactId>mule-transport-http</artifactId>
	<version>${mule.version}</version>
</dependency>
```

## Create a mule config file:
Make sure this file is in the artifact classpath. Create an application property called
mule.config.files. Add a comma separated list of mule config files.
```
mule.config.files=mule-config.xml
```

## Add annotation to your Spring Boot application entry point.
@EnableMuleContext

```

@EnableMuleContext
@SpringBootApplication
public class Application {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	@Autowired
	private ApplicationContext context;

	public static void main(String... args) {
		logger.info("Starting SpringApplication...");
        SpringApplication.run(Application.class, args);
        logger.info("SpringApplication has started...");
	}
}
```