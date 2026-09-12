plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.littleapp.wordpress"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.littleapp.wordpress"
        minSdk = 24
        targetSdk = 37
        versionCode = 2
        versionName = "1.0.1"

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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.preference.ktx)           //Shared Preference
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //Layout
    implementation(libs.material)
    //Image
    implementation(libs.coil)                          //Coil Image
    //Other's
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.androidx.swiperefreshlayout)
}