package deps

import org.gradle.api.artifacts.ExternalModuleDependency

object DependencyConfig {
    fun getDependencies(platform: String, minecraft: String): List<VersionedDependency> {
        return when (platform) {
            "fabric" -> LoomDeps.get(minecraft)
            "forge" -> ForgeDeps.get(minecraft)
            "neoforge" -> NeoForgeDeps.get(minecraft)
            else -> throw IllegalStateException("Unsupported loader")
        }
    }
}

data class VersionedDependency(
    val configuration: String,
    val notation: String,
    val options: ExternalModuleDependency.() -> Unit = {}
)


