plugins {
    id("worday.android.library")
    id("worday.android.hilt")
    id("worday.android.library.compose")
}

android {
    namespace = "com.wordayapp.worday.core.ui"
}

dependencies {
    implementation(projects.core.common)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)
}