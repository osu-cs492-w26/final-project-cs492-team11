plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.navigation.safeargs)
}

android {
    namespace = "edu.oregonstate.cs492.assignmentfinal"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "edu.oregonstate.cs492.assignmentfinal"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        /*
         * The following line adds the OpenWeather API key into the app's string resources under
         * the name "openweather_api_key".  In other words, this will make the API key available
         * in the Kotlin code via `R.string.openweather_api_key`.
         *
         * To make this work, you should follow the instructions in the comment at the top of
         * MainActivity.kt.
         */
        resValue("string", "openweather_api_key", properties["OPENWEATHER_API_KEY"]?.toString() ?: "")
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.coordinatorlayout)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.preference.ktx)

    implementation(libs.retrofit2.retrofit)
    implementation(libs.retrofit2.convertermoshi)
    implementation(libs.moshi)
    implementation(libs.glide)

    implementation(libs.kotlinx.coroutines.android)

    ksp(libs.moshi.codegen)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}