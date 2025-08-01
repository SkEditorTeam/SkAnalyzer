dependencies {
    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")

    implementation(project(":skanalyzer-api"))
    implementation("net.sf.jopt-simple:jopt-simple:5.0.4")
}

tasks.jar {
    manifest.attributes["Specification-Version"] = version
}
