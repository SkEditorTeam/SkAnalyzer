plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    api("org.mockbukkit.mockbukkit:mockbukkit-v1.21:4.72.5") {
        exclude("net.bytebuddy")
        exclude("org.apache.logging.log4j")
        exclude("org.slf4j")
    }

    api("org.apache.logging.log4j:log4j-to-slf4j:3.0.0-beta2")
    api("ch.qos.logback:logback-classic:1.5.17")
    api("org.slf4j:jul-to-slf4j:2.0.17")
    api("org.fusesource.jansi:jansi:2.4.1")

    api("commons-io:commons-io:2.18.0")
    api("commons-lang:commons-lang:2.6")
    api("org.ow2.asm:asm:9.7.1")
    api("org.jgrapht:jgrapht-core:1.5.2")

    // some deps used by plugins, but not necessarily by analyzer
    runtimeOnly("com.googlecode.json-simple:json-simple:1.1.1") {
        isTransitive = false
    }
}

java {
    withSourcesJar()
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
            from(components["java"])
        }
    }
}
