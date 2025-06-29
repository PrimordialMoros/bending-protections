plugins {
    java
}

subprojects {
    group = "me.moros"
    version = "1.0.0"

    apply(plugin = "java")

    configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }

    repositories {
        mavenCentral() // for bending-api releases
        maven("https://oss.sonatype.org/content/repositories/snapshots/") // for bending-api snapshots
        maven("https://repo.papermc.io/repository/maven-public/") // for bending-api snapshots
    }

    dependencies {
        compileOnly(rootProject.libs.paper)
        compileOnly(rootProject.libs.bending)
        compileOnly(rootProject.libs.configurate)
    }

    tasks {
        withType<JavaCompile> {
            options.compilerArgs.addAll(listOf("-Xlint:unchecked", "-Xlint:deprecation"))
            options.encoding = "UTF-8"
        }
        withType<AbstractArchiveTask> {
            isPreserveFileTimestamps = false
            isReproducibleFileOrder = true
            destinationDirectory = file(rootProject.layout.buildDirectory)
        }
        named<Copy>("processResources") {
            filesMatching("paper-plugin.yml") {
                expand(mapOf("version" to project.version, "mcVersion" to libs.versions.minecraft.get()))
            }
        }
    }
}
