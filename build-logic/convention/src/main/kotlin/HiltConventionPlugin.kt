import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class HiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {

            pluginManager.apply("com.google.devtools.ksp")

            val applyHilt = {
                pluginManager.apply("com.google.dagger.hilt.android")
            }

            pluginManager.withPlugin("com.android.library") {
                applyHilt()
            }

            pluginManager.withPlugin("com.android.application") {
                applyHilt()
            }

            dependencies {
                add("implementation", libs.findLibrary("hilt-android").get())
                add("ksp", libs.findLibrary("hilt-compiler").get())
                add("implementation", libs.findLibrary("hilt-navigation-compose").get())

                add("implementation", libs.findLibrary("hilt-work").get())
                add("implementation", libs.findLibrary("work-runtime-ktx").get())
            }
        }
    }
}