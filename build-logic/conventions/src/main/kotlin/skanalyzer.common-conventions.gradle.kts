plugins {
    id("net.kyori.indra")
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
}

indra {
    javaVersions {
        target(21)
    }
}
