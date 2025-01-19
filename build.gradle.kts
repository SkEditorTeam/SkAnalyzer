plugins {
    id("java")
    id("io.github.goooler.shadow") version "8.1.7" apply false
}

configure(subprojects.filter { it.name != "MockSkript" }) {
    plugins.apply("java")

    repositories {
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    java {
        toolchain.languageVersion = JavaLanguageVersion.of(21)
    }

    tasks {
        withType<JavaCompile> {
            options.encoding = Charsets.UTF_8.name()
            options.release = 21
            dependsOn(clean)
        }
    }
}
