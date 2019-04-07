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
    implementation("com.fazecast:jSerialComm:[2.0.0,3.0.0)")
    implementation ("org.slf4j:slf4j-api:1.7.26")
    implementation ("org.slf4j:slf4j-simple:1.7.26")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
}