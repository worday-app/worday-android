plugins {
    id("worday.android.library")
    id("worday.android.hilt")
}

android {
    namespace = "com.wordayapp.worday.core.network"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.data)

    // Retrofit + OkHttp
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)
}