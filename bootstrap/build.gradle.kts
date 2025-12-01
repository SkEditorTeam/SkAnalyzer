plugins {
    id("skanalyzer.common-conventions")
}

tasks.jar {
    manifest.attributes["Main-Class"] = "me.glicz.skanalyzer.bootstrap.Main"
}
