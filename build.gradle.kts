plugins {
    alias(libs.plugins.hivemq.enterpriseExtension)
    alias(libs.plugins.defaults)
}

group = "com.hivemq.extensions"
description = "HiveMQ 4 Hello World Enterprise Extension - a simple reference for all enterprise extension developers"

hivemqExtension {
    name = "Hello World Enterprise Extension"
    author = "HiveMQ"
    priority = 1000
    startPriority = 1000
    mainClass = "$group.helloworld.HelloWorldEnterpriseMain"
    sdkVersion = "$version"
}

dependencies {
    implementation(libs.commons.lang3)
}

@Suppress("UnstableApiUsage")
testing {
    suites {
        withType<JvmTestSuite> {
            useJUnitJupiter(libs.versions.junit.jupiter)
        }
        "test"(JvmTestSuite::class) {
            dependencies {
                implementation(libs.mockito)
            }
        }
        "integrationTest"(JvmTestSuite::class) {
            dependencies {
                compileOnly(libs.jetbrains.annotations)
                implementation(libs.hivemq.mqttClient)
                implementation(libs.testcontainers.junitJupiter)
                implementation(libs.testcontainers.hivemq)
                runtimeOnly(libs.logback.classic)
            }
        }
    }
}

/* ******************** debugging ******************** */

tasks.prepareHivemqHome {
    hivemqHomeDirectory = file("/your/path/to/hivemq-<VERSION>")
}

tasks.runHivemqWithExtension {
    debugOptions {
        enabled = false
    }
}
