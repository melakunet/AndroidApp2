plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.melakunet.androidapp2"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.melakunet.androidapp2"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    // JSON encoding/decoding so order history can be saved to SharedPreferences
    implementation("com.google.code.gson:gson:2.11.0")
    // Swipeable pages for the menu screen
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    // Scrolling list used by the History screen
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}