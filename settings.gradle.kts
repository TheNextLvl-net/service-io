plugins {
    id("org.gradle.toolchains.foojay-resolver-convention").version("1.0.0")
}

rootProject.name = "service-io"
include("plugin")

include("fancy-holograms-v2")
include("fancy-holograms-v3")