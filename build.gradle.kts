import deps.DependencyConfig
import org.gradle.kotlin.dsl.add

plugins {
    id("dev.isxander.modstitch.base") version "0.8.4"
    id("me.modmuss50.mod-publish-plugin") version "1.1.0"
}

val mod_version = property("mod_version") as String
val mod_id = property("mod_id") as String
val minecraft = property("deps.minecraft") as String
val libVersion = findProperty("deps.lib_version") as String?
val minecraftVersionSplit = minecraft.split('.')
fun prop(name: String, consumer: (prop: String) -> Unit) {
    (findProperty(name) as? String?)?.let{
        consumer.invoke(it)
    }
}
fun propLib(consumer: (prop: String) -> Unit){
    prop("deps.lib_version", consumer)
}

var loader: String = name.split("-")[1]

base {
    archivesName.set("Iron Furnace Reburn-$loader-$minecraft")
}
modstitch {
    minecraftVersion = minecraft
    javaVersion = when (minecraft){
        "1.20.1" -> 17
        else -> 21
    }

    // If parchment doesnt exist for a version yet you can safely
    // omit the "deps.parchment" property from your versioned gradle.properties
    parchment {
        prop("deps.parchment") {
            if (minecraft == "1.21.1") minecraftVersion.set("1.21")
            mappingsVersion = it
        }
    }

    // This metadata is used to fill out the information inside
    // the metadata files found in the templates folder.
    metadata {
        modId = mod_id
        modName = property("mod_name") as String
        modVersion = property("mod_version") as String
        modGroup = property("mod_group_id") as String
        modAuthor = property("mod_authors") as String
        modLicense = "Apache License Version 2.0"

        fun <K : Any, V : Any> MapProperty<K, V>.populate(block: MapProperty<K, V>.() -> Unit) {
            block()
        }

        replacementProperties.populate {
            // You can put any other replacement properties/metadata here that
            // modstitch doesn't initially support. Some examples below.
            put("mod_issue_tracker", property("mod_issue") as String)
            put("pack_format", when (property("deps.minecraft")) {
                    "1.20.1" -> 15
                    "1.21.1" -> 34
                    "1.21.4" -> 46
                    "1.21.8" -> 64
                    "1.21.10" -> 69
                    "1.21.11" -> 70.0
                else -> throw IllegalArgumentException("Please store the resource pack version for ${property("deps.minecraft")} in build.gradle.kts! https://minecraft.wiki/w/Pack_format")
            }.toString())

            prop("deps.fzzy_config_version"){
                put("fzzy_config_version", it)
            }

            propLib{
                put("lib_version", it)
                put("common_networking_version", property("deps.common_networking") as String)
            }

            put("target_minecraft", minecraft)
            put(
                "target_loader", when (loader) {
                    "neoforge" -> property("deps.neoforge") as String
                    else -> ""
                }
            )
            put("loader", loader)
            put(
                "target_fabricloader", when (loader) {
                    "fabric" -> property("deps.fabric_loader") as String
                    else -> ""
                }
            )

            put("target_forge", findProperty("deps.forge") as? String ?: "")

        }
    }

    loom {

        fabricLoaderVersion = "0.16.10"
        // Configure loom like normal in this block.
        configureLoom {
            runConfigs.all {
                ideConfigGenerated(false)
            }
            mixin.useLegacyMixinAp = false
        }
    }

    // ModDevGradle (NeoForge, Forge, Forgelike)
    moddevgradle {
        prop("deps.forge") { forgeVersion = it }
        prop("deps.neoforge") { neoForgeVersion = it }
        prop("deps.mcp") { mcpVersion = it }

        configureNeoForge {

            runs {
                configureEach {
                    systemProperty("neoforge.enabledGameTestNamespaces", mod_id)
                    disableIdeRun()
                    jvmArguments.add("-XX:+AllowEnhancedClassRedefinition")
                }
                register("client") {
                    client()
                }
                if(minecraftVersionSplit[2].toInt() >= 4 ){
                    register("clientData") {
                        clientData()
                        programArguments.addAll("--mod", mod_id, "--all", "--output", file("../../src/generated/resources/").getAbsolutePath(), "--existing", file("../../src/main/resources/").getAbsolutePath())
                    }

                    register("serverData") {
                        serverData()
                        programArguments.addAll("--mod", mod_id, "--all", "--output", file("../../src/generated/resources/").getAbsolutePath(), "--existing", file("../../src/main/resources/").getAbsolutePath())
                    }
                } else {
                    register("data") {
                        data()
                        programArguments.addAll("--mod", mod_id, "--all", "--output", file("../../src/generated/resources/").getAbsolutePath(), "--existing", file("../../src/main/resources/").getAbsolutePath())
                    }
                }

                register("server") {
                    server()
                }
                afterEvaluate{
                    this@runs.names.forEach {
                        val capitalizedName = it.replaceFirstChar(Char::uppercaseChar)
                        project.tasks.named<JavaExec>("run$capitalizedName") {
                            val toolchain = project.extensions.getByType<JavaToolchainService>()
                            javaLauncher.set(
                                toolchain.launcherFor {
                                    languageVersion.set(JavaLanguageVersion.of(project.modstitch.javaVersion.get()))
                                    vendor.set(JvmVendorSpec.JETBRAINS)
                                }
                            )
                        }
                    }
                }
            }


            mods {
                register("main") {
                    sourceSet(sourceSets.main.get())
                }
            }


        }

    }

    mixin {
        addMixinsToModManifest = true
        configs.register("ironfurnaces")
    }
}


