plugins {
    id("net.kyori.indra")
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io") {
        content {
            includeModule("com.github.MockBukkit", "MockBukkit")
        }
    }
}

indra {
    javaVersions {
        target(21)
    }
}
