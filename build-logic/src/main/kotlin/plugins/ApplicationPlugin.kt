package plugins

import delegates.ApplicationDelegate
import delegates.JetpackComposeDelegate
import delegates.KotlinDelegate
import org.gradle.api.Plugin
import org.gradle.api.Project

class ApplicationPlugin : Plugin<Project> {

    private val delegates = listOf(
        ApplicationDelegate(),
        KotlinDelegate(),
        JetpackComposeDelegate()
    )

    override fun apply(target: Project) {
        delegates.forEach { delegate -> delegate.apply(target) }
    }
}