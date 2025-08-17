import org.jetbrains.kotlin.gradle.dsl.JvmTarget

/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.dokka)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.publish)
    alias(libs.plugins.metalava)
    alias(libs.plugins.dependencyAnalysis)
}

kotlin {
    explicitApi()
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

android {
    namespace = "com.google.android.glance.tools.viewer"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
        consumerProguardFiles("consumer-rules.pro")
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro",
                ),
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += listOf(
                "/META-INF/AL2.0",
                "/META-INF/LGPL2.1",
            )
        }
    }


    publishing {
        multipleVariants {
            allVariants()
        }
    }
    lint {
        checkReleaseBuilds = false
        baseline = file("lint-baseline.xml")
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))

    api(project(":appwidget-host"))

    compileOnly(libs.glance.appwidget)

    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.ui.ui)
    implementation(libs.compose.material.material3)
    implementation(libs.compose.material.material)
    implementation(libs.compose.material.iconsext)
}

metalava {
    filename = "api/current.api"
    reportLintsAsErrors = true
}
