plugins {
    id("worday.android.library")
    id("worday.android.hilt")
}

android {
    namespace = "com.wordayapp.worday.core.data"
    sourceSets {
        getByName("main") {
            assets.srcDirs("src/main/assets")
        }
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation(project(":core:domain"))
    implementation(libs.gson)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // DataStore
    implementation(libs.datastore.preferences)

    // WorkManager
    implementation(libs.work.runtime.ktx)
    implementation(libs.hilt.work)

    testImplementation(libs.junit)
}
