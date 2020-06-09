buildscript {
    repositories {
        google()
        jcenter()
        maven { url = uri("https://maven.fabric.io/public") }
    }
    dependencies {
        classpath(Depends.BuildPlugins.gradlePlugin)
        classpath(Depends.BuildPlugins.kotlinPlugin)
        classpath(Depends.BuildPlugins.navigationPlugin)
        classpath(Depends.BuildPlugins.fabricPlugin)
        classpath("com.github.ben-manes:gradle-versions-plugin:0.26.0")
        classpath("com.google.gms:google-services:4.3.3")
        classpath("io.fabric.tools:gradle:1.31.2")

    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven { url = uri("https://jitpack.io/") }
        maven { url = uri("https://maven.fabric.io/public") }
        maven { url = uri("https://maven.lokalise.com") }
    }
}
tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
    println("clean")
}