plugins {
    id("skanalyzer.common-conventions")
    id("skanalyzer.bundler-plugin")
}

dependencies {
    implementation(project(":skanalyzer-core"))
    implementation(libs.joptSimple)

    plugin(project(":MockSkript"))
    plugin(project(":MockSkriptBridge"))

    library(project)
}

tasks {
    jar {
        manifest.attributes["Specification-Version"] = version
    }

    bundlerJar {
        from(project(":skanalyzer-bootstrap").tasks.jar.map {
            it.outputs.files.map(::zipTree)
        })
    }

    runDev {
        mainClass = "me.glicz.skanalyzer.shell.SkAnalyzerShell"
    }
}
