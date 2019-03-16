plugins {
    java
}

group = "ru.hse"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    var junitVersion = "5.4.0"
    testImplementation("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
}