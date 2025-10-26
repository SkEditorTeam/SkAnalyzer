import org.gradle.api.Action
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskContainer
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.register

fun TaskContainer.registerRunTask(
    name: String,
    configurationAction: Action<in JavaExec>
) = register(name, JavaExec::class) {
    group = "skanalyzer"
    doNotTrackState(name)

    standardInput = System.`in`

    if (System.getProperty("idea.active")?.toBoolean() == true) {
        jvmArgs("-Djansi.passthrough=true")
    }

    doFirst {
        workingDir(project.rootDir.resolve("run").apply { mkdirs() })
    }

    val java: JavaPluginExtension by project.extensions
    val javaToolchains: JavaToolchainService by project.extensions

    javaLauncher.set(javaToolchains.launcherFor {
        vendor.set(JvmVendorSpec.JETBRAINS)
        languageVersion.set(java.toolchain.languageVersion)
    })

    configurationAction.execute(this)
}
