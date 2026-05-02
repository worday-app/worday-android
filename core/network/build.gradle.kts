import java.util.Properties

plugins {
    id("worday.android.library")
    id("worday.android.hilt")
}

android {
    namespace = "com.wordayapp.worday.core.network"

    defaultConfig {
        val props = Properties().apply {
            val localFile = rootProject.file("local.properties")
            if (localFile.exists()) load(localFile.inputStream())
        }
        buildConfigField(
            "String",
            "GEMINI_API_KEY",
            "\"${props.getProperty("GEMINI_API_KEY", "")}\""
        )
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.data)

    // Retrofit + OkHttp
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)
}