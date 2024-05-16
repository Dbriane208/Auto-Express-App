@file:Suppress("DEPRECATION")

package daniel.brian.autoexpress.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import daniel.brian.autoexpress.firebase.FirebaseCommon
import daniel.brian.autoexpress.utils.Constants.INTRODUCTION_SP
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseDatabase() = Firebase.firestore

    @Provides
    fun provideIntroductionSP(
        application: Application
    ): SharedPreferences = application.getSharedPreferences(INTRODUCTION_SP, MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideFirebaseCommon(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ) = FirebaseCommon(auth,firestore)

}