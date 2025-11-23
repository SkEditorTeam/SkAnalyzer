plugins {
    id("skanalyzer.common-conventions")
    id("skanalyzer.bootstrap-plugin")
}

dependencies {
    runtimeOnly(project(":skanalyzer-shell"))

    plugin(project(":MockSkript"))
    plugin(project(":MockSkriptBridge"))
}

tasks {
    jar {
        manifest.attributes["Main-Class"] = "me.glicz.skanalyzer.bootstrap.Main"

        archiveBaseName = rootProject.name
    }

    registerRunTask("runDev") {
        mainClass = "me.glicz.skanalyzer.shell.SkAnalyzerShell"
        classpath(configurations.runtimeClasspath.get())

        configurations.plugin {
            files.forEach {
                args("--add-plugin=${it.absolutePath}")
            }
        }
        dependsOn(configurations.plugin)

        debugOptions {
            suspend = false
            server = true
            port = 5005
            host = "*"
        }

        jvmArgs("-XX:+AllowEnhancedClassRedefinition")
    }

    registerRunTask("runBootstrap") {
        mainClass = "me.glicz.skanalyzer.bootstrap.Main"
        classpath(jar)
    }
}
