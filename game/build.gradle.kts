// Game module - contains all game code organized by packages
// Packages: client (command, controller, runtime, view), domain (model, utils), service

plugins {
    id("java")
    id("checkstyle")
    id("pmd")
    id("jacoco")
    id("com.diffplug.spotless") version "8.0.0"
}

group = "com.avengers"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    // Gson for JSON serialization
    implementation("com.google.code.gson:gson:2.10.1")

    // JUnit 5 (Jupiter): API + Engine
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    // Mockito
    testImplementation("org.mockito:mockito-core:5.12.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.12.0")
}

tasks.test {
    useJUnitPlatform()
    reports {
        junitXml.required = true
        html.required = true
    }
}

checkstyle {
    toolVersion = "11.0.0"
    configFile = rootProject.file("config/checkstyle/google_checks.xml")
}

tasks.withType<Checkstyle>().configureEach {
    reports {
        xml.required = false
        html.required = true
    }
}

spotless {
    java {
        googleJavaFormat("1.25.0")
        removeUnusedImports()
        target("src/**/*.java")
    }
}

tasks.named("check") {
    dependsOn("spotlessCheck")
}

pmd {
    toolVersion = "6.55.0"
    isConsoleOutput = true
    ruleSets = listOf(
        "category/java/bestpractices.xml",
        "category/java/errorprone.xml",
        "category/java/codestyle.xml"
    )
}

tasks.withType<Pmd>().configureEach {
    reports {
        xml.required = false
        html.required = true
    }
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true
        html.required = true
    }
}

tasks.named("check") {
    dependsOn(tasks.jacocoTestReport)
}


