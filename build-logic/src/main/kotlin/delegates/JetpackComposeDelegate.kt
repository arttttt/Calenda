package delegates

import extensions.android
import extensions.debugImplementation
import extensions.implementation
import extensions.library
import extensions.libs
import extensions.pluginId
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class JetpackComposeDelegate : PluginDelegate {

    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply(libs.pluginId("kotlin-compose"))

            android {
                buildFeatures {
                    compose = true
                }
            }

            dependencies {
                implementation(platform(libs.library("androidx-compose-bom")))
                implementation(libs.library("androidx-compose-ui"))
                implementation(libs.library("androidx-compose-ui-graphics"))
                implementation(libs.library("androidx-compose-ui-tooling-preview"))
                implementation(libs.library("androidx-compose-material3"))
                debugImplementation(libs.library("androidx-compose-ui-tooling"))
                debugImplementation(libs.library("androidx-compose-ui-test-manifest"))
            }
        }
    }
}