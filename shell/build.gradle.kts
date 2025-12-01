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

bundler {
    bootstrapJar = project(":skanalyzer-bootstrap").tasks.jar
}

tasks {
    jar {
        manifest.attributes["Specification-Version"] = version
    }

    runDev {
        mainClass = "me.glicz.skanalyzer.shell.SkAnalyzerShell"
    }
}
