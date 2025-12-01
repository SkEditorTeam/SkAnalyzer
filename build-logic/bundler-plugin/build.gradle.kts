plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        register("bundlerPlugin") {
            id = "skanalyzer.bundler-plugin"
            implementationClass = "me.glicz.skanalyzer.bundler.BundlerPlugin"
        }
    }
}
