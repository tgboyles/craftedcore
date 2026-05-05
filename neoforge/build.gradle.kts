plugins {
    id("net.neoforged.moddev")
    `java-library`
}

base {
    archivesName = "${property("archives_base_name")}-neoforge"
}

val javaVersion = (property("java") as String).toInt()

java {
    toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
    withSourcesJar()
}

// Resolve common sources from :common subproject
val commonJava: Configuration by configurations.creating { isCanBeResolved = true }
val commonResources: Configuration by configurations.creating { isCanBeResolved = true }

neoForge {
    version = property("neoforge") as String
}

dependencies {
    commonJava(project(":common", "commonJava"))
    commonResources(project(":common", "commonResources"))

    compileOnly(annotationProcessor("io.github.llamalad7:mixinextras-common:${property("mixinextras_version")}")!!)
    implementation(jarJar("io.github.llamalad7:mixinextras-neoforge:${property("mixinextras_version")}")!!)

    // Needed to compile common sources that use @Environment(EnvType.CLIENT)
    compileOnly("net.fabricmc:fabric-loader:${property("fabric_loader")}")

    val clothConfigVersion = properties["cloth_config_version"] as String?
    if (clothConfigVersion != null) {
        compileOnly("me.shedaniel.cloth:cloth-config-neoforge:${clothConfigVersion}")
        runtimeOnly("me.shedaniel.cloth:cloth-config-neoforge:${clothConfigVersion}")
    }
}

// Include common sources in this compilation
tasks.compileJava { source(commonJava) }
tasks.javadoc { source(commonJava) }

tasks.processResources {
    from(commonResources)
    val mcVersion = project.property("minecraft")
    val clothVersion = project.property("cloth_config_version")
    filesMatching(listOf("META-INF/neoforge.mods.toml", "META-INF/mods.toml")) {
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
