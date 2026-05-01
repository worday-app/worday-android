plugins {
    id("worday.android.library")
    id("worday.android.hilt")
}

android {
    namespace = "com.wordayapp.worday.core.data"
}

dependencies {
    implementation(project(":core:domain"))
    // implementation(project(":core:common")) // common modülünü henüz açmadıysan bunu yorumda bırak

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler) // KSP'yi HiltConventionPlugin'den alıyor ama Room için de lazım

    // DataStore
    implementation(libs.datastore.preferences)

    // WorkManager
    implementation(libs.work.runtime.ktx)
    implementation(libs.hilt.work)
    // hilt-compiler zaten hilt plugin içinde ksp ile ekleniyor, tekrar yazmaya gerek yok

    // Tarih/Saat işlemleri için (SM-2 algoritmasında önemli)
    implementation(libs.kotlinx.datetime)
}