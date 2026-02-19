package deps

object NeoForgeDeps {
    fun get(minecraft: String): List<VersionedDependency> {
        return buildDependencies{
            when (minecraft){
                "1.21.1" -> {
                    modstitchModImplementation ("maven.modrinth:curios:9.5.1+1.21.1")
                    modstitchModImplementation ("com.tterrag.registrate:Registrate:MC1.21-1.3.0+67")
                    modstitchJiJ ("com.tterrag.registrate:Registrate:MC1.21-1.3.0+67")

                }

                "1.21.4" -> {

                }
            }
        }
    }
}