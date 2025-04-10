plugins {
    id("java-library")
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.jetbrains.kotlin.jvm)
}
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

dependencies{
    implementation(project(":annotation"))
    implementation(libs.kotlinpoet) // Kotlin 代码生成
    implementation(libs.kotlinpoet.ksp)
    implementation(libs.symbol.processing.api)
}