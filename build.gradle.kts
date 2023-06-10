plugins {

    `java-library`
    id("io.papermc.paperweight.userdev") version "1.5.4"
    id("xyz.jpenilla.run-paper") version "2.0.1" // Adds runServer and runMojangMappedServer tasks for testing
    id("net.minecrell.plugin-yml.bukkit") version "0.5.3" // Generates plugin.yml
    id("com.github.johnrengelman.shadow") version "7.1.2"
    `maven-publish`
    signing
}

group = "io.github.kidofcubes"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")

}


java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    withSourcesJar()
    withJavadocJar()
}
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
dependencies {
    paperweight.paperDevBundle("1.19.4-R0.1-SNAPSHOT")
    implementation("net.bytebuddy:byte-buddy:1.14.4")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.7")
}

tasks {
    // Configure reobfJar to run when invoking the build task
    assemble {
        dependsOn(reobfJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(17)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    }


    reobfJar {
        // This is an example of how you might change the output location for reobfJar. It's recommended not to do this
        // for a variety of reasons, however it's asked frequently enough that an example of how to do it is included here.
        outputJar.set(layout.buildDirectory.file("/SSD128GB/ModTestingServer/plugins/RpgPlugin-${project.version}.jar"))
    }

//    jar {
//        from (configurations.compileClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
//        duplicatesStrategy = DuplicatesStrategy.INCLUDE
//        manifest {
//            attributes["Agent-Class"] = "io.github.kidofcubes.rpgplugin.RpgifierAgent"
//            attributes["Can-Redefine-Classes"] = true
//            attributes["Premain-Class"] = "io.github.kidofcubes.rpgplugin.RpgifierAgent"
//            attributes["Can-Retransform-Classes"] = true
//        }
//    }
    shadowJar {

        relocate("net.bytebuddy", "io.github.kidofcubes.dependencies.net.bytebuddy")

        manifest {
            attributes["Agent-Class"] = "io.github.kidofcubes.rpgplugin.RpgifierAgent"
            attributes["Can-Redefine-Classes"] = true
            attributes["Premain-Class"] = "io.github.kidofcubes.rpgplugin.RpgifierAgent"
            attributes["Can-Retransform-Classes"] = true
        }
    }
}

// Configure plugin.yml generation
bukkit {
    load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.STARTUP
    main = "io.github.kidofcubes.rpgplugin.RpgPlugin"
    apiVersion = "1.19"
    authors = listOf("KidOfCubes")
    commands {
        // ...
    }
    softDepend = listOf("WorldGuard")
}
publishing {
    publications {
        create<MavenPublication>("RpgPlugin") {
            from(components["java"])
        }
    }
}


tasks.withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

