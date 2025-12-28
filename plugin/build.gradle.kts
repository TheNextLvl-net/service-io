import io.papermc.hangarpublishplugin.model.Platforms
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("com.gradleup.shadow") version "9.3.0"
    id("com.modrinth.minotaur") version "2.+"
    id("de.eldoria.plugin-yml.paper") version "0.8.0"
    id("io.papermc.hangar-publish-plugin") version "0.1.4"
}

repositories {
    maven("https://jitpack.io")
    maven("https://maven.citizensnpcs.co/repo")
    maven("https://repo.alessiodp.com/releases")
    maven("https://repo.codemc.org/repository/maven-public")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.fancyplugins.de/releases")
}

dependencies {
    compileOnly("com.github.ElgarL:groupmanager:3.2")
    compileOnly("com.github.decentsoftware-eu:decentholograms:2.9.9")
    compileOnly("de.oliver:FancyNpcs:2.9.0")
    compileOnly("me.clip:placeholderapi:2.11.7")
    compileOnly("net.citizensnpcs:citizens-main:2.0.41-SNAPSHOT")
    compileOnly("net.luckperms:api:5.5")

    implementation("net.thenextlvl:vault-api:1.7.1")
    implementation("net.milkbowl.vault:VaultUnlockedAPI:2.15") {
        exclude("com.github.MilkBowl", "VaultAPI")
    }

    implementation("net.thenextlvl.version-checker:modrinth-paper:1.0.0")
    implementation("net.thenextlvl:i18n:1.1.0")

    implementation("dev.faststats.metrics:bukkit:0.7.2")
    implementation("org.bstats:bstats-bukkit:3.1.0")

    implementation(project(":fancy-holograms-v2"))
    implementation(project(":fancy-holograms-v3"))
    implementation(rootProject)

    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(platform("org.junit:junit-bom:6.1.0-SNAPSHOT"))
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
    bootstrapper = "net.thenextlvl.service.ServiceBootstrapper"
    author = "NonSwag"
    apiVersion = "1.21"
    foliaSupported = true
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP

    website = "https://thenextlvl.net"
    provides = listOf("Vault", "VaultUnlocked")

    serverDependencies {
        register("Citizens") {
            load = PaperPluginDescription.RelativeLoadOrder.AFTER
            required = false
        }
        register("DecentHolograms") {
            load = PaperPluginDescription.RelativeLoadOrder.AFTER
            required = false
        }
        register("FancyHolograms") {
            load = PaperPluginDescription.RelativeLoadOrder.AFTER
            required = false
        }
        register("FancyNpcs") {
            load = PaperPluginDescription.RelativeLoadOrder.AFTER
            required = false
        }
        register("GroupManager") {
            load = PaperPluginDescription.RelativeLoadOrder.AFTER
            required = false
        }
        register("LuckPerms") {
            load = PaperPluginDescription.RelativeLoadOrder.AFTER
            required = false
        }
        register("PlaceholderAPI") {
            load = PaperPluginDescription.RelativeLoadOrder.AFTER
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
        changelog = System.getenv("CHANGELOG")
        channel.set(if (isRelease) "Release" else "Snapshot")
        apiKey.set(System.getenv("HANGAR_API_TOKEN"))
        platforms.register(Platforms.PAPER) {
            jar.set(tasks.shadowJar.flatMap { it.archiveFile })
            platformVersions.set(versions)
            dependencies {
                url("LuckPerms", "https://modrinth.com/project/Vebnzrzj") { required.set(false) }
                url("DecentHolograms", "https://modrinth.com/project/w02MKsTg") { required.set(false) }
                hangar("FancyNpcs") { required.set(false) }
                hangar("FancyHolograms") { required.set(false) }
                hangar("PlaceholderAPI") { required.set(false) }
            }
        }
    }
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("MNPyHOe7")
    changelog = System.getenv("CHANGELOG")
    versionType = if (isRelease) "release" else "beta"
    uploadFile.set(tasks.shadowJar)
    gameVersions.set(versions)
    syncBodyFrom.set(rootProject.file("README.md").readText())
    loaders.add("paper")
    loaders.add("folia")
    dependencies {
        optional.project("luckperms", "decentholograms", "fancynpcs", "fancyholograms", "placeholderapi")
    }
}
