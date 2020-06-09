plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    kotlin("android.extensions")
}

android {
    buildToolsVersion = Versions.Android.buildTools
    compileSdkVersion(Versions.Android.compileSdk)
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    defaultConfig {
        minSdkVersion(Versions.Android.minSdk)
        targetSdkVersion(Versions.Android.targetSdk)
        versionCode = Config.Application.versionCode
        versionName = Config.Application.versionName
    }

    androidExtensions {
        isExperimental = true
    }
}

dependencies {

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(Depends.Kotlin.stdlib)
    implementation(Depends.Kotlin.core)
    implementation(Depends.Kotlin.extensions)

    implementation(Depends.Coroutines.core)
    implementation(Depends.Coroutines.android)

    implementation(Depends.Lifecycle.runtime)
    implementation(Depends.Lifecycle.liveDataExtensions)
    kapt(Depends.LifecyclePlugins.plugin)

    api(Depends.Database.runtime)
    implementation(Depends.Database.ktx)
    kapt(Depends.DatabasePlugin.plugin)

    implementation(Depends.Paging.core)

    implementation(Depends.Api.core)
    implementation(Depends.Api.converter)
    implementation(Depends.Api.kotlin)
    implementation(Depends.Api.adapters)
    kapt(Depends.Api.codegen)
    implementation(Depends.Api.retrofit)
    implementation(Depends.Api.okhttp)
    implementation(Depends.Api.logging)
}
