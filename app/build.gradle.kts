plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
    implementation(project(":api", "shadow"))
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
        dependsOn(clean)
    }

    shadowJar {
        manifest.attributes["Main-Class"] = "me.glicz.skanalyzer.Main"
        manifest.attributes["Specification-Version"] = version

        archiveBaseName = rootProject.name
        archiveVersion = ""
        archiveClassifier = null
    }
}