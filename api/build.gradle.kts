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
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    api("com.github.seeseemelk:MockBukkit-v1.20:3.70.0")
    implementation("org.apache.logging.log4j:log4j-core:3.0.0-alpha1")
    implementation("org.slf4j:slf4j-simple:2.0.9")
    implementation("commons-io:commons-io:2.14.0")
    implementation("commons-lang:commons-lang:2.6")
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
        listOf(project(":MockSkript"), project(":MockSkriptBridge")).forEach { addon ->
            dependsOn(addon.tasks.clean)
            val jarTask: DefaultTask =
                addon.tasks.findByName("shadowJar") as DefaultTask? ?: addon.tasks.jar.get()
            dependsOn(jarTask)
            from(jarTask.outputs.files.singleFile) {
                include("*.jar")
                rename { "${addon.name}.jar.embedded" }
            }
        }

        archiveVersion = ""
        archiveClassifier = null
    }
}