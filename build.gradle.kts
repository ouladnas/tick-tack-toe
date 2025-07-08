val lwjglVersion = "3.3.4"
val jomlVersion = "1.10.8"
val `joml-primitivesVersion` = "1.10.0"
val steamworks4jVersion = "1.9.0"
val lwjglNatives = "natives-macos-arm64"

plugins {
    kotlin("jvm") version "2.0.21"
}

group = "net.haytokiyin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.assertj:assertj-core:3.27.3")
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    // LWJGL Core
    implementation("org.lwjgl:lwjgl")
    implementation("org.lwjgl:lwjgl-glfw")

    // LWJGL System module
    implementation("org.lwjgl:lwjgl")
    runtimeOnly("org.lwjgl:lwjgl:$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-glfw:$lwjglNatives")

    implementation("org.lwjgl", "lwjgl-assimp")
    implementation("org.lwjgl", "lwjgl-openal")
    implementation("org.lwjgl", "lwjgl-opengl")
    implementation("org.lwjgl", "lwjgl-stb")
    implementation ("org.lwjgl", "lwjgl-assimp", classifier = lwjglNatives)
    implementation ("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)
    implementation ("org.lwjgl", "lwjgl-openal", classifier = lwjglNatives)
    implementation ("org.lwjgl", "lwjgl-opengl", classifier = lwjglNatives)
    implementation ("org.lwjgl", "lwjgl-stb", classifier = lwjglNatives)
    implementation("org.joml", "joml", jomlVersion)
    implementation("org.joml", "joml-primitives", `joml-primitivesVersion`)
}

tasks.test {
    useJUnitPlatform()
}