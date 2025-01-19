plugins {
    id("java")
    id("me.glicz.skanalyzer.plugin")
}

dependencies {
    runtimeOnly(project(":skanalyzer-app"))

    plugin(project(":MockSkript"))
    plugin(project(":MockSkriptBridge"))
}

tasks{
    jar {
        manifest.attributes["Main-Class"] = "me.glicz.skanalyzer.bootstrap.Main"

        archiveBaseName = rootProject.name
    }

    register("runBootstrap", JavaExec::class) {
        group = "skanalyzer"
        doNotTrackState("Run bootstrap")

        mainClass = "me.glicz.skanalyzer.bootstrap.Main"
        classpath(jar)

        standardInput = System.`in`

        doFirst {
            workingDir(project.rootDir.resolve("run").apply { mkdirs() })
        }
    }
}
