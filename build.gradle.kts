plugins {

    `java-library`
    id("io.papermc.paperweight.userdev") version "1.4.0"
    id("xyz.jpenilla.run-paper") version "2.0.1" // Adds runServer and runMojangMappedServer tasks for testing
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2" // Generates plugin.yml
    `maven-publish`
}

group = "io.github.kidofcubes"
version = "1.1-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")

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
    paperDevBundle("1.19.3-R0.1-SNAPSHOT")
    implementation("net.bytebuddy:byte-buddy:1.12.21")
    implementation("net.bytebuddy:byte-buddy-agent:1.12.21")
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


    reobfJar {
      // This is an example of how you might change the output location for reobfJar. It's recommended not to do this
      // for a variety of reasons, however it's asked frequently enough that an example of how to do it is included here.
      outputJar.set(layout.buildDirectory.file("/HDD2TB/Testing Version Servers/1.19.3Paper/plugins/RpgPlugin-${project.version}.jar"))
    }

    jar {
        from (configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        manifest {
            attributes["Agent-Class"] = "io.github.kidofcubes.RpgifierAgent"
//            attributes["Agent-Class"] = "net.bytebuddy.agent.Installer"
            attributes["Can-Redefine-Classes"] = true
//            attributes["Import-Package"] = "com.sun.tools.attach;resolution:=optional,com.ibm.tools.attach;resolution:=optional"
//            attributes["Require-Capability"] = "osgi.ee;filter:=\"(&(osgi.ee=JavaSE)(version=1.5))\""
//            attributes["Can-Set-Native-Method-Prefix"] = true
//            attributes["Tool"] = "Bnd-6.3.1.202206071316"
//            attributes["Export-Package"] = "net.bytebuddy.agent;uses:=\"net.bytebuddy.agent.utility.nullability\";version=\"1.12.21\",net.bytebuddy.agent.utility.nullability;version=\"1.12.21\""
            attributes["Premain-Class"] = "io.github.kidofcubes.RpgifierAgent"
//            attributes["Premain-Class"] = "net.bytebuddy.agent.Installer"
            attributes["Can-Retransform-Classes"] = true
        }
    }

//    runServer {
//        // Configure the Minecraft version for our task.
//        // This is the only required configuration besides applying the plugin.
//        // Your plugin's jar (or shadowJar if present) will be used automatically.
//        minecraftVersion("1.19.3")
//
//    }
}

// Configure plugin.yml generation
bukkit {
    load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.STARTUP
//    main = "io.github.kidofcubes.Main"
    main = "io.github.kidofcubes.RpgPlugin"
    apiVersion = "1.19"
    authors = listOf("KidOfCubes")
    commands {
        register("testcommand") {
            description = "This is a test command!"
            aliases = listOf("t")
            usage = "Just run the command!"
            // permissionMessage = "You may not test this command!"
        }
        // ...
    }
}
publishing {
    publications {
        create<MavenPublication>("RpgPlugin") {
            from(components["java"])
        }
    }

//    repositories {
//        maven {
//            name = "myRepo"
//            url = uri(layout.buildDirectory.dir("repo"))
//        }
//    }
}
