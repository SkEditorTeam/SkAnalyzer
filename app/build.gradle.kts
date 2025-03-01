dependencies {
    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")

    implementation(project(":skanalyzer-api"))
}

tasks.jar {
    manifest.attributes["Specification-Version"] = version
}
