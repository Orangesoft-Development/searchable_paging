import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariantOutput
import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("android.extensions")

}

kapt {
    correctErrorTypes = true
}

androidExtensions {
    isExperimental = true
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
        applicationId = Config.Application.appId
        minSdkVersion(Versions.Android.minSdk)
        targetSdkVersion(Versions.Android.targetSdk)
        versionCode = Config.Application.versionCode
        versionName = Config.Application.versionName
    }

    lintOptions {
        isCheckReleaseBuilds = false
        isAbortOnError  = false
        setDisable(setOf("GradleCompatible", "FontValidationError"))
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("arm64-v8a")
            isUniversalApk = false
        }
    }

    androidExtensions {
        isExperimental = true
    }

    buildTypes {

        getByName("debug") {
            isDebuggable = true
        }

        getByName("release") {
            isShrinkResources = true
            isMinifyEnabled = true
            isDebuggable = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }

        applicationVariants.all(object : Action<ApplicationVariant> {
            override fun execute(variant: ApplicationVariant) {
                variant.outputs.all(object : Action<BaseVariantOutput> {
                    override fun execute(output: BaseVariantOutput) {
                        val outputImpl = output as BaseVariantOutputImpl
                        val fileName = "${Config.Application.appName}-v${Config.Application.versionName}-${variant.name}.apk"
                        outputImpl.outputFileName = fileName
                    }
                })
            }
        })
    }
}

dependencies {

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(project(":searchablepaging"))

    implementation(Depends.Kotlin.stdlib)
    implementation(Depends.Kotlin.core)
    implementation(Depends.Kotlin.extensions)

    implementation(Depends.BaseAndroid.material)
    implementation(Depends.BaseAndroid.appcompat)
    implementation(Depends.BaseAndroid.constraint)
    implementation(Depends.BaseAndroid.cardview)
    implementation(Depends.BaseAndroid.annotations)
    implementation(Depends.BaseAndroid.fragments)
    implementation(Depends.BaseAndroid.fragmentsKtx)
    implementation(Depends.BaseAndroid.transition)
    implementation(Depends.BaseAndroid.swipeToRefresh)

    implementation(Depends.Coroutines.core)
    implementation(Depends.Coroutines.android)

    implementation(Depends.Api.okhttp)
    implementation(Depends.Api.retrofit)
    implementation(Depends.Api.logging)

    implementation(Depends.Paging.core)

    api(Depends.Database.runtime)
    implementation(Depends.Database.ktx)
    implementation("androidx.navigation:navigation-fragment-ktx:2.1.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.1.0")
    kapt(Depends.DatabasePlugin.plugin)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}