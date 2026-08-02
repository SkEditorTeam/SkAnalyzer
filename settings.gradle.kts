pluginManagement {
    includeBuild("build-logic")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "skanalyzer-parent"

fun include(path: String, action: ProjectDescriptor.() -> Unit) {
    include(path)
    project(path).action()
}

listOf(
    "bootstrap",
    "core",
    "shell"
).forEach { module ->
    include(":skanalyzer-$module") {
        projectDir = file(module)
    }
}
