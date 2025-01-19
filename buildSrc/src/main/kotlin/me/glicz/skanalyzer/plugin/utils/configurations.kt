package me.glicz.skanalyzer.plugin.utils

import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.kotlin.dsl.get

val ConfigurationContainer.plugin: Configuration
    get() = get("plugin")

val ConfigurationContainer.runtimeClasspath: Configuration
    get() = get("runtimeClasspath")
