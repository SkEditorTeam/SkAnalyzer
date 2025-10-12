plugins {
    id("skanalyzer.common-conventions")
}

dependencies {
    implementation(project(":skanalyzer-core"))
    implementation(libs.joptSimple)
}

tasks.jar {
    manifest.attributes["Specification-Version"] = version
}
