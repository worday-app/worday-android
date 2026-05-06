plugins {
    id("worday.android.feature")
    id("worday.android.library.compose")
    id("worday.android.hilt")
}

android {
    namespace = "com.wordayapp.worday.feature.quiz"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.common)
    implementation(projects.core.ui)
    implementation(projects.core.data)
}