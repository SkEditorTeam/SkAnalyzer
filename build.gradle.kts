plugins {
    id("java")
    id("java-library")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly(project(":MockSkript")) {
        exclude("*", "*")
    }
    api("com.github.seeseemelk:MockBukkit-v1.20:3.9.0")
    implementation("org.apache.logging.log4j:log4j-core:3.0.0-alpha1")
    implementation("commons-io:commons-io:2.14.0")
    implementation("commons-lang:commons-lang:2.6")
    implementation("org.slf4j:slf4j-simple:2.0.9")
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
        manifest.attributes["Main-Class"] = "me.glicz.skanalyzer.SkAnalyzer"
        manifest.attributes["Specification-Version"] = version

        dependsOn(project(":MockSkriptBridge").tasks.jar)

        subprojects.forEach { subproject ->
            from(subproject.tasks.jar.get().outputs.files.singleFile) {
                include("*.jar")
                rename { "${subproject.name}.jar.embedded" }
            }
        }

        archiveVersion = ""
        archiveClassifier = null
    }
}