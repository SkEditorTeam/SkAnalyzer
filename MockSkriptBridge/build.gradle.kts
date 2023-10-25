plugins {
    id("java")
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly(rootProject)
    compileOnly(project(":MockSkript")) {
        exclude("*", "*")
    }
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

    jar.get().archiveVersion = ""
}

bukkit {
    main = "me.glicz.skanalyzer.bridge.MockSkriptBridgeImpl"
}