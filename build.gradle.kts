plugins {
    id("java")
    id("maven-publish")
}

val compileJvm = 21
val toolchainJvm = 25

allprojects {
    apply {
        plugin("java")
        plugin("java-library")
    }

    group = "net.thenextlvl.services"

    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion = JavaLanguageVersion.of(toolchainJvm)
        withSourcesJar()
        withJavadocJar()
    }

    tasks.compileJava {
        options.release.set(compileJvm)
    }

    configurations.compileClasspath {
        attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, toolchainJvm)
    }

    tasks.test {
        dependsOn(tasks.javadoc)
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
            showCauses = true
            showExceptions = true
        }
    }

    repositories {
        mavenCentral()
        maven("https://repo.codemc.org/repository/maven-public")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.thenextlvl.net/releases")
    }

    dependencies {
        compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")
        compileOnly("net.thenextlvl:vault-api:1.7.1")
        compileOnly("net.milkbowl.vault:VaultUnlockedAPI:2.15") {
            exclude("com.github.MilkBowl", "VaultAPI")
            exclude("org.jetbrains", "annotations")
        }
    }
}

subprojects {
    dependencies {
        compileOnly(rootProject)
    }
}

tasks.test {
    dependsOn(tasks.javadoc)
}

tasks.javadoc {
    val options = options as StandardJavadocDocletOptions
    options.tags("apiNote:a:API Note:", "implSpec:a:Implementation Requirements:")
}

publishing {
    publications.create<MavenPublication>("maven") {
        artifactId = "service-io"
        groupId = "net.thenextlvl"
        pom.url.set("https://thenextlvl.net/docs/serviceio")
        pom.scm {
            val repository = "TheNextLvl-net/service-io"
            url.set("https://github.com/$repository")
            connection.set("scm:git:git://github.com/$repository.git")
            developerConnection.set("scm:git:ssh://github.com/$repository.git")
        }
        from(components["java"])
    }
    repositories.maven {
        val branch = if (version.toString().contains("-pre")) "snapshots" else "releases"
        url = uri("https://repo.thenextlvl.net/$branch")
        credentials {
            username = System.getenv("REPOSITORY_USER")
            password = System.getenv("REPOSITORY_TOKEN")
        }
    }
}
