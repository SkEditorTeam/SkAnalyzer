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
