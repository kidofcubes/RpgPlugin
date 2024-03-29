plugins {

    `java-library`
    id("io.papermc.paperweight.userdev") version "1.4.0"
    id("xyz.jpenilla.run-paper") version "2.0.1" // Adds runServer and runMojangMappedServer tasks for testing
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2" // Generates plugin.yml
}

group = "io.github.kidofcubes"
version = "1.1-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")

}

dependencies {
    paperDevBundle("1.19-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
dependencies {
    paperDevBundle("1.19.3-R0.1-SNAPSHOT")
    // paperweightDevBundle("com.example.paperfork", "1.19.3-R0.1-SNAPSHOT")

    // You will need to manually specify the full dependency if using the groovy gradle dsl
    // (paperDevBundle and paperweightDevBundle functions do not work in groovy)
    // paperweightDevelopmentBundle("io.papermc.paper:dev-bundle:1.19.3-R0.1-SNAPSHOT")
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

    /*
    reobfJar {
      // This is an example of how you might change the output location for reobfJar. It's recommended not to do this
      // for a variety of reasons, however it's asked frequently enough that an example of how to do it is included here.
      outputJar.set(layout.buildDirectory.file("libs/PaperweightTestPlugin-${project.version}.jar"))
    }
     */
}

// Configure plugin.yml generation
bukkit {
    load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.STARTUP
    main = "io.github.kidofcubes.RpgPlugin"
    apiVersion = "1.19"
    authors = listOf("KidOfCubes")
}