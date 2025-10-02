plugins {
    id("skanalyzer.common-conventions")
    alias(libs.plugins.pluginYml.bukkit)
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
