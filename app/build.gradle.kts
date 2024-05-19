import com.android.ide.common.repository.pickPluginVariableName
import com.chaquo.python.*

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.chaquo.python")

}

android {
    namespace = "com.example.soundscape"
    compileSdk = 34


    defaultConfig {
        applicationId = "com.example.soundscape"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        chaquopy {
            defaultConfig {
                pip{
                    install("numpy")
                  //  install("scipy")
                    install("tensorflow")
                  //  install("numba")
                    //install ("joblib==0.11.0")
                   // install ("librosa")

                }
            }
            productFlavors { }
            sourceSets { }
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ndk {
            // On Apple silicon, you can omit x86_64.
            abiFilters += listOf("arm64-v8a", "x86_64")
        }

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation ("com.google.code.gson:gson:2.7")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")

    //tensorflow-lite
    runtimeOnly("org.tensorflow:tensorflow-lite:2.15.0")


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //implementation ("android.core:core:1.12.0")


}