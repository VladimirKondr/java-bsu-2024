plugins {
    id("java")
    id("application")
    id("com.github.sherter.google-java-format") version "0.9"
}

googleJavaFormat {
    toolVersion = "1.9"
}

group = "by.bsu.dependency"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(23))
    }
}

dependencies {
    implementation("org.reflections:reflections:0.10.2")
    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("org.slf4j:slf4j-simple:2.0.7")

    compileOnly("org.projectlombok:lombok:1.18.34")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testImplementation("org.assertj:assertj-core:3.26.3")
    testImplementation("org.junit.platform:junit-platform-suite:1.10.0")
}

application {
    mainClass.set("by.bsu.dependency.example.Main")
}

tasks.test {
    useJUnitPlatform()
}