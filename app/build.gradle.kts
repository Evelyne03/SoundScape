import com.android.ide.common.repository.pickPluginVariableName
import com.chaquo.python.*

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.chaquo.python")
    id("com.google.gms.google-services")

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

        buildFeatures {
            viewBinding = true
            compose = true
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ndk {
            // On Apple silicon, you can omit x86_64.
            abiFilters += listOf("arm64-v8a", "x86_64")
        }
        vectorDrawables {
            useSupportLibrary = true
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    dexOptions {
        javaMaxHeapSize = "4g"
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
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.android)
    implementation(libs.firebase.database.ktx)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation ("com.google.android.material:material:1.6.0")


    implementation("org.tensorflow:tensorflow-lite:2.15.0")

    implementation ("org.tensorflow:tensorflow-lite:2.10.0")
    implementation ("org.tensorflow:tensorflow-lite-support:0.1.0")

    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    /*implementation("com.google.firebase:firebase-analytics")*/
    implementation("com.google.firebase:firebase-auth"){
    }
    implementation("com.google.firebase:firebase-firestore-ktx")


    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    implementation ("com.github.doyaaaaaken:kotlin-csv-jvm:1.2.0")


   // implementation(files("libs/jvm-25.jar"))

    //tensorflow-lite
    runtimeOnly("org.tensorflow:tensorflow-lite:2.15.0")


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)



    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    implementation ("com.google.android.material:material:1.4.0")
    implementation("androidx.compose.ui:ui:1.0.0")


    //implementation ("android.core:core:1.12.0")


}