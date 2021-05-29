plugins {
    kotlin("jvm") version "1.5.10"

    // static code analysis
    id("io.gitlab.arturbosch.detekt") version "1.17.1"
    id("com.diffplug.spotless") version "5.12.5"
}

repositories {
    jcenter()
}

val ktLintVersion = "0.39.0"
allprojects {
    group = "net.mayope.errorcollector"
    version = "0.0.1-SNAPSHOT"

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "1.8"
        }
    }

    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "jacoco")
    apply(plugin = "java")
    spotless {
        kotlin {
            ktlint(ktLintVersion)
        }
        kotlinGradle {
            target("*.gradle.kts", "**/*.gradle.kts")

            ktlint(ktLintVersion)
        }
    }

    tasks.named<Test>("test") {
        useJUnitPlatform()
        group = "test"
        description = "Executes Tests"

        maxHeapSize = "1024m"

        filter {
            isFailOnNoMatchingTests = false
            includeTestsMatching("*Test")
            includeTestsMatching("*IT")
            includeTestsMatching("*ITFull")
        }
    }
}
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    // Kotlin
    implementation(kotlin("stdlib"))

    implementation(kotlin("reflect"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")

    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("ch.qos.logback:logback-core:1.2.3")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("com.fasterxml.jackson.core:jackson-core:2.12.3")

    implementation("io.github.openfeign:feign-core:10.9")
    implementation("io.github.openfeign:feign-jackson:10.9")

    // Utility
    implementation("org.apache.commons:commons-lang3:3.9")

    implementation("org.slf4j:slf4j-api:1.7.30")

    // ExceptionTypes are needed for camunda exception blacklist
    implementation("com.h2database:h2:1.4.200")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
    testImplementation("io.mockk:mockk:1.11.0")
    testImplementation("org.assertj:assertj-core:3.19.0")

    testImplementation("ch.qos.logback:logback-classic:1.2.3")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.0")
}

tasks.named("build") {
    dependsOn(gradle.includedBuild("versions").task(":build"))
}
