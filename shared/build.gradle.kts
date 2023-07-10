import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithSimulatorTests

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.20"
    id("app.cash.sqldelight")
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("com.akjaw.ai.assistant.database")
            generateAsync.set(false)
        }
    }
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "com.akjaw.ai.assistant.shared.MainView"
    }
}

kotlin {
    val ktorVersion = extra["ktor.version"] as String
    android()

    jvm("desktop")

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        version = "1.0.0"
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
            isStatic = true
        }
        extraSpecAttributes["resources"] = "['src/commonMain/resources/**', 'src/iosMain/resources/**']"
    }

    sourceSets {
        all {
            languageSettings.apply {
                optIn("kotlin.RequiresOptIn")
                optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
            }
        }
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                implementation("com.aallam.openai:openai-client:3.2.0")
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                implementation("io.ktor:ktor-client-json:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                implementation("co.touchlab:kermit:2.0.0-RC4")
                implementation("app.cash.sqldelight:coroutines-extensions:2.0.0-alpha05")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-common:1.8.20")
                implementation("org.jetbrains.kotlin:kotlin-test-annotations-common:1.8.20")
                implementation("io.kotest:kotest-assertions-core:5.6.2")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.2")
                implementation("io.ktor:ktor-client-mock:$ktorVersion")
                implementation("app.cash.turbine:turbine:1.0.0")
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.activity:activity-compose:1.6.1")
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.9.0")
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
                implementation("app.cash.sqldelight:android-driver:2.0.0-alpha05")
                implementation("app.cash.sqldelight:sqlite-driver:2.0.0-alpha05")
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation("io.ktor:ktor-client-darwin:$ktorVersion")
                implementation("app.cash.sqldelight:native-driver:2.0.0-alpha05")
            }
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }

        targets.withType(KotlinNativeTargetWithSimulatorTests::class.java) {
            testRuns["test"].deviceId = "iPhone 14"
        }
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.common)
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
                implementation("app.cash.sqldelight:sqlite-driver:2.0.0-alpha05")
            }
        }
        val desktopTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
            }
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "com.akjaw.ai.assistant.common"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        jvmToolchain(11)
    }
}
