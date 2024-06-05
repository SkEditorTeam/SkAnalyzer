plugins {
    id("io.github.goooler.shadow")
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    implementation(project(":api", "shadow"))
}

tasks {
    shadowJar {
        manifest.attributes["Main-Class"] = "me.glicz.skanalyzer.app.SkAnalyzerApp"
        manifest.attributes["Specification-Version"] = version

        archiveBaseName = rootProject.name
        archiveVersion = ""
        archiveClassifier = null
    }
}