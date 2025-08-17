/*
 * Copyright 2020 The Android Open Source Project
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

import org.jetbrains.dokka.gradle.DokkaMultiModuleTask

plugins {
    alias(libs.plugins.spotless)
    alias(libs.plugins.dokka)
    alias(libs.plugins.doctor)
    alias(libs.plugins.dependencyAnalysis)
    alias(libs.plugins.publish) apply false
    alias(libs.plugins.metalava) apply false
    alias(libs.plugins.android.app) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.org.jetbrains.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
}

// https://docs.gradle.org/8.9/userguide/gradle_daemon.html#daemon_jvm_criteria
tasks.updateDaemonJvm.configure {
    languageVersion = JavaLanguageVersion.of(libs.versions.jdk.get())
    vendor = JvmVendorSpec.AZUL
}

// TODO do we need?
tasks.withType(DokkaMultiModuleTask::class).configureEach {
    outputDirectory = rootProject.file("docs/api")
    failOnWarning = true
}

subprojects {
    apply(plugin = "com.diffplug.spotless")
    spotless {
        kotlin {
            target("**/*.kt")
            ktlint(libs.versions.ktlint.get())
            licenseHeaderFile(rootProject.file("spotless/copyright.txt"))
        }

        groovyGradle {
            target("**/*.gradle")
            greclipse().configFile(rootProject.file("spotless/greclipse.properties"))
            licenseHeaderFile(rootProject.file("spotless/copyright.txt"),
                    "(buildscript|apply|import|plugins)")
        }
    }

    // Remove all test apps after running UI tests.
    // This is specially important in CI so that test emulators don"t run out of space.
    tasks.whenTaskAdded {
        if (this.name == "connectedDebugAndroidTest") {
            this.finalizedBy("uninstallDebugAndroidTest")
        }
    }

    configurations.configureEach {
        resolutionStrategy.eachDependency {
            // Make sure that we"re using the Android version of Guava
            if (this.requested.group == "com.google.guava"
                    && this.requested.module.name == "guava"
                    && this.requested.version?.contains("jre") == true) {
                this.useVersion(this.requested.version?.replace("jre", "android").orEmpty())
            }
        }
    }


    // TODO figure out dokka later
    // Must be afterEvaluate or else com.vanniktech.maven.publish will overwrite our
    // dokka and version configuration.
//    afterEvaluate {
//        if (tasks.findByName("dokkaHtmlPartial") == null) {
//            // If dokka isn"t enabled on this module, skip
//            return@afterEvaluate
//        }
//
//        tasks.named<DokkaMultiModuleTask>("dokkaHtmlPartial") {
//            dokkaSourceSets.configureEach {
//                reportUndocumented.set(true)
//                skipEmptyPackages.set(true)
//                skipDeprecated.set(true)
//                jdkVersion.set(17)
//
//                // Add Android SDK packages
//                noAndroidSdkLink.set(false)
//
//                // Add samples from :sample module
//                samples.from(rootProject.file("sample/src/main/java/"))
//
//                // AndroidX + Compose docs
//                externalDocumentationLink {
//                    url.set(new URL("https://developer.android.com/reference/"))
//                    packageListUrl.set(new URL("https://developer.android.com/reference/androidx/package-list"))
//                }
//                externalDocumentationLink {
//                    url.set(new URL("https://developer.android.com/reference/kotlin/"))
//                    packageListUrl.set(new URL("https://developer.android.com/reference/kotlin/androidx/package-list"))
//                }
//
//                sourceLink {
//                    localDirectory.set(project.file("src/main/java"))
//                    // URL showing where the source code can be accessed through the web browser
//                    remoteUrl.set(new URL("https://github.com/google/glance-experimental-tools/blob/main/${project.name}/src/main/java"))
//                    // Suffix which is used to append the line number to the URL. Use #L for GitHub
//                    remoteLineSuffix.set("#L")
//                }
//            }
//        }
//    }
}
