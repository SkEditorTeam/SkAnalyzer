package me.glicz.skanalyzer.bundler

import org.gradle.api.model.ObjectFactory
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.property

abstract class BundlerExtension(
    objects: ObjectFactory
) {
    val bootstrapJar = objects.property<Jar>()
}