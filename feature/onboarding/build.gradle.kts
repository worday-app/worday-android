plugins {
    id("worday.android.feature")
    id("worday.android.library.compose")
}

android {
    namespace = "com.worday.feature.onboarding"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.ui)
    implementation(projects.core.common)
    implementation(projects.core.data)
}