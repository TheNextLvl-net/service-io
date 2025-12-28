plugins {
    id("org.gradle.toolchains.foojay-resolver-convention").version("1.0.0")
}

rootProject.name = "service-io"
include("plugin")

include("providers:citizens")
include("providers:decent-holograms")
include("providers:fancy-holograms-v2")
include("providers:fancy-holograms-v3")
include("providers:fancy-npcs")
include("providers:group-manager")
include("providers:luckperms")
include("providers:superperms")