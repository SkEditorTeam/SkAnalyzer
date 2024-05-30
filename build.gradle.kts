plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
}

configure(subprojects.filter { it.name != "MockSkript" }) {
    plugins.apply("java")

    repositories {
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    java {
        toolchain.languageVersion = JavaLanguageVersion.of(17)
    }

    tasks {
        withType<JavaCompile> {
            options.encoding = Charsets.UTF_8.name()
            options.release = 17
            dependsOn(clean)
        }
    }
}