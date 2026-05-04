plugins {
    id("dev.kikugie.stonecutter")
}
stonecutter active "1.21.1-neoforge"
/*
stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) { 
    group = "project"
    ofTask("build")
}
*/
allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://maven.neoforged.net/releases")
        maven("https://maven.fabricmc.net/")
        maven("https://api.modrinth.com/maven")

        maven("https://maven.fzzyhmstrs.me/")

        maven("https://cursemaven.com")
        maven("https://thedarkcolour.github.io/KotlinForForge/")
        maven ("https://jm.gserv.me/repository/maven-public/")
        maven ("https://maven.blamejared.com/")
        maven("https://maven.theillusivec4.top/")
        maven("https://thedarkcolour.github.io/KotlinForForge/")

        maven("https://maven.ithundxr.dev/snapshots")
        maven("https://mvn.devos.one/snapshots/")
        maven("https://maven.tterrag.com/")
    }
}