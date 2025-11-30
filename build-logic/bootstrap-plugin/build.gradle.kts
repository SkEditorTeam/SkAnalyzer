plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        register("bootstrapPlugin") {
            id = "skanalyzer.bootstrap-plugin"
            implementationClass = "me.glicz.skanalyzer.bootstrap.BoostrapPlugin"
        }
    }
}
