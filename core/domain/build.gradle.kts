plugins {
    id("worday.android.library")
}

android {
    namespace = "com.wordayapp.worday.domain"
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("io.mockk:mockk:1.14.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