// Stonecutter constants for mod loaders.
// See https://stonecutter.kikugie.dev/stonecutter/guide/comments#condition-constants

stonecutter {
    constants.putAll(mapOf<String, Boolean>(
        "fabric" to loader.equals("fabric"),
        "neoforge" to loader.equals("neoforge"),
        "forge" to loader.equals("forge"),
        "vanilla" to loader.equals("vanilla")

    ))
    replacements.string(current.version >= "1.20.1" && loader.equals("neoforge")) {
        replace("ForgeConfigSpec", "ModConfigSpec")
        replace("net.minecraftforge", "net.neoforged.neoforge")
    }
}

sourceSets["main"].resources.srcDir("../../src/generated/resources")

dependencies {
    fun Dependency?.jij() = this?.also(::modstitchJiJ)
    fun String.implementation() = if (modstitch.isModDevGradleLegacy){
        //avoid the modstitch remap bug on 1.20.1
        add("modImplementation", this)
    } else {
        modstitchModImplementation(this)
    }
    fun String.runtimeOnly() = if (modstitch.isModDevGradleLegacy) {
        add("modRuntimeOnly", this)
    } else {
        modstitchModRuntimeOnly(this)
    }
    fun String.compileOnly() = if (modstitch.isModDevGradleLegacy) {
        add("modCompileOnly", this)
    } else {
        modstitchModCompileOnly(this)
    }



    prop("deps.fzzy_config_version"){
        val fzzyConfigVersion = findProperty("deps.fzzy_config_version")
        val fzzyMinecraftVersion = when (minecraft) {
            "1.21.1" -> "1.21"
            "1.21.4" -> "1.21.3"
            "1.21.8" -> "1.21.6"
            "1.21.10" -> "1.21.9"
            else -> minecraft
        }
        var fzzyString : String = "";

        modstitch.loom {
            fzzyString = "me.fzzyhmstrs:fzzy_config:${fzzyConfigVersion}+${fzzyMinecraftVersion}";

        }

        modstitch.moddevgradle {
            if (modstitch.isModDevGradleLegacy){
                fzzyString = "me.fzzyhmstrs:fzzy_config:${fzzyConfigVersion}+${fzzyMinecraftVersion}+forge";
            } else {
                if (minecraft == "1.21.8"){
                    fzzyString = "me.fzzyhmstrs:fzzy_config:${fzzyConfigVersion}+1.21.7+neoforge";
                } else {
                    fzzyString = "me.fzzyhmstrs:fzzy_config:${fzzyConfigVersion}+${fzzyMinecraftVersion}+neoforge"
                }

            }

        }

        modstitchModCompileOnly(fzzyString)
        (fzzyString).runtimeOnly()
    }

    modstitch.loom{
    prop("deps.fabric_api"){
        ("net.fabricmc.fabric-api:fabric-api:$it+${minecraft}").implementation()
    }
    }

    propLib{
        "maven.modrinth:nirvana-library:$loader-$minecraft-$it".implementation()
        "mysticdrew:common-networking-$loader:${property("deps.common_networking") as String}".runtimeOnly()
        modstitchModCompileOnly ("mysticdrew:common-networking-$loader:${property("deps.common_networking") as String}")
    }


    modstitchModCompileOnly ("curse.maven:project-mmo-353935:5075049")
    //("curse.maven:project-mmo-353935:5075049").runtimeOnly()

    prop("deps.jei"){
        modstitchModCompileOnly("mezz.jei:jei-${minecraft}-${loader}-api:${it}")
        // at runtime, use the full JEI jar for NeoForge
        ("mezz.jei:jei-${minecraft}-${loader}:${it}").runtimeOnly()
    }

    DependencyConfig.getDependencies(loader, minecraft).forEach { dep ->
        dependencies.add(dep.configuration, dep.notation, dep.options)
    }

    //lombok
    modstitchCompileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")

    testCompileOnly("org.projectlombok:lombok:1.18.42")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.42")


}

publishMods {

    dryRun.set(true)

    afterEvaluate {
        file = modstitch.finalJarTask.flatMap { it.archiveFile }
        this@publishMods.displayName.set(file.map { it.asFile.name })
    }


    changelog = file("../../changelog.md")
        .takeIf { it.exists() }
        ?.readLines()
        ?.joinToString("\n") { line ->
            if (line.isNotBlank()) {
                "$line</br>"
            } else {
                line
            }
        }
        ?: ""
    type = BETA
    modLoaders.add(loader)



    curseforge {
        accessToken = file("D:\\curseforge-key.txt").readText()
        projectId = "1497798"
        minecraftVersions.add(minecraft)
        serverRequired = true
        clientRequired = true
        javaVersions.set(listOf<JavaVersion>(JavaVersion.VERSION_17))
        requires("nirvana-library")

    }

}