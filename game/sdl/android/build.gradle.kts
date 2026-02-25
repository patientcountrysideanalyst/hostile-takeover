plugins {
    id("com.android.application") version "8.5.2"
    kotlin("android") version "1.9.24"
}

android {
    namespace = "com.spiffcode.ht"
    compileSdk = 35
    ndkVersion = "21.4.7075529"

    defaultConfig {
        applicationId = "com.spiffcode.ht"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.63"
    }

    sourceSets {
        getByName("main") {
            manifest.srcFile("AndroidManifest.xml")
            java.srcDirs("src")
            res.srcDirs("res")
            assets.srcDirs("assets")
            jniLibs.srcDirs("libs")
        }
    }

    externalNativeBuild {
        ndkBuild {
            path = file("jni/Android.mk")
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-project.txt"
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

    lint {
        abortOnError = false
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(kotlin("stdlib"))
}
