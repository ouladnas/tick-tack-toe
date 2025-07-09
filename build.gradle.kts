plugins {
    kotlin("jvm") version "2.0.21"
}

group = "net.ouladnas"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        // Disable FIR compiler
        freeCompilerArgs.add("-Xuse-fir=false")
    }
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.assertj:assertj-core:3.27.3")

    implementation(platform("org.lwjgl:lwjgl-bom:3.3.4"))

    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-assimp")
    implementation("org.lwjgl", "lwjgl-glfw")
    implementation("org.lwjgl", "lwjgl-openal")
    implementation("org.lwjgl", "lwjgl-opengl")
    implementation("org.lwjgl", "lwjgl-stb")
    implementation ("org.lwjgl", "lwjgl", classifier = "natives-macos-arm64")
    implementation ("org.lwjgl", "lwjgl-assimp", classifier = "natives-macos-arm64")
    implementation ("org.lwjgl", "lwjgl-glfw", classifier = "natives-macos-arm64")
    implementation ("org.lwjgl", "lwjgl-openal", classifier = "natives-macos-arm64")
    implementation ("org.lwjgl", "lwjgl-opengl", classifier = "natives-macos-arm64")
    implementation ("org.lwjgl", "lwjgl-stb", classifier = "natives-macos-arm64")
    implementation("org.joml", "joml", "1.10.8")

// https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-serialization-json-jvm
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.9.0")
}

tasks.test {
    useJUnitPlatform()
}