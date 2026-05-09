import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.library")
            pluginManager.apply("org.jetbrains.kotlin.android")

            pluginManager.withPlugin("com.android.library") {
                extensions.configure<LibraryExtension> {
                    compileSdk = 36
                    defaultConfig { minSdk = 26 }
                    compileOptions {
                        sourceCompatibility = JavaVersion.VERSION_11
                        targetCompatibility = JavaVersion.VERSION_11
                    }
                }
            }

            pluginManager.withPlugin("org.jetbrains.kotlin.android") {
                extensions.configure<KotlinAndroidProjectExtension> {
                    compilerOptions {
                        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
                    }
                }
            }
            dependencies {
                add("implementation", libs.findLibrary("kotlinx.coroutines.core").get())
                add("api", "javax.inject:javax.inject:1")
            }
        }
    }
}