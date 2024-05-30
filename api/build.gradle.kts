plugins {
    id("java-library")
    id("com.github.johnrengelman.shadow")
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    api("com.github.MockBukkit:MockBukkit:v3.88.1")
    implementation("org.apache.logging.log4j:log4j-core:3.0.0-alpha1")
    implementation("org.slf4j:slf4j-simple:2.0.9")
    implementation("commons-io:commons-io:2.14.0")
    implementation("commons-lang:commons-lang:2.6")
}

tasks {
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