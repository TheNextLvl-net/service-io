plugins {
    id("java")
    id("maven-publish")
}

val javaVersion = 21

allprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")

    group = "net.thenextlvl.services"

    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
        withSourcesJar()
        withJavadocJar()
    }

    tasks.compileJava {
        options.release.set(javaVersion)
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
        maven("https://repo.thenextlvl.net/releases")
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    dependencies {
        compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
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
