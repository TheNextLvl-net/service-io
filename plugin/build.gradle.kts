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
    annotationProcessor("org.projectlombok:lombok:1.18.36")

    compileOnly("io.papermc.paper:paper-api:1.21.3-R0.1-SNAPSHOT")
    compileOnly("org.projectlombok:lombok:1.18.36")

    compileOnly("com.github.ElgarL:groupmanager:3.2")
    compileOnly("net.luckperms:api:5.4")

    implementation("com.github.MilkBowl:VaultAPI:1.7.1")
    implementation("net.thenextlvl.core:paper:1.5.3")
    implementation("org.bstats:bstats-bukkit:3.1.0")

    implementation(rootProject)

    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(platform("org.junit:junit-bom:5.11.3"))
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
    main = "net.thenextlvl.service.ServicePlugin"
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

    permissions {
        register("vault.admin") {
            description = "Backwards compatibility for Vault permission"
            default = BukkitPluginDescription.Permission.Default.OP
            children = listOf("service.admin")
        }
        register("service.admin") {
            description = "Grants access to all /services commands"
            default = BukkitPluginDescription.Permission.Default.OP
            children = listOf("service.info", "service.convert")
        }
        register("service.info") {
            description = "Grants access to /service info"
            default = BukkitPluginDescription.Permission.Default.OP
            children = listOf("service.command")
        }
        register("service.convert") {
            description = "Grants access to /service convert"
            default = BukkitPluginDescription.Permission.Default.OP
            children = listOf("service.command")
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
