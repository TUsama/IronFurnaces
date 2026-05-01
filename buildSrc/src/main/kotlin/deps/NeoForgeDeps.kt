package deps

object NeoForgeDeps {
    fun get(minecraft: String): List<VersionedDependency> {
        return buildDependencies{
            when (minecraft){
                "1.21.1" -> {
                    modstitchModImplementation ("maven.modrinth:curios:9.5.1+1.21.1")
                    //modstitchModImplementation ("com.tterrag.registrate:Registrate:MC1.21-1.3.0+67")

                    "dev.anvilcraft.lib:anvillib-registrum-neoforge-1.21.1:2.0.0+snapshot.316".let {
                        modstitchModCompileOnly(it)
                        modstitchModRuntimeOnly(it)
                        modstitchJiJ (it)
                    }
                    modstitchModCompileOnly ("curse.maven:project-mmo-353935:7623313")

                    "curse.maven:farmers-delight-398521:7801529".let {
                        modstitchModCompileOnly(it)
                        modstitchModRuntimeOnly(it)
                    }

                    if (true){
                        modstitchModRuntimeOnly ("curse.maven:cyclops-core-232758:8006281")
                        modstitchModRuntimeOnly ("curse.maven:common-capabilities-247007:7756804")
                        modstitchModRuntimeOnly ("curse.maven:integrated-dynamics-236307:8006261")
                    }

                }


                "26.1.2" -> {

                    "dev.anvilcraft.lib:anvillib-registrum-neoforge-26.1:2.0.0+snapshot.328".let {
                        modstitchModCompileOnly(it)
                        modstitchModRuntimeOnly(it)
                    }
                }
            }
        }
    }
}