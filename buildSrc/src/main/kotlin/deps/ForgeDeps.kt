package deps

object ForgeDeps {
    fun get(minecraft: String): List<VersionedDependency> {
        return buildDependencies{


            modstitchLegacyModImplementation("thedarkcolour:kotlinforforge:4.11.0")

            modstitchLegacyModRuntimeOnly ("top.theillusivec4.curios:curios-forge:5.14.1+1.20.1")
            modstitchModCompileOnly ("top.theillusivec4.curios:curios-forge:5.14.1+1.20.1:api")

            modstitchLegacyModRuntimeOnly ("curse.maven:cloth-config-348521:5729105")
            modstitchLegacyModRuntimeOnly ("curse.maven:crafttweaker-239197:5880672")

            modstitchLegacyModRuntimeOnly("curse.maven:thirst-was-taken-679270:6660408")

            modstitchLegacyModImplementation ("com.tterrag.registrate:Registrate:MC1.20-1.3.11")
            modstitchJiJ ("com.tterrag.registrate:Registrate:MC1.20-1.3.11")

            modstitchLegacyModRuntimeOnly("curse.maven:integrated-dynamics-236307:7786218")
            modstitchLegacyModRuntimeOnly("curse.maven:common-capabilities-247007:7229322")
            modstitchLegacyModRuntimeOnly("curse.maven:cyclops-core-232758:7637992")

            modstitchLegacyModCompileOnly("curse.maven:farmers-delight-398521:7801523")
            modstitchLegacyModRuntimeOnly ("curse.maven:farmers-delight-398521:7801523")
        }
    }
}