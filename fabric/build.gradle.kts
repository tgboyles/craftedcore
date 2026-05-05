plugins {
    id("net.fabricmc.fabric-loom")
    `java-library`
}

base {
    archivesName = "${property("archives_base_name")}-fabric"
}

val javaVersion = (property("java") as String).toInt()

java {
    toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
    withSourcesJar()
}

// Resolve common sources from :common subproject
val commonJava: Configuration by configurations.creating { isCanBeResolved = true }
val commonResources: Configuration by configurations.creating { isCanBeResolved = true }

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft")}")
    implementation("net.fabricmc:fabric-loader:${property("fabric_loader")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${property("fabric")}")

    commonJava(project(":common", "commonJava"))
    commonResources(project(":common", "commonResources"))

    // MixinExtras bundled with the mod
    include(implementation(annotationProcessor("io.github.llamalad7:mixinextras-fabric:${property("mixinextras_version")}")!!)!!)

    val modMenuVersion = properties["modmenu_version"] as String?
    if (modMenuVersion != null) {
        compileOnly("com.terraformersmc:modmenu:${modMenuVersion}") { isTransitive = false }
        runtimeOnly("com.terraformersmc:modmenu:${modMenuVersion}") { isTransitive = false }
    }
    val clothConfigVersion = properties["cloth_config_version"] as String?
    if (clothConfigVersion != null) {
        compileOnly("me.shedaniel.cloth:cloth-config-fabric:${clothConfigVersion}") {
            exclude(group = "net.fabricmc.fabric-api")
        }
        runtimeOnly("me.shedaniel.cloth:cloth-config-fabric:${clothConfigVersion}") {
            exclude(group = "net.fabricmc.fabric-api")
        }
    }
}

// Include common sources in this compilation
tasks.compileJava { source(commonJava) }
tasks.javadoc { source(commonJava) }

tasks.processResources {
    from(commonResources)
    val mcVersion = project.property("minecraft")
    val clothVersion = project.property("cloth_config_version")
    filesMatching("fabric.mod.json") {
        expand(mapOf(
            "version" to project.version,
            "minecraft" to mcVersion,
            "clothConfig" to clothVersion
        ))
    }
    outputs.upToDateWhen { false }
}

tasks.named<Jar>("sourcesJar") {
    from(commonJava)
    from(commonResources)
}
