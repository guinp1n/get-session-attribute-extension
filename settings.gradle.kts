pluginManagement {
    repositories {
        gradlePluginPortal()
        exclusiveContent {
            forRepository {
                maven {
                    name = "hivemqEnterprise"
                    url = uri("s3://hivemq-enterprise-extension-gradle-plugin/release")
                    credentials(AwsCredentials::class)
                }
            }
            filter {
                includeModule("com.hivemq", "hivemq-enterprise-extension-gradle-plugin")
                includeModule("com.hivemq.enterprise-extension", "com.hivemq.enterprise-extension.gradle.plugin")
            }
        }
    }
}

rootProject.name = "hivemq-hello-world-enterprise-extension"
