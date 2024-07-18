plugins {
    id("java-library")
    id("maven-publish")
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    api("com.github.seeseemelk:MockBukkit-v1.20:3.89.0")
    api("org.apache.logging.log4j:log4j-core:3.0.0-alpha1")
    api("org.slf4j:slf4j-simple:2.0.9")
    api("commons-io:commons-io:2.14.0")
    api("commons-lang:commons-lang:2.6")
}

java {
    withSourcesJar()
}

tasks {
    jar {
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
    }
}

publishing {
    repositories {
        val repoType = if (version.toString().endsWith("-SNAPSHOT")) "snapshots" else "releases"
        maven("https://repo.roxymc.net/${repoType}") {
            name = "roxymc"
            credentials(PasswordCredentials::class)
        }
    }
    publications {
        create<MavenPublication>("maven") {
            artifactId = "${rootProject.name.lowercase()}-${project.name.lowercase()}"
            from(components["java"])
        }
    }
}