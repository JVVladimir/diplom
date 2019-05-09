plugins {
    java
}

group = "ru.hse"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    var junitVersion = "5.4.0"

    implementation("org.scream3r:jssc:2.8.0")

    implementation("com.jfoenix:jfoenix:8.0.8")
   // implementation("com.fazecast:jSerialComm:[2.0.0,3.0.0)")
    implementation ("org.slf4j:slf4j-api:1.7.26")
    implementation ("org.slf4j:slf4j-simple:1.7.26")

    implementation ("com.google.code.gson:gson:2.8.2")

    implementation("com.google.code.gson:gson:2.8.5")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
}

