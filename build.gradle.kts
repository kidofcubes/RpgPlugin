plugins {

    `java-library`
    id("io.papermc.paperweight.userdev") version "1.3.7"
}

group = "io.github.kidofcubes"
version = "v1.1"

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
