plugins {
    id("skanalyzer.publishing-conventions")
}

dependencies {
    api(libs.paper.api) {
        exclude("org.apache.logging.log4j")
        exclude("org.slf4j")
    }
    api(libs.mockbukkit) {
        exclude("net.bytebuddy")
    }

    api(libs.log4j.to.slf4j)
    api(libs.logback.classic)
    api(libs.jul.to.slf4j)
    api(libs.jansi)

    api(libs.commons.io)
    api(libs.commons.lang)
    api(libs.asm)
    api(libs.jgrapht.core)

    // some deps used by plugins, but not necessarily by analyzer
    runtimeOnly(libs.jsonSimple) {
        isTransitive = false
    }
}
