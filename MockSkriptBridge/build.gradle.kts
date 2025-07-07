plugins {
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

dependencies {
    compileOnly(project(":skanalyzer-api"))
    compileOnly(project(":MockSkript", "shadow")) {
        exclude("*", "*")
    }
}

bukkit {
    main = "me.glicz.skanalyzer.bridge.MockSkriptBridgePlugin"
    depend = listOf("Skript")
}
