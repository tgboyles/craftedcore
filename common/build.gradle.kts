plugins {
    `java-library`
    id("net.neoforged.moddev")
}

val javaVersion = (property("java") as String).toInt()

java {
    toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
    withSourcesJar()
}

neoForge {
    neoFormVersion = property("neoform_version") as String
}

// Expose common sources as consumable artifacts for loader subprojects
val commonJava: Configuration by configurations.creating {
    isCanBeResolved = false
    isCanBeConsumed = true
}
val commonResources: Configuration by configurations.creating {
    isCanBeResolved = false
    isCanBeConsumed = true
}

artifacts {
    add("commonJava", sourceSets.main.get().java.sourceDirectories.singleFile)
    add("commonResources", sourceSets.main.get().resources.sourceDirectories.singleFile)
}

val clothConfigVersion = properties["cloth_config_version"] as String?

repositories {
    maven("https://maven.shedaniel.me/")
}

dependencies {
    compileOnly("org.spongepowered:mixin:0.8.5")
    compileOnly("io.github.llamalad7:mixinextras-common:${property("mixinextras_version")}")
    annotationProcessor("io.github.llamalad7:mixinextras-common:${property("mixinextras_version")}")
    compileOnly("net.fabricmc:fabric-loader:${property("fabric_loader")}")
    if (clothConfigVersion != null) {
        compileOnly("me.shedaniel.cloth:cloth-config:${clothConfigVersion}") {
            exclude("net.fabricmc.fabric-api")
        }
    }
}
