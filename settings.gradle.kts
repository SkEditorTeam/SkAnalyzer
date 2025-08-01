plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "SkAnalyzer"

listOf(
    "api",
    "app"
).forEach {
    val name = "${rootProject.name}-$it".lowercase()

    include(name)
    project(":$name").projectDir = file(it)
}

include(
    "bootstrap",
    "MockSkript",
    "MockSkriptBridge"
)
