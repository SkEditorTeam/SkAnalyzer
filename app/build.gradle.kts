plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
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
        manifest.attributes["Main-Class"] = "me.glicz.skanalyzer.app.SkAnalyzerApp"
        manifest.attributes["Specification-Version"] = version

        archiveBaseName = rootProject.name
        archiveVersion = ""
        archiveClassifier = null
    }
}