plugins {
    id("me.glicz.skanalyzer.plugin")
}

dependencies {
    runtimeOnly(project(":skanalyzer-app"))

    plugin(project(":MockSkript"))
    plugin(project(":MockSkriptBridge"))
}

tasks {
    jar {
        manifest.attributes["Main-Class"] = "me.glicz.skanalyzer.bootstrap.Main"

        archiveBaseName = rootProject.name
    }

    fun registerRunTask(name: String, configurationAction: Action<in JavaExec>) = register(name, JavaExec::class) {
        group = "skanalyzer"
        doNotTrackState(name)

        standardInput = System.`in`

        if (System.getProperty("idea.active")?.toBoolean() == true) {
            jvmArgs("-Djansi.passthrough=true")
        }

        doFirst {
            workingDir(project.rootDir.resolve("run").apply { mkdirs() })
        }

        configurationAction.execute(this)
    }

    registerRunTask("runDev") {
        mainClass = "me.glicz.skanalyzer.app.SkAnalyzerApp"
        classpath(configurations.runtimeClasspath.get())

        configurations.plugin {
            files.forEach {
                args("--add-plugin=${it.absolutePath}")
            }
        }
        dependsOn(configurations.plugin)

        javaLauncher = project.javaToolchains.launcherFor {
            vendor = JvmVendorSpec.JETBRAINS
            languageVersion = java.toolchain.languageVersion
        }

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
