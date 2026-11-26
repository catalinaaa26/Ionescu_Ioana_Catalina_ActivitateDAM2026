plugins {
    alias(libs.plugins.android.library)
}

val secretsFile = rootProject.file("secrets.properties")
val weatherProperties = java.util.Properties()
if (secretsFile.exists()) {
    secretsFile.inputStream().use { weatherProperties.load(it) }
}
val weatherApiKey = weatherProperties.getProperty("ACCUWEATHER_API_KEY", "").trim()
val escapedWeatherApiKey = weatherApiKey
    .replace("\\", "\\\\")
    .replace("\"", "\\\"")

android {
    namespace = "ro.example.weather"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 24
        buildConfigField("String", "ACCUWEATHER_API_KEY", "\"$escapedWeatherApiKey\"")
    }

    buildFeatures {
        buildConfig = true
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.material)
}
