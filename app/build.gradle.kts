plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.stisbanksoal"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.stisbanksoal"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    // 1. NAVIGASI (Pindah Layar)
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // 2. RETROFIT (Komunikasi ke Backend Spring Boot)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Untuk ubah JSON ke Data Class

    // 3. LOGGING (Buat ngintip data request/response di Logcat)
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // 4. DATASTORE (Simpan Token Login pengganti SharedPrefs)
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // 5. COIL (Load Gambar Soal dari URL)
    implementation("io.coil-kt:coil-compose:2.6.0")

    // 6. ICONS (Icon tambahan seperti tombol Back, Add, Profile)
    implementation("androidx.compose.material:material-icons-extended:1.6.3")

}