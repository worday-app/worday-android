plugins {
    id("worday.android.feature")
}

android {
    namespace = "com.wordayapp.worday.feature.learn"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.common)
    implementation(projects.core.ui)
    implementation(projects.core.data)
}