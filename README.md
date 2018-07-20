# jFixer
Java API for the Exchange Rates Fixer.io service
See https://fixer.io/ for documentation about this service


## Getting started
See the wiki pages for usage guidelines:
https://github.com/lico/jFixer/wiki



## Install
You can compile the API in Maven.
This will produce two jar files : with and without the dependencies.

If you don't use the jar with the dependencies, you will have to include them in your project. They are the following:
<!-- HTTP CLIENT -->
		<dependency>
			<groupId>org.apache.httpcomponents</groupId>
			<artifactId>httpclient</artifactId>
			<version>${httpclient.version}</version>
		</dependency>

		<!-- JSON / FASTERXML -->
		<dependency>
			<groupId>com.fasterxml.jackson.core</groupId>
			<artifactId>jackson-annotations</artifactId>
			<version>${fasterxml.version}</version>
		</dependency>
		<dependency>
			<groupId>com.fasterxml.jackson.core</groupId>
			<artifactId>jackson-core</artifactId>
			<version>${fasterxml.version}</version>
		</dependency>

		<!-- APACHE COMMONS -->
		<dependency>
			<groupId>org.apache.commons</groupId>
			<artifactId>commons-lang3</artifactId>
			<version>${commons-lang3.version}</version>
		</dependency>
		<dependency>
			<groupId>org.apache.commons</groupId>
			<artifactId>commons-collections4</artifactId>
			<version>${commons-collections4.version}</version>
		</dependency>

		<!-- TESTS -->
		<dependency>
			<groupId>junit</groupId>
			<artifactId>junit</artifactId>
			<version>${junit.version}</version>
			<scope>test</scope>
			<type>jar</type>
		</dependency>
		<dependency>
			<groupId>com.github.tomakehurst</groupId>
			<artifactId>wiremock</artifactId>
			<version>${wiremock.version}</version>
			<scope>test</scope>
		</dependency>

		<!-- LOG -->
		<!-- Uses Logback and Simple Logging Facade https://www.slf4j.org/ -->
		<dependency>
			<groupId>ch.qos.logback</groupId>
			<artifactId>logback-classic</artifactId>
			<version>${logback.version}</version>
		</dependency>
