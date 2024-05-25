plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id ("kotlin-parcelize")
    id ("androidx.navigation.safeargs.kotlin")
    id ("dagger.hilt.android.plugin")
    id ("kotlin-kapt")
    id("com.google.gms.google-services")
}

android {
    namespace = "daniel.brian.autoexpress"
    compileSdk = 34

    defaultConfig {
        applicationId = "daniel.brian.autoexpress"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildTypes.forEach {
        it.buildConfigField("String", "CONSUMER_KEY","\"QL7S1jYVxSZNZ8TrUAlImRVqiH3IEvV5p2Wd43P1EKke0ZkL\"" )
        it.buildConfigField("String", "CONSUMER_SECRET", "\"iG66xMbrtE4rVpf2gO8OSbnq03nLjOkyGoj08zs4WPUGEwoxnxmq5pNEfgk45GcU\"")
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //loading button
    implementation("br.com.simplepass:loading-button-android:2.2.0")

    //Glide
    implementation("com.github.bumptech.glide:glide:4.13.0")

    //circular image
    implementation("de.hdodenhof:circleimageview:3.1.0")

    //viewpager2 indicator
    implementation("io.github.vejei.viewpagerindicator:viewpagerindicator:1.0.0-alpha.1")

    //stepView
    implementation ("com.shuhart.stepview:stepview:1.5.1")

    //Android Ktx
    implementation ("androidx.navigation:navigation-fragment-ktx:2.7.7")

    //Dagger hilt
    implementation ("com.google.dagger:hilt-android:2.49")
    kapt("com.google.dagger:hilt-compiler:2.49")

    //Firebase
    implementation (libs.firebase.auth)

    //Navigation
    implementation ("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation ("androidx.navigation:navigation-ui-ktx:2.7.7")

    // coroutine with firebase
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    
    // signing in with google
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    //noinspection UseTomlInstead
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    //Mpesa Stk push dependancies
    implementation ("com.jakewharton:butterknife:10.1.0")
    annotationProcessor("com.jakewharton:butterknife-compiler:10.1.0")
    implementation ("com.jakewharton.timber:timber:5.0.1")

    implementation("cn.pedant.sweetalert:library:1.3")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.okio:okio:3.6.0")
}