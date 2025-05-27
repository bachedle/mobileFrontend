plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("androidx.navigation.safeargs")
    id("kotlin-kapt")
}

android {
    namespace = "com.pokellect.mobilefrontend"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.pokellect.mobilefrontend"
        minSdk = 24
        targetSdk = 35
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

    buildFeatures{

        viewBinding = true

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
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.datastore.core.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation (libs.retrofit)
    implementation (libs.okhttp)
    implementation (libs.logging.interceptor)
    implementation (libs.converter.gson)

    //Coroutine
    implementation (libs.kotlinx.coroutines.android)
    implementation (libs.kotlinx.coroutines.core)
    implementation (libs.gson)

    implementation (libs.androidx.lifecycle.viewmodel.ktx)

    //Glide
    implementation (libs.glide)
    kapt (libs.compiler)
    testImplementation (libs.mockwebserver)

    //retrofit and gson
    implementation(libs.retrofit)
    implementation (libs.gson)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)

    //viewmodel and livedata
        // ViewModel
        implementation(libs.androidx.lifecycle.viewmodel.ktx)
        // ViewModel utilities for Compose
        implementation(libs.androidx.lifecycle.viewmodel.compose)
        // LiveData
        implementation(libs.androidx.lifecycle.livedata.ktx)
        // Lifecycles only (without ViewModel or LiveData)
        implementation(libs.androidx.lifecycle.runtime.ktx)
        // Lifecycle utilities for Compose
        implementation(libs.androidx.lifecycle.runtime.compose)
        // Saved state module for ViewModel
        implementation(libs.androidx.lifecycle.viewmodel.savedstate)


    //navigation fragment
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)


    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.datastore.preferences)

    implementation("com.github.yuyakaido:CardStackView:v2.3.4")

    implementation(libs.koin.android)
    implementation("androidx.viewpager2:viewpager2:1.1.0")



    //camera X
    implementation ("androidx.camera:camera-core:1.4.2")
    implementation ("androidx.camera:camera-camera2:1.4.2")
    implementation ("androidx.camera:camera-lifecycle:1.4.2")
    implementation ("androidx.camera:camera-view:1.4.2")
    implementation ("androidx.camera:camera-video:1.4.2")
    implementation ("androidx.camera:camera-extensions:1.4.2")


    implementation ("androidx.compose.material:material-icons-extended:1.7.8")

    implementation ("org.opencv:opencv:4.9.0")

}