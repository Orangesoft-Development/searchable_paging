import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariantOutput
import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("android.extensions")
    id("androidx.navigation.safeargs")
    id("com.github.ben-manes.versions")

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

    dataBinding {
        isEnabled = true
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
    implementation(Depends.BaseAndroid.localBroadcast)
    implementation(Depends.BaseAndroid.fragments)
    implementation(Depends.BaseAndroid.fragmentsKtx)
    implementation(Depends.BaseAndroid.transition)
    implementation(Depends.BaseAndroid.swipeToRefresh)

    implementation(Depends.Coroutines.core)
    implementation(Depends.Coroutines.android)

    implementation(Depends.Navigation.fragment)
    implementation(Depends.Navigation.ui)

    implementation(Depends.Lifecycle.runtime)
    implementation(Depends.Lifecycle.extensions)
    implementation(Depends.Lifecycle.viewModelExtensions)
    kapt(Depends.LifecyclePlugins.plugin)

    implementation(Depends.Api.core)
    implementation(Depends.Api.kotlin)
    implementation(Depends.Api.okhttp)
    implementation(Depends.Api.converter)
    implementation(Depends.Api.adapters)
    kapt(Depends.Api.codegen)
    implementation(Depends.Api.retrofit)
    implementation(Depends.Api.logging)

    implementation(Depends.Image.core)
    implementation(Depends.Image.plugin)
    kapt(Depends.ImagePlugins.compiler)

    implementation(Depends.BindingCollections.core)
    implementation(Depends.BindingCollections.plugin)
    implementation(Depends.BindingCollections.paging)
    implementation(Depends.BindingCollections.viewPager)

    implementation(Depends.Paging.core)

    implementation(Depends.Di.core)
    implementation(Depends.Di.support)
    kapt(Depends.DiPlugins.compiler)
    kapt(Depends.DiPlugins.processor)

    implementation(Depends.Misc.discreteScrollView)

    implementation(Depends.Misc.charts)
    implementation(Depends.Misc.easyPopUps)
    implementation(Depends.Misc.viewPagerIndicator)
    implementation(Depends.Handy.core)
    implementation(Depends.Handy.databinding)
    implementation(Depends.swipeLayout)
    implementation(Depends.pinEntryEditText) {
        exclude("androidx.appcompat", "appcompat")
    }
    implementation(Depends.inputMask)

    implementation(Depends.Worker.worker)
    implementation(Depends.Worker.workerKtx)

    implementation(Depends.Koin.koinAndroid)
    implementation(Depends.Koin.koinAndroidScope)
    implementation(Depends.Koin.koinAndroidViewModel)

    api(Depends.Database.runtime)
    implementation(Depends.Database.ktx)
    kapt(Depends.DatabasePlugin.plugin)

    implementation(Depends.lokalise) {
        isTransitive = true
    }
}

tasks.withType<DependencyUpdatesTask> {

    checkForGradleUpdate = true
    outputFormatter = "json"
    outputDir = "build/dependencyUpdates"
    reportfileName = "report"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}