dependencies {
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    implementation(project(":skanalyzer-api"))
}

tasks.jar {
    manifest.attributes["Specification-Version"] = version
}
