import org.gradle.kotlin.dsl.compileOnly

plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.gradlePlugin.android)
    compileOnly(libs.gradlePlugin.kotlin)
    compileOnly(libs.gradlePlugin.hilt)
    compileOnly(libs.gradlePlugin.ksp)
    compileOnly(libs.gradlePlugin.compose)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "worday.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "worday.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "worday.android.library.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("androidHilt") {
            id = "worday.android.hilt"
            implementationClass = "HiltConventionPlugin"
        }
        register("androidFeature") {
            id = "worday.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
    }
}