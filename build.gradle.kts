import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.3.2.RELEASE"
	id("io.spring.dependency-management") version "1.0.9.RELEASE"
	kotlin("jvm") version "1.3.72"
	kotlin("kapt") version "1.3.72"
	kotlin("plugin.spring") version "1.3.72"
	kotlin("plugin.jpa") version "1.3.72"
	kotlin("plugin.allopen") version "1.3.72"
}

group = "kr.bistroad"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
	mavenCentral()
}

dependencies {
	// Kotlin
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	testImplementation("io.kotest:kotest-assertions-core:4.2.2")

	// Spring Boot
	implementation("org.springframework.boot:spring-boot-starter-web")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
		exclude(module = "mockito-core")
	}
	testImplementation("com.ninja-squad:springmockk:2.0.3")

	// Spring Boot JPA
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	runtimeOnly("mysql:mysql-connector-java")
	testRuntimeOnly("com.h2database:h2")

    // Spring Boot Security + JWT
    implementation("org.springframework.boot:spring-boot-starter-security")
	testImplementation("org.springframework.security:spring-security-test")
    api("io.jsonwebtoken:jjwt-api:0.11.2")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.2")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.2")

	// Spring Cloud Kubernetes
	implementation("org.springframework.cloud:spring-cloud-starter-kubernetes:1.1.4.RELEASE")
	implementation("org.springframework.cloud:spring-cloud-starter-kubernetes-config:1.1.4.RELEASE")
	implementation("org.springframework.cloud:spring-cloud-starter-kubernetes-ribbon:1.1.4.RELEASE")

	// Querydsl
	api("com.querydsl:querydsl-jpa:4.3.1")
	kapt("com.querydsl:querydsl-apt:4.3.1:hibernate")

	// Swagger
	implementation("io.springfox:springfox-boot-starter:3.0.0")
}

allOpen {
	annotation("javax.persistence.Entity")
	annotation("javax.persistence.MappedSuperclass")
	annotation("javax.persistence.Embeddable")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}