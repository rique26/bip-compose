import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.google.services)
}

dependencyLocking {
    lockAllConfigurations()
}

fun git(vararg args: String): String? = try {
    val process = ProcessBuilder("git", *args)
        .directory(rootProject.projectDir)
        .redirectErrorStream(true)
        .start()
    val output = process.inputStream.bufferedReader().use { it.readText().trim() }
    if (process.waitFor() == 0) output.takeIf { it.isNotBlank() } else null
} catch (_: Exception) { null }

fun semVerToVersionCode(version: String): Int? {
    val match = Regex("""(\d+)\.(\d+)\.(\d+)""").matchEntire(version) ?: return null
    val (major, minor, patch) = match.destructured
    return major.toInt() * 10000 + minor.toInt() * 100 + patch.toInt()
}

val versionProps = Properties().apply {
    load(rootProject.file("version.properties").inputStream())
}
val major = versionProps["MAJOR"].toString().toInt()
val minor = versionProps["MINOR"].toString().toInt()
val patch = versionProps["PATCH"].toString().toInt()

val fallbackVersionName = "$major.$minor.$patch"
val fallbackVersionCode = major * 10000 + minor * 100 + patch

val latestTag = git("describe", "--tags", "--abbrev=0")
val sha = git("rev-parse", "--short", "HEAD") ?: "nogit"
val commitsSinceTag = latestTag?.let { tag ->
    git("rev-list", "$tag..HEAD", "--count")?.toIntOrNull() ?: 0
} ?: 0

val baseTagVersion = latestTag?.removePrefix("v")
val baseVersionName = baseTagVersion ?: fallbackVersionName
val baseVersionCode = baseTagVersion?.let(::semVerToVersionCode) ?: fallbackVersionCode

val versionNameFromGit = when {
    latestTag == null -> "$fallbackVersionName+local.$sha"
    commitsSinceTag == 0 -> baseVersionName
    else -> "$baseVersionName+$commitsSinceTag.$sha"
}

val versionCodeFromGit = baseVersionCode + commitsSinceTag

val localProps = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) load(file.inputStream())
}

fun resolveSecretOrNull(name: String): String? =
    System.getenv(name)?.takeIf { it.isNotBlank() }
        ?: localProps.getProperty(name)?.takeIf { it.isNotBlank() }

fun resolveSecret(name: String): String =
    resolveSecretOrNull(name)
        ?: error("Missing secret: $name — add it to local.properties or set it as an env var.")

android {
    namespace = "com.ebody.bip"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ebody.bip"
        minSdk = 24
        targetSdk = 36
        versionCode = versionCodeFromGit
        versionName = versionNameFromGit

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val storeFilePath = resolveSecretOrNull("RELEASE_SIGNING_STORE_FILE")
            if (storeFilePath != null) {
                storeFile = file(storeFilePath)
                storePassword = resolveSecretOrNull("RELEASE_SIGNING_STORE_PASSWORD") ?: ""
                keyAlias = resolveSecretOrNull("RELEASE_SIGNING_KEY_ALIAS") ?: ""
                keyPassword = resolveSecretOrNull("RELEASE_SIGNING_KEY_PASSWORD") ?: ""
            }
        }
    }

    buildTypes {
        debug {
            manifestPlaceholders["appLabel"] = "Bip (Debug)"
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            isMinifyEnabled = false
        }
        release {
            signingConfig = signingConfigs.getByName("release")
            manifestPlaceholders["appLabel"] = "Bip"
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    defaultConfig {
        multiDexEnabled = true
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // UI e Core (BoM)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui.text.google.fonts)
    implementation(platform(libs.androidx.compose.bom))


    // Compose e Material
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    // Navegação e Hilt
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    // Hilt Core e Compilador
    implementation(libs.dagger.hilt.android)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    implementation(libs.play.services.location)
    ksp(libs.androidx.hilt.compiler)
    ksp(libs.dagger.hilt.compiler)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.database)

    // Coroutines Play Services
    implementation(libs.kotlinx.coroutines.play.services)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Datastore e Security
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.security.crypto)

    // Retrofit e Network
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Lottie Animation Compose
    implementation(libs.lottie.compose)

    // MediaPipe LLM Inference
    implementation(libs.mediapipe.tasks.genai)

    // Desugaring
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // Testes e Debug
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.junit)
    testImplementation(libs.androidx.test.core)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}