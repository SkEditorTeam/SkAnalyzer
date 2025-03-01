plugins {
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

dependencies {
    compileOnly(project(":skanalyzer-api"))
    compileOnly(project(":MockSkript", "shadow")) {
        exclude("*", "*")
    }
    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")
}

bukkit {
    main = "me.glicz.skanalyzer.bridge.MockSkriptBridgeImpl"
    depend = listOf("Skript")
}
