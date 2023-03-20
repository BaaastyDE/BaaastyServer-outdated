plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("maven-publish")

    id("org.springframework.boot") version "3.0.3"
    id("io.spring.dependency-management") version "1.1.0"
}

group = "de.baaasty"
version = "1.0.0"

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
    shadowJar {
        archiveFileName.set("${project.name}.jar")
    }
    jar {
        manifest {
            attributes["Main-Class"] = "de.baaasty.baaastyserver.BaaastyServer"
        }
    }
    withType<Test> {
        useJUnitPlatform()
    }
}


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
    maven("https://eldonexus.de/repository/maven-public")
    maven("https://repo.spring.io/snapshot")
    maven("https://repo.spring.io/milestone")
}

dependencies {
    implementation("de.chojo.sadu", "sadu", "1.2.0-DEV")
    implementation("org.mariadb.jdbc", "mariadb-java-client", "3.0.7")
    implementation("com.auth0", "java-jwt", "4.2.1")

    implementation("org.springframework.boot", "spring-boot-starter-web")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")

    implementation("org.yaml", "snakeyaml", "1.33")
}