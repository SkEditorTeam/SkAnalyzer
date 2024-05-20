plugins {
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

dependencies {
    compileOnly(project(":api"))
    compileOnly(project(":MockSkript", "shadow")) {
        exclude("*", "*")
    }
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
}

bukkit {
    main = "me.glicz.skanalyzer.bridge.MockSkriptBridgeImpl"
    depend = listOf("Skript")
}