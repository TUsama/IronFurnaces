import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.withType
import org.gradle.kotlin.dsl.add
import kotlin.text.split


class CommonPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply {
            "java"
            "dev.isxander.modstitch.base"
            "dev.isxander.modstitch.publishing"
        }

        target.tasks.withType<JavaCompile>().configureEach {
            options.encoding = Charsets.UTF_8.name()
        }

        target.dependencies {
            val version = 42
            add("compileOnly", "org.projectlombok:lombok:1.18.${version}")
            add("annotationProcessor", "org.projectlombok:lombok:1.18.${version}")
            add("testCompileOnly", "org.projectlombok:lombok:1.18.${version}")
            add("testAnnotationProcessor", "org.projectlombok:lombok:1.18.${version}")
        }


    }

}