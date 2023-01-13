plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("maven-publish")
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
}

dependencies {
    implementation("de.chojo.sadu", "sadu", "1.2.0")
    implementation("org.mariadb.jdbc", "mariadb-java-client", "3.0.7")
    implementation("com.auth0", "java-jwt", "4.2.1")

    implementation("io.javalin", "javalin-bundle", "5.3.0")

    implementation("org.slf4j", "slf4j-simple", "2.0.6")
    implementation("org.slf4j", "slf4j-api", "2.0.6")
}