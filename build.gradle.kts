plugins {
    kotlin("jvm") version "2.2.0-RC"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.17"
}

group = "kr.sobin"
version = "1.0-SNAPSHOT"

val mcVersion = "1.20.1"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://maven.citizensnpcs.co/repo")
    maven("https://repo.dmulloy2.net/nexus/repository/public/")
    maven("https://nexus.hc.to/content/repositories/pub_releases/")
    maven("https://maven.minecraftforge.net/")
    maven("https://jitpack.io")
    maven("https://maven.devs.beer")
}

dependencies {
    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")



    implementation(kotlin("stdlib-jdk8"))

    compileOnly("net.citizensnpcs:citizens-main:2.0.35-SNAPSHOT") {
        exclude(group = "*", module = "*")
    }
    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")
    compileOnly("net.kyori:adventure-api:4.14.0")
    compileOnly("net.kyori:adventure-platform-bukkit:4.3.2")
    compileOnly("net.milkbowl.vault:VaultAPI:1.7")
    compileOnly("dev.lone:api-itemsadder:4.0.10")

    compileOnly("net.minecraftforge:forge:1.20.1-47.1.0") // Mohist Forge 모드 연동 시
}

kotlin {
    jvmToolchain(17)
}

tasks {
    build {
        dependsOn("shadowJar")
    }

    processResources {
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }

    runServer {
        minecraftVersion(mcVersion)
    }
}


