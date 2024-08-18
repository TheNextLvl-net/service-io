import io.papermc.hangarpublishplugin.model.Platforms
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("java")

    id("io.github.goooler.shadow") version "8.1.8"
    id("io.papermc.hangar-publish-plugin") version "0.1.2"
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
    id("com.modrinth.minotaur") version "2.+"
}

group = rootProject.group
version = rootProject.version

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

tasks.compileJava {
    options.release.set(21)
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.thenextlvl.net/releases")
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    compileOnly("net.thenextlvl.core:annotations:2.0.1")
    compileOnly("org.projectlombok:lombok:1.18.34")

    compileOnly("com.github.ElgarL:groupmanager:3.2")
    compileOnly("net.luckperms:api:5.4")

    implementation("com.github.MilkBowl:VaultAPI:1.7.1")
    implementation("net.thenextlvl.core:paper:1.4.1")
    implementation("org.bstats:bstats-bukkit:3.0.2")

    implementation(rootProject)

    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(platform("org.junit:junit-bom:5.11.0"))
}

tasks.shadowJar {
    relocate("org.bstats", "net.thenextlvl.services.bstats")
    archiveBaseName.set("service-io")
}

tasks.test {
    useJUnitPlatform()
}

paper {
    name = "ServiceIO"
    main = "net.thenextlvl.services.ServicePlugin"
    author = "NonSwag"
    apiVersion = "1.21"
    foliaSupported = true
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP

    website = "https://thenextlvl.net"
    provides = listOf("Vault")

    serverDependencies {
        register("GroupManager") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = false
        }
        register("LuckPerms") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = false
        }
    }
}

val versionString: String = project.version as String
val isRelease: Boolean = !versionString.contains("-pre")

val versions: List<String> = (property("gameVersions") as String)
    .split(",")
    .map { it.trim() }

hangarPublish { // docs - https://docs.papermc.io/misc/hangar-publishing
    publications.register("plugin") {
        id.set("ServiceIO")
        version.set(versionString)
        channel.set(if (isRelease) "Release" else "Snapshot")
        apiKey.set(System.getenv("HANGAR_API_TOKEN"))
        platforms.register(Platforms.PAPER) {
            jar.set(tasks.shadowJar.flatMap { it.archiveFile })
            platformVersions.set(versions)
        }
    }
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("") // todo: create project
    versionType = if (isRelease) "release" else "beta"
    uploadFile.set(tasks.shadowJar)
    gameVersions.set(versions)
    loaders.add("paper")
    loaders.add("folia")
}
